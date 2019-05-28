package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.items.Carrier;
import net.andrc.items.GeoData;
import net.andrc.states.ChangeCarrierState;

import java.util.Date;

@Data
public class ChangeCarrierStateDto {
    private Carrier carrier;
    private String data;
    private String signature;
    private GeoData geoData;
    private Date date;

    public ChangeCarrierStateDto(ChangeCarrierState carrierState) {
        this.carrier = carrierState.getCarrier();
        this.data = carrierState.getData();
        this.date = carrierState.getDate();
        this.signature = carrierState.getSignature();
        this.geoData = carrierState.getGeoData();
    }

}
