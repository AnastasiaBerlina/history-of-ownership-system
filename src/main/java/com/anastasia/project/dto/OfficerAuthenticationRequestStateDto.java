package com.anastasia.project.dto;

import lombok.Data;
import lombok.val;
import net.andrc.items.GeoData;
import net.andrc.items.OfficerCertificate;
import net.andrc.states.OfficerAuthenticationRequestState;
import net.andrc.states.OfficerAuthenticationResponseState;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OfficerAuthenticationRequestStateDto {

    private OfficerCertificate officerCertificate;
    private String signature;
    private List<String> ownersName;
    private GeoData geoData;
    private String requestId;
    private Date date;

    public OfficerAuthenticationRequestStateDto (OfficerAuthenticationRequestState responseState){
        this.date = responseState.getDate();
        this.signature = responseState.getSignature();
        this.officerCertificate = responseState.getOfficerCertificate();
        this.geoData = responseState.getGeoData();
        this.requestId = responseState.getRequestId();
        this.ownersName = responseState.getOwners()
                .stream()
                .map(owner->owner.getName()
                        .getOrganisation())
                .collect(Collectors.toList());
    }
}
