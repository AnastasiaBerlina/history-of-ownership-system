package com.anastasia.project.service;

import com.anastasia.project.client.NodeRPCConnection;
import com.anastasia.project.configs.CordaNodeConfig;
import com.anastasia.project.dto.BaseStateDto;
import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.PutContainerStateDto;
import com.anastasia.project.model.History;
import lombok.RequiredArgsConstructor;
import net.andrc.states.CarrierEventState;
import net.andrc.states.ChangeCarrierState;
import net.andrc.states.DeleteContainerState;
import net.andrc.states.PutContainerState;
import net.corda.core.contracts.StateAndRef;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ConsumeInfo {

    private final CordaNodeConfig cordaNodeConfig;

    Map<String, List<History>> statisticsMap;

    public List<BaseStateDto> produceSpecificHistory(String name) {

        List<NodeRPCConnection> nodeRPCConnections = new ArrayList<>();

        for (NodeRPCConnection node : cordaNodeConfig.getConnections()) {
            List<StateAndRef<PutContainerState>> states = node.getProxy().vaultQuery(PutContainerState.class)
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

        NodeRPCConnection nodeRoot = cordaNodeConfig.getConnections()
                .stream()
                .filter(node -> node.getRpcPort() == (cordaNodeConfig.getRoot()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Node not found"));

        produceWayEvents(nodeRoot, stateList);
        produceCarrierChange(nodeRPCConnection, stateList);
        produceDeleteContainer(nodeRPCConnection, stateList, name);
        producePutContainer(nodeRPCConnection, stateList, name);

        stateList.sort(Comparator.comparing(BaseStateDto::getDate));
        return filter(stateList);
    }

    private List<BaseStateDto> filter(List<BaseStateDto> stateList) {
        BaseStateDto[] dtos = produceDates(stateList);
        return stateList.stream()
                .filter(baseStateDto -> baseStateDto.getDate()
                        .after(dtos[0].getDate()) || baseStateDto.getDate().equals(dtos[0].getDate()))
                .filter(baseStateDto -> baseStateDto.getDate()
                        .before(dtos[1].getDate()) || baseStateDto.getDate().equals(dtos[1].getDate()))
                .collect(Collectors.toList());
    }

    private BaseStateDto[] produceDates(List<BaseStateDto> stateList) {
        BaseStateDto[] dtos = new BaseStateDto[2];
        dtos[0] = stateList.stream()
                .filter(baseStateDto -> baseStateDto.getClass().equals(PutContainerStateDto.class))
                .findFirst().orElseThrow(() -> new NotFoundException("Container wasn't put"));
        dtos[1] = stateList.stream()
                .filter(baseStateDto -> baseStateDto.getClass().equals(DeleteContainerStateDto.class))
                .findFirst().orElse(new BaseStateDto(new Date()));
        return dtos;
    }

    private void produceWayEvents(NodeRPCConnection nodeRPCConnection, List<BaseStateDto> stateList) {
        stateList.addAll(nodeRPCConnection.getProxy()
                .vaultQuery(CarrierEventState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new CarrierEventStateDto(carrierEventState.getState().getData()))
                .collect(Collectors.toList()));
    }

    private void produceCarrierChange(NodeRPCConnection nodeRPCConnection, List<BaseStateDto> stateList) {
        stateList.addAll(nodeRPCConnection.getProxy()
                .vaultQuery(ChangeCarrierState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new ChangeCarrierStateDto(carrierEventState.getState().getData()))
                .collect(Collectors.toList()));
    }

    private void producePutContainer(NodeRPCConnection nodeRPCConnection, List<BaseStateDto> stateList,
                                     String name) {
        stateList.addAll(nodeRPCConnection.getProxy()
                .vaultQuery(PutContainerState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new PutContainerStateDto(carrierEventState.getState().getData()))
                .filter(putContainerStateDto -> putContainerStateDto.getContainerName().equals(name))
                .collect(Collectors.toList()));
    }

    private void produceDeleteContainer(NodeRPCConnection nodeRPCConnection, List<BaseStateDto> stateList,
                                        String name) {
        stateList.addAll(nodeRPCConnection.getProxy()
                .vaultQuery(DeleteContainerState.class)
                .getStates()
                .stream()
                .map(carrierEventState -> new DeleteContainerStateDto(carrierEventState.getState().getData()))
                .filter(deleteContainerStateDto -> deleteContainerStateDto.getContainerName().equals(name))
                .collect(Collectors.toList()));
    }

    private void produceStatisticsMap() {
        statisticsMap = new HashMap<>();
        List<BaseStateDto> stateList = new ArrayList<>();
        for (NodeRPCConnection nodeRPCConnection : cordaNodeConfig.getConnections()) {
            produceWayEvents(nodeRPCConnection, stateList);
            produceCarrierChange(nodeRPCConnection, stateList);
            stateList.sort(Comparator.comparing(BaseStateDto::getDate));
            ChangeCarrierStateDto changedCarrier = null;
            List<CarrierEventStateDto> carrierEvent = new ArrayList<>();
            for (BaseStateDto state : stateList) {
                if (state.getClass().equals(ChangeCarrierStateDto.class)) {
                    if (changedCarrier != null) {
                        if (!statisticsMap.containsKey(changedCarrier.getCarrierName())) {
                            statisticsMap.put(changedCarrier.getCarrierName(),
                                    Arrays.asList(new History(changedCarrier.getGeoData(),
                                            ((ChangeCarrierStateDto) state).getGeoData(),
                                            carrierEvent.size(), false)));
                        } else {
                            statisticsMap.get(changedCarrier.getCarrierName()).
                                    add(new History(changedCarrier.getGeoData(), ((ChangeCarrierStateDto) state).getGeoData(),
                                            carrierEvent.size(), false));
                        }

                    }
                    changedCarrier = (ChangeCarrierStateDto) state;
                    carrierEvent.clear();

                }
                if (state.getClass().equals(CarrierEventStateDto.class)) {
                    carrierEvent.add((CarrierEventStateDto) state);
                }
            }
            stateList.clear();
        }
        fillInIfCarrierIsFaithFull();

    }

    private void fillInIfCarrierIsFaithFull() {
        Set<String> keySet = statisticsMap.keySet();
        for (String key : keySet) {
            for (History history : statisticsMap.get(key)) {
                int allCarriersTheSameWay = 0;
                int allEventsSameWay = 0;
                for (String otherKey : keySet) {
                    if (otherKey.equals(key)) break;
                    for (History otherHistory : statisticsMap.get(otherKey)) {
                        if (otherHistory.getFrom().equals(history.getFrom()) &&
                                otherHistory.getTo().equals(history.getTo())) {
                            allCarriersTheSameWay++;
                            allEventsSameWay += otherHistory.getExtraordinaryEvent();
                        }
                    }
                }
                if (allCarriersTheSameWay == 0) {
                    if (history.getExtraordinaryEvent() > 0) {
                        history.setBadCarrier(true);
                    } else history.setBadCarrier(false);
                } else {
                    double stats = Math.round(allEventsSameWay / allCarriersTheSameWay);
                    if (history.getExtraordinaryEvent() > stats) {
                    history.setBadCarrier(true);
                    } else {
                        history.setBadCarrier(false);
                    }
                }
            }
        }
    }

    public List<History> produceCarrierStatistics(String name) {
        if (statisticsMap.isEmpty()) {
            produceStatisticsMap();
        }
       return statisticsMap.get(name);
    }

}
