package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.items.Events;
import net.andrc.states.CarrierEventState;

import java.util.Date;

@Data
public class CarrierEventStateDto {

    private Events eventContract;
    private Date date;


    public CarrierEventStateDto (CarrierEventState carrierState) {
        this.eventContract = carrierState.getEventContract();
        this.date = carrierState.getDate();

    }
}
