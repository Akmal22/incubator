package kz.incubator.backend.service.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RevenueDto implements BaseDto {
    private final String dataName = "revenue";
    private Long id;
    private IncubatorProjectDto project;
    private Double leaseRevenue;
    private Double serviceRevenue;
    private Double sponsorshipRevenue;
    private Double grantRevenue;
}
