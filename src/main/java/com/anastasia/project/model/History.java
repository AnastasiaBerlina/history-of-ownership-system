package com.anastasia.project.model;

import com.anastasia.project.dto.CarrierEventStateDto;
import com.anastasia.project.dto.ChangeCarrierStateDto;
import com.anastasia.project.dto.DeleteContainerStateDto;
import com.anastasia.project.dto.PutContainerStateDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class History {
    private List<CarrierEventStateDto> events;

    private List<ChangeCarrierStateDto> changeCarrier;

    private List<PutContainerStateDto> putContainer;

    private List<DeleteContainerStateDto> deleteContainer;


}
