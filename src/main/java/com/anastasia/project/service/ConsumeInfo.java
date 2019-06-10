package com.anastasia.project.service;

import com.anastasia.project.client.NodeRPCConnection;
import com.anastasia.project.configs.CordaNodeConfig;
import com.anastasia.project.dto.BaseStateDto;
import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.PutContainerStateDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.andrc.states.CarrierEventState;
import net.andrc.states.ChangeCarrierState;
import net.andrc.states.DeleteContainerState;
import net.andrc.states.PutContainerState;
import net.corda.core.contracts.StateAndRef;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ConsumeInfo {

    private final CordaNodeConfig cordaNodeConfig;

    public List<BaseStateDto> produceSpecificHistory(String name) {

        List<NodeRPCConnection> nodeRPCConnections = new ArrayList<>();

        for (NodeRPCConnection node : cordaNodeConfig.getConnections()) {
            System.out.println("----------------------------------------");
            List<StateAndRef<PutContainerState>> states = node.getProxy().vaultQuery(PutContainerState.class)
                    .getStates();
            List<StateAndRef<DeleteContainerState>> deleteStates = node.getProxy().vaultQuery(DeleteContainerState.class)
                    .getStates();
            for (StateAndRef<PutContainerState> state : states) {
                System.out.println(state);
                if (state.getState().getData().getContainerName().equals(name)) {
                    nodeRPCConnections.add(node);
                }
            }
        }
        NodeRPCConnection nodeRPCConnection;
        if (nodeRPCConnections.size() > 1) {
            nodeRPCConnection = nodeRPCConnections.stream()
                    .filter(node -> node.getRpcPort() != (cordaNodeConfig.getRoot()))
                    .findFirst().orElseThrow(() -> new NotFoundException("Node not found"));
        } else {
            nodeRPCConnection = nodeRPCConnections.get(0);
        }

        List<BaseStateDto> stateList = new ArrayList<>();

        NodeRPCConnection nodeRPCConnection1 = cordaNodeConfig.getConnections()
                .stream()
                .filter(node -> node.getRpcPort() == (cordaNodeConfig.getRoot()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Node not found"));

        stateList.addAll(nodeRPCConnection1.getProxy()
                .vaultQuery(CarrierEventState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new CarrierEventStateDto(carrierEventState.getState().getData()))
                .collect(Collectors.toList()));

        stateList.addAll(nodeRPCConnection.getProxy()
                .vaultQuery(ChangeCarrierState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new ChangeCarrierStateDto(carrierEventState.getState().getData()))
                .collect(Collectors.toList()));

        stateList.addAll(nodeRPCConnection.getProxy()
                .vaultQuery(DeleteContainerState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new DeleteContainerStateDto(carrierEventState.getState().getData()))
                .filter(deleteContainerStateDto -> deleteContainerStateDto.getContainerName().equals(name))
                .collect(Collectors.toList()));

        stateList.addAll(nodeRPCConnection.getProxy()
                .vaultQuery(PutContainerState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new PutContainerStateDto(carrierEventState.getState().getData()))
                .filter(putContainerStateDto -> putContainerStateDto.getContainerName().equals(name))
                .collect(Collectors.toList()));


        stateList.sort(Comparator.comparing(BaseStateDto::getDate));

        BaseStateDto putDto = stateList.stream()
                .filter(baseStateDto -> baseStateDto.getClass().equals(PutContainerStateDto.class))
                .findFirst().orElseThrow(() -> new NotFoundException("Container wasn't put"));
        BaseStateDto deleteDto = stateList.stream()
                .filter(baseStateDto -> baseStateDto.getClass().equals(DeleteContainerStateDto.class))
                .findFirst().orElse(new BaseStateDto(new Date()));

        return stateList.stream()
                .filter(baseStateDto -> baseStateDto.getDate()
                        .after(putDto.getDate()) || baseStateDto.getDate().equals(putDto.getDate()))
                .filter(baseStateDto -> baseStateDto.getDate()
                        .before(deleteDto.getDate()) || baseStateDto.getDate().equals(deleteDto.getDate()))
                .collect(Collectors.toList());
    }

}
