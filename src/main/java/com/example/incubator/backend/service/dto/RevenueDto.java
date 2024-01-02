package com.example.incubator.backend.service.dto;

import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RevenueDto {
    private Long id;
    private IncubatorProjectDto incubatorProjectDto;
    private Double leaseRevenue;
    private Double serviceRevenue;
    private Double sponsorshipRevenue;
    private Double grantRevenue;
}
