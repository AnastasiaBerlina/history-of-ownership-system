package com.anastasia.project.controller;

import com.anastasia.project.configs.GenericModelMapper;
import com.anastasia.project.dto.BaseStateDto;
import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.PutContainerStateDto;
import com.anastasia.project.service.ConsumeInfo;
import com.anastasia.project.service.ProduceInfo;
import com.anastasia.project.webDto.CarrierEventStateWebDto;
import com.anastasia.project.webDto.ChangeCarrierStateWebDto;
import com.anastasia.project.webDto.DeleteContainerStateWebDto;
import com.anastasia.project.webDto.HistoryDto;
import com.anastasia.project.webDto.PutContainerStateWebDto;
import com.anastasia.project.webDto.StatisticsWebDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController {

    private final ProduceInfo produceInfo;
    private final ConsumeInfo consumeInfo;


    @Autowired
    public HistoryController(ProduceInfo produceInfo, ConsumeInfo consumeInfo) {
        this.produceInfo = produceInfo;
        this.consumeInfo = consumeInfo;
    }

    @GetMapping
    public void getMessages() {
        produceInfo.produceInfo();
    }

    @GetMapping("/carrier")
    public List<StatisticsWebDto> getCarrierStatistics(@RequestParam String name) {
        return GenericModelMapper.convertList(consumeInfo.produceCarrierStatistics(name), StatisticsWebDto.class);
    }

    @GetMapping("/container")
    public List<? super HistoryDto> getHistoryForSpecificItem(@RequestParam String name) {
        List<BaseStateDto> stateDtos = consumeInfo.produceSpecificHistory(name);

        List<? super HistoryDto> history = new ArrayList<>();
        for (BaseStateDto stateDto : stateDtos) {
            if (stateDto.getClass().equals(CarrierEventStateDto.class)) {
                CarrierEventStateWebDto carrierEvent = GenericModelMapper.convertToClass(stateDto, CarrierEventStateWebDto.class);
                carrierEvent.setTypeOfAction("event");
                history.add(carrierEvent);
            }
            if (stateDto.getClass().equals(ChangeCarrierStateDto.class)) {
                ChangeCarrierStateWebDto changeCarrier = GenericModelMapper.convertToClass(stateDto, ChangeCarrierStateWebDto.class);
                changeCarrier.setTypeOfAction("changeCarrier");
                history.add(changeCarrier);
            }
            if (stateDto.getClass().equals(PutContainerStateDto.class)) {
                PutContainerStateWebDto putContainer = GenericModelMapper.convertToClass(stateDto, PutContainerStateWebDto.class);
                putContainer.setTypeOfAction("putContainer");
                history.add(putContainer);
            }
            if (stateDto.getClass().equals(DeleteContainerStateDto.class)) {
                DeleteContainerStateWebDto deleteContainer = GenericModelMapper.convertToClass(stateDto, DeleteContainerStateWebDto.class);
                deleteContainer.setTypeOfAction("deleteContainer");
                history.add(deleteContainer);
            }

        }
        return history;
    }

}
