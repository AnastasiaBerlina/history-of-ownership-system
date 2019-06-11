package com.anastasia.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.andrc.items.GeoData;
import net.andrc.states.ChangeCarrierState;

@Data
@NoArgsConstructor
public class ChangeCarrierStateDto extends BaseStateDto {
    private String carrierName;
    private String data;
    private GeoData geoData;


    public ChangeCarrierStateDto(ChangeCarrierState carrierState) {
        super(carrierState.getDate());
        this.carrierName = carrierState.getCarrier().getOrganizationName();
        this.data = carrierState.getData();
        this.geoData = carrierState.getGeoData();
    }

}
