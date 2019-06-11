package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.items.OfficerCertificate;
import net.andrc.states.OfficerAuthenticationResponseState;
import net.andrc.states.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OfficerAuthenticationResponseStateDto extends BaseStateDto {
    private OfficerCertificate officerCertificate;
    private String data;
    private String signature;
    private List<String> ownersNames;
    private ResponseStatus responseStatus;
    private String requestId;
    private GeoData geoData;

    public OfficerAuthenticationResponseStateDto(OfficerAuthenticationResponseState responseState) {
        super(responseState.getDate());
        this.date = responseState.getDate();
        this.signature = responseState.getSignature();
        this.officerCertificate = responseState.getOfficerCertificate();
        this.geoData = responseState.getGeoData();
        this.requestId = responseState.getRequestId();
        this.responseStatus = responseState.getResult();
        this.ownersNames = responseState.getOwners()
                .stream()
                .map(owner -> owner.getName()
                        .getOrganisation())
                .collect(Collectors.toList());
    }

}
