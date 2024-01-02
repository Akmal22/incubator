package com.example.incubator.ui.form.dto;

import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditClientsDto {
    private Long id;
    @NotNull
    private IncubatorProjectDto project;
    @NotNull
    private Integer applications;
    @NotNull
    private Integer accepted;
    @NotNull
    private Integer graduated;
    @NotNull
    private Integer failed;
}
