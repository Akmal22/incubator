package com.example.incubator.backend.service.dto;

import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConsumedResourcesDto {
    private Long id;
    private IncubatorProjectDto project;
    private Integer involvedManagers;
    private Integer involvedCoaches;
    private Integer involvedMentors;
    private Integer usedServices;
    private Double rentSpace;
}
