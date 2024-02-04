package kz.incubator.backend.service.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExpenseDto implements BaseDto {
    private final String dataName = "expense";
    private Long id;
    private IncubatorProjectDto project;
    private Double marketing;
    private Double payroll;
    private Double equipment;
    private Double utilities;
    private Double material;
    private Double insurance;
}
