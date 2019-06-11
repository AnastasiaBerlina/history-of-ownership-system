package com.anastasia.project.webDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import net.andrc.items.GeoData;

import java.util.Date;

@Getter
@Setter
public class StatisticsWebDto {

    private GeoData from;
    private GeoData to;

    @JsonProperty("title")
    private String eventType;

    @JsonProperty("date")
    private Date eventDate;

    @JsonProperty("extraordinaryEvents")
    private boolean badCarrier;

}
