package com.example.incubator.backend.service.dto;

import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;

public interface BaseDto {
    Long getId();
    IncubatorProjectDto getProject();
    String getDataName();
}
