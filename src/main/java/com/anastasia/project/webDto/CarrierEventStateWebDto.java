package com.anastasia.project.webDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.andrc.items.Events;

@Data
@Setter
@Getter
public class CarrierEventStateWebDto extends HistoryDto {

    @JsonProperty("type")
    private String typeOfAction;

    @JsonProperty("events")
    private String event;


}
