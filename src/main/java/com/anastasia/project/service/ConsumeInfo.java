package com.anastasia.project.service;

import com.anastasia.project.client.NodeRPCConnection;
import com.anastasia.project.configs.CordaNodeConfig;
import com.anastasia.project.dto.BaseStateDto;
import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.PutContainerStateDto;
import com.anastasia.project.model.Statistics;
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

    Map<String, List<Statistics>> statisticsMap;

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
                        String eventType = "";
                        Date eventDate = new Date(1081157732);
                        if (!carrierEvent.isEmpty()) {
                            eventType = carrierEvent.get(carrierEvent.size() - 1).getEvent();
                            eventDate = carrierEvent.get(carrierEvent.size() - 1).getDate();
                        }
                        if (!statisticsMap.containsKey(changedCarrier.getCarrierName())) {
                            List<Statistics> list = new ArrayList<>();
                            list.add(new Statistics(changedCarrier.getCarrierName(),changedCarrier.getGeoData(),
                                    ((ChangeCarrierStateDto) state).getGeoData(),
                                    eventType, eventDate,
                                    carrierEvent.size(), false));
                            statisticsMap.put(changedCarrier.getCarrierName(),
                                    list);
                        } else {
                            statisticsMap.get(changedCarrier.getCarrierName()).
                                    add(new Statistics(changedCarrier.getCarrierName(),
                                            changedCarrier.getGeoData(), ((ChangeCarrierStateDto) state).getGeoData(),
                                            eventType, eventDate,
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
            for (Statistics statistics : statisticsMap.get(key)) {
                int allCarriersTheSameWay = 0;
                int allEventsSameWay = 0;
                for (String otherKey : keySet) {
                    if (otherKey.equals(key)) break;
                    for (Statistics otherStatistics : statisticsMap.get(otherKey)) {
                        if (otherStatistics.getFrom().equals(statistics.getFrom()) &&
                                otherStatistics.getTo().equals(statistics.getTo())) {
                            allCarriersTheSameWay++;
                            allEventsSameWay += otherStatistics.getExtraordinaryEventCnt();
                        }
                    }
                }
                if (allCarriersTheSameWay == 0) {
                    if (statistics.getExtraordinaryEventCnt() > 0) {
                        statistics.setBadCarrier(true);
                    } else statistics.setBadCarrier(false);
                } else {
                    double stats = Math.round(allEventsSameWay / allCarriersTheSameWay);
                    if (statistics.getExtraordinaryEventCnt() > stats) {
                        statistics.setBadCarrier(true);
                    } else {
                        statistics.setBadCarrier(false);
                    }
                }
            }
        }
    }

    public List<Statistics> produceCarrierStatistics(String name) {
        if (statisticsMap==null) {
            produceStatisticsMap();
        }
        return statisticsMap.get(name);
    }

}
