package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.states.CarrierEventState;

@Data
public class CarrierEventStateDto extends BaseStateDto {

    private String event;

    public CarrierEventStateDto(CarrierEventState carrierState) {
        super(carrierState.getDate());
        this.event = carrierState.getEventContract().name();


    }
}
