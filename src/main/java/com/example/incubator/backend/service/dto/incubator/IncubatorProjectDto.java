package com.example.incubator.backend.service.dto.incubator;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class IncubatorProjectDto {
    private Long id;
    private String name;
    private IncubatorDto incubatorDto;
    private LocalDate startDate;
    private LocalDate endDate;
}
