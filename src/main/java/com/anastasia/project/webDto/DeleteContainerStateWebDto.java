package com.anastasia.project.webDto;

import com.anastasia.project.dto.BaseStateDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.states.DeleteContainerState;

@Data
public class DeleteContainerStateWebDto extends HistoryDto {

    @JsonProperty("type")
    private String typeOfAction;

    @JsonProperty("container_name")
    private String containerName;

    @JsonProperty("ownerName")
    private String ownerName;

    @JsonProperty("geo_location")
    private GeoData geoData;


}
