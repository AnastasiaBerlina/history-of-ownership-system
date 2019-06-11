package com.anastasia.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.andrc.states.CarrierEventState;

import java.util.List;

@Data
@NoArgsConstructor
public class CarrierEventStateDto extends BaseStateDto {

    private String event;
    private List<String> participants;

    public CarrierEventStateDto(CarrierEventState carrierState) {
        super(carrierState.getDate());
        this.event = carrierState.getEventContract().name();
    }
}
