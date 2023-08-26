package com.example.incubator.back.service.dto.report;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IncubatorProjectDto {
    private String name;
    private Double income;
    private Double expenses;
    private long residentApplications;
    private long acceptedResidents;
    private long graduatedResidents;
}
