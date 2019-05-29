package com.anastasia.project.webDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class HistoryDto {

    @JsonProperty("date")
    private Date date;
}
