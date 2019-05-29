package com.anastasia.project.webDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.andrc.items.GeoData;

@Data
public class ChangeCarrierStateWebDto extends HistoryDto {

    @JsonProperty("type")
    private String typeOfAction;

    @JsonProperty("carrier_name")
    private String carrierName;

    @JsonProperty("geo_location")
    private GeoData geoData;


}
