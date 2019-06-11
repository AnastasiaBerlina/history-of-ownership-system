package com.anastasia.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.andrc.items.GeoData;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class Statistics {
    private String carrierName;
    private GeoData from;
    private GeoData to;
    private String eventType;
    private Date eventDate;
    private int extraordinaryEventCnt;
    private boolean badCarrier;
}
