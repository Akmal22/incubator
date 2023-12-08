package com.example.incubator.ui.form.dto;

import com.example.incubator.backend.service.dto.incubator.IncubatorDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EditProjectDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private IncubatorDto incubator;
    @NotNull
    private Double income;
    @NotNull
    private Double expenses;
    @NotNull
    private Integer residentApplications;
    @NotNull
    private Integer acceptedResidentApplications;
    @NotNull
    private Integer graduatedResidentsCount;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
}
