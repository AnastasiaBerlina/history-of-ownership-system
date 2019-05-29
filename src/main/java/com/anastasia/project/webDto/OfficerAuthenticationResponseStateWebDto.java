package com.anastasia.project.webDto;

import com.anastasia.project.dto.BaseStateDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.items.OfficerCertificate;
import net.andrc.states.ResponseStatus;

import java.util.List;

@Data
public class OfficerAuthenticationResponseStateWebDto extends HistoryDto {

    @JsonProperty("type")
    private String typeOfAction;
    private OfficerCertificate officerCertificate;
    private String data;
    private String signature;
    private List<String> ownersNames;
    private ResponseStatus responseStatus;
    private String requestId;
    private GeoData geoData;


}
