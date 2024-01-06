package com.example.incubator.ui.form.dto;

import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditConsumedResourcesDto {
    private Long id;
    @NotNull
    private IncubatorProjectDto project;
    @NotNull
    @Min(0)
    private Integer involvedManagers;
    @NotNull
    @Min(0)
    private Integer involvedCoaches;
    @NotNull
    @Min(0)
    private Integer involvedMentors;
    @NotNull
    @Min(0)
    private Integer usedServices;
    @NotNull
    @Min(0)
    private Double rentSpace;
}
