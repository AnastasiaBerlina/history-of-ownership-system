package com.anastasia.project.parties;

import com.anastasia.project.configs.CordaNodeConfig;
import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.OfficerAuthenticationRequestStateDto;
import com.anastasia.project.dto.OfficerAuthenticationResponseStateDto;
import com.anastasia.project.serializr.KafkaJSONSerialize;
import com.anastasia.project.dto.PutContainerStateDto;
import net.andrc.states.CarrierEventState;
import net.andrc.states.ChangeCarrierState;
import net.andrc.states.DeleteContainerState;
import net.andrc.states.OfficerAuthenticationRequestState;
import net.andrc.states.OfficerAuthenticationResponseState;
import net.andrc.states.PutContainerState;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class KafkaCustomProducer {

    private CordaNodeConfig cordaNodeConfig;

    @Autowired
    public KafkaCustomProducer(CordaNodeConfig cordaNodeConfig) {
        this.cordaNodeConfig = cordaNodeConfig;
    }

    public void collectInfoFromNodes(CordaRPCOps cordaRPCOps, Class<? extends ContractState> clazz) {
        List<? extends StateAndRef<? extends ContractState>> states = cordaRPCOps.vaultQuery(clazz).getStates();

        Map<String, ? extends ContractState> collect = states
                .stream()
                .collect(Collectors
                        .toMap(e -> e.getRef().getTxhash().toString(),
                                e -> e.getState().getData()));
        if (clazz.equals(ChangeCarrierState.class)) {
            collect.forEach((k, v) -> sendChangeCarrier(kafkaProducerCarrierChangeState(),
                    new ProducerRecord<>("changeCarrierStates", k,
                            new ChangeCarrierStateDto((ChangeCarrierState) v))));
        }
        if (clazz.equals(CarrierEventState.class)) {
            collect.forEach((k, v) -> sendCarrierEvent(kafkaProducerCarrierEventState(),
                    new ProducerRecord<>("CarrierEventStates", k,
                            new CarrierEventStateDto((CarrierEventState) v))));
        }
        if (clazz.equals(OfficerAuthenticationRequestState.class)) {
            collect.forEach((k, v) -> sendAuthenticationRequestState(kafkaProducerAuthenticationRequestState(),
                    new ProducerRecord<>("AuthRequestStates", k,
                            new OfficerAuthenticationRequestStateDto((OfficerAuthenticationRequestState) v))));
        }
        if (clazz.equals(OfficerAuthenticationResponseState.class)) {
            collect.forEach((k, v) -> sendAuthenticationResponseState(kafkaProducerAuthenticationResponse(),
                    new ProducerRecord<>("AuthResponseStates", k,
                            new OfficerAuthenticationResponseStateDto((OfficerAuthenticationResponseState) v))));
        }
        if (clazz.equals(PutContainerState.class)) {
            collect.forEach((k, v) -> sendPutContainerState(kafkaPutContainerState(),
                    new ProducerRecord<>("PutContainerStates", k,
                            new PutContainerStateDto((PutContainerState) v))));
        }
        if (clazz.equals(DeleteContainerState.class)) {
            collect.forEach((k, v) -> sendDeleteContainer(kafkaDeleteContainerState(),
                    new ProducerRecord<>("DeleteContainerStates", k,
                            new DeleteContainerStateDto((DeleteContainerState) v))));
        }

    }

    public static KafkaProducer<String, ChangeCarrierStateDto> kafkaProducerCarrierChangeState() {
        Map<String, Object> map = getProducerMap();
        return new KafkaProducer<>(map);
    }

    public static KafkaProducer<String, CarrierEventStateDto> kafkaProducerCarrierEventState() {
        Map<String, Object> map = getProducerMap();
        return new KafkaProducer<>(map);
    }

    public static KafkaProducer<String, OfficerAuthenticationRequestStateDto> kafkaProducerAuthenticationRequestState() {
        Map<String, Object> map = getProducerMap();
        return new KafkaProducer<>(map);
    }

    public static KafkaProducer<String, OfficerAuthenticationResponseStateDto> kafkaProducerAuthenticationResponse() {
        Map<String, Object> map = getProducerMap();
        return new KafkaProducer<>(map);
    }

    public static KafkaProducer<String, PutContainerStateDto> kafkaPutContainerState() {
        Map<String, Object> map = getProducerMap();
        return new KafkaProducer<>(map);
    }

    public static KafkaProducer<String, DeleteContainerStateDto> kafkaDeleteContainerState() {
        Map<String, Object> map = getProducerMap();
        return new KafkaProducer<>(map);
    }

    @NotNull
    private static Map<String, Object> getProducerMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("bootstrap.servers", "127.0.0.1:9092");
        map.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        map.put("value.serializer", KafkaJSONSerialize.class);
        return map;
    }

    public static void sendChangeCarrier(KafkaProducer<String, ChangeCarrierStateDto> producer,
                                         ProducerRecord<String, ChangeCarrierStateDto> record) {
        try {
            producer.send(record, new ProducerCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendCarrierEvent(KafkaProducer<String, CarrierEventStateDto> producer,
                                        ProducerRecord<String, CarrierEventStateDto> record) {
        try {
            producer.send(record, new ProducerCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendDeleteContainer(KafkaProducer<String, DeleteContainerStateDto> producer,
                                           ProducerRecord<String, DeleteContainerStateDto> record) {
        try {
            producer.send(record, new ProducerCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendAuthenticationRequestState(KafkaProducer<String, OfficerAuthenticationRequestStateDto> producer,
                                                      ProducerRecord<String, OfficerAuthenticationRequestStateDto> record) {
        try {
            producer.send(record, new ProducerCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendAuthenticationResponseState(KafkaProducer<String, OfficerAuthenticationResponseStateDto> producer,
                                                       ProducerRecord<String, OfficerAuthenticationResponseStateDto> record) {
        try {
            producer.send(record, new ProducerCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPutContainerState(KafkaProducer<String, PutContainerStateDto> producer,
                                             ProducerRecord<String, PutContainerStateDto> record) {
        try {
            producer.send(record, new ProducerCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class ProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                e.printStackTrace();
            } else {
                System.out.println("sent on : " + recordMetadata.topic() + " offset : " + recordMetadata.offset());
            }

        }
    }
}
