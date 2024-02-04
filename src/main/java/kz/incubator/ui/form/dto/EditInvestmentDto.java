package kz.incubator.ui.form.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditInvestmentDto {
    private Long id;
    @NotNull
    private IncubatorProjectDto project;
    @NotNull
    @Min(0)
    @Max(100)
    private Integer investorsCount;
    @NotNull
    @Min(0)
    @Max(100)
    private Integer percentageOfInvestedClients;
}
