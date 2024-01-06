package com.example.incubator.ui.form.dto;

import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditRevenueDto {
    private Long id;
    @NotNull
    private IncubatorProjectDto project;
    @NotNull
    private Double leaseRevenue;
    @NotNull
    private Double serviceRevenue;
    @NotNull
    private Double sponsorshipRevenue;
    @NotNull
    private Double grantRevenue;
}
