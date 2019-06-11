package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.states.DeleteContainerState;

@Data
public class DeleteContainerStateDto extends BaseStateDto {

    private String containerName;
    private String ownerName;
    private GeoData geoData;

    public DeleteContainerStateDto(DeleteContainerState deleteContainerState) {
        super(deleteContainerState.getDate());
        this.containerName = deleteContainerState.getContainerName();
        this.ownerName = deleteContainerState.getOwner().getName().getOrganisation();
        this.geoData = deleteContainerState.getGeoData();
    }

}
