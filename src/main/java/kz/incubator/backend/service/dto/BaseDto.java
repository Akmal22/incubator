package kz.incubator.backend.service.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;

public interface BaseDto {
    Long getId();
    IncubatorProjectDto getProject();
    String getDataName();
}
