package com.example.incubator.back.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
public class IncubatorDto {
    private String incubatorName;
    private String founder;
    private LocalDate founded;
    private List<IncubatorProjectDto> incubatorProjects;
}
