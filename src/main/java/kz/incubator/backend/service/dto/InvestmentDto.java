package kz.incubator.backend.service.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InvestmentDto implements BaseDto {
    private final String dataName = "investment";
    private Long id;
    private IncubatorProjectDto project;
    private Integer investorsCount;
    private Integer percentageOfInvestedClients;
}
