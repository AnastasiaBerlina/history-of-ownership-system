package com.anastasia.project.dto;

import lombok.Data;
import net.andrc.items.GeoData;
import net.andrc.items.OfficerCertificate;
import net.andrc.states.OfficerAuthenticationRequestState;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OfficerAuthenticationRequestStateDto extends BaseStateDto {

    private OfficerCertificate officerCertificate;
    private String signature;
    private List<String> ownersName;
    private GeoData geoData;
    private String requestId;


    public OfficerAuthenticationRequestStateDto(OfficerAuthenticationRequestState responseState) {
        super(responseState.getDate());
        this.signature = responseState.getSignature();
        this.officerCertificate = responseState.getOfficerCertificate();
        this.geoData = responseState.getGeoData();
        this.requestId = responseState.getRequestId();
        this.ownersName = responseState.getOwners()
                .stream()
                .map(owner -> owner.getName()
                        .getOrganisation())
                .collect(Collectors.toList());
    }
}
