package com.example.incubator.back.service.dto.incubator;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class IncubatorProjectDto {
    private Long id;
    private String name;
    private IncubatorDto incubatorDto;
    private Double income;
    private Double expenses;
    private int residentApplications;
    private int acceptedResidents;
    private int graduatedResidents;
    private LocalDate startDate;
    private LocalDate endDate;
}
