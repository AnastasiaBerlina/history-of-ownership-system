package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.states.DeleteContainerState;

import java.util.Date;

@Data
public class DeleteContainerStateDto {

    private String containerName;
    private String ownerName;
    private GeoData geoData;
    private Date date;

    public DeleteContainerStateDto (DeleteContainerState deleteContainerState){
        this.containerName = deleteContainerState.getContainerName();
        this.ownerName = deleteContainerState.getOwner().getName().getOrganisation();
        this.geoData = deleteContainerState.getGeoData();
        this.date = deleteContainerState.getDate();
    }

}
