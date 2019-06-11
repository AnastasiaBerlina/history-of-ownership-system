package com.anastasia.project.model;

import com.anastasia.project.dto.CarrierEventStateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.andrc.items.GeoData;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class History {
    private GeoData from;
    private GeoData to;
    private int extraordinaryEvent;
    private boolean badCarrier;
}
