package com.anastasia.project.service;

import com.anastasia.project.client.NodeRPCConnection;
import com.anastasia.project.configs.CordaNodeConfig;
import com.anastasia.project.dto.BaseStateDto;
import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.OfficerAuthenticationRequestStateDto;
import com.anastasia.project.dto.OfficerAuthenticationResponseStateDto;
import com.anastasia.project.dto.PutContainerStateDto;
import com.anastasia.project.parties.KafkaCustomProducer;
import lombok.RequiredArgsConstructor;
import net.andrc.states.CarrierEventState;
import net.andrc.states.ChangeCarrierState;
import net.andrc.states.DeleteContainerState;
import net.andrc.states.OfficerAuthenticationRequestState;
import net.andrc.states.OfficerAuthenticationResponseState;
import net.andrc.states.PutContainerState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ProduceInfo {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private final KafkaCustomProducer kafkaCustomProducer;

    private final CordaNodeConfig cordaNodeConfig;

    public void produceInfo() {
        cordaNodeConfig.getConnections().forEach((connection -> {
            executorService.submit(() -> kafkaCustomProducer.collectInfoFromNodes(connection.getProxy(), CarrierEventState.class));
            executorService.submit(() -> kafkaCustomProducer.collectInfoFromNodes(connection.getProxy(), ChangeCarrierState.class));
            executorService.submit(() -> kafkaCustomProducer.collectInfoFromNodes(connection.getProxy(), OfficerAuthenticationResponseState.class));
            executorService.submit(() -> kafkaCustomProducer.collectInfoFromNodes(connection.getProxy(), OfficerAuthenticationRequestState.class));
            executorService.submit(() -> kafkaCustomProducer.collectInfoFromNodes(connection.getProxy(), PutContainerState.class));
            executorService.submit(() -> kafkaCustomProducer.collectInfoFromNodes(connection.getProxy(), DeleteContainerState.class));
        }));

        try {
            executorService.awaitTermination(100000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
