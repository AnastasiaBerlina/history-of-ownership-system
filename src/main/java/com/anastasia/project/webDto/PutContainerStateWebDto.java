package com.anastasia.project.webDto;

import com.anastasia.project.dto.BaseStateDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.andrc.items.GeoData;

@Data
public class PutContainerStateWebDto extends HistoryDto{

    @JsonProperty("type")
    private String typeOfAction;

    @JsonProperty("container_name")
    private String containerName;

    @JsonProperty("owner_name")
    private String owner;

    @JsonProperty("geolocation")
    private GeoData geoData;

}
