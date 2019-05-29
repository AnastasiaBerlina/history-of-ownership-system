package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.items.Item;
import net.andrc.states.PutContainerState;

import java.util.Date;
import java.util.List;

@Data
public class PutContainerStateDto extends BaseStateDto {
    private String containerName;
    private long maxCapacity;
    private List<Item> items;
    private List<String> containers;
    private String owner;
    private GeoData geoData;

    public PutContainerStateDto(PutContainerState putContainerState) {
        containerName = putContainerState.getContainerName();
        maxCapacity = putContainerState.getMaxCapacity();
        items = putContainerState.getItems();
        containers = putContainerState.getContainers();
        owner = putContainerState.getOwner().getName().getOrganisation();
        geoData = putContainerState.getGeoData();
        date = putContainerState.getDate();
    }
}
