package com.anastasia.project.webDto;

import com.anastasia.project.dto.BaseStateDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.items.OfficerCertificate;

import java.util.List;

@Data
public class OfficerAuthenticationRequestStateWebDto extends HistoryDto {

    @JsonProperty("type")
    private String typeOfAction;

    private OfficerCertificate officerCertificate;
    private String signature;
    private List<String> ownersName;
    private GeoData geoData;
    private String requestId;


}
