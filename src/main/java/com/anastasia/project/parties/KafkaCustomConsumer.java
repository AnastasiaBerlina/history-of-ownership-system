package com.anastasia.project.parties;


import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.OfficerAuthenticationResponseStateDto;
import com.anastasia.project.dto.PutContainerStateDto;
import com.anastasia.project.serializr.KafkaJSONDeserializer;
import net.andrc.states.OfficerAuthenticationRequestState;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class KafkaCustomConsumer {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    private static final String KAFKA_SERVER_URL = "localhost";
    private static final int KAFKA_SERVER_PORT = 9092;
    private static final String CLIENT_ID = "SampleConsumer";

    public KafkaCustomConsumer() {}

    public KafkaConsumer<String, CarrierEventStateDto> getCarrierEventConsumer() {
        Properties props = getProperties();
        return new KafkaConsumer<>(props);
    }

    public KafkaConsumer<String, ChangeCarrierStateDto> getCarrierChangeConsumer() {
        Properties props = getProperties();
        return new KafkaConsumer<>(props);
    }

    public KafkaConsumer<String, PutContainerStateDto> putContanerConsumer() {
        Properties props = getProperties();
        return new KafkaConsumer<>(props);
    }

    public KafkaConsumer<String, DeleteContainerStateDto> deleteContainerEventConsumer() {
        Properties props = getProperties();
        return new KafkaConsumer<>(props);
    }

    public KafkaConsumer<String, OfficerAuthenticationRequestState> officerRequestConsumer() {
        Properties props = getProperties();
        return new KafkaConsumer<>(props);
    }

    public KafkaConsumer<String, OfficerAuthenticationResponseStateDto> officerResponseConsumer() {
        Properties props = getProperties();
        return new KafkaConsumer<>(props);
    }


    @NotNull
    private Properties getProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_URL + ":" + KAFKA_SERVER_PORT);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CLIENT_ID);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJSONDeserializer.class);
        return props;
    }

    public void subscribe(KafkaConsumer consumer, List<String> topics) {
        consumer.subscribe(topics);
    }


}