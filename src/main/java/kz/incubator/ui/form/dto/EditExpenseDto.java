package kz.incubator.ui.form.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditExpenseDto {
    private Long id;
    @NotNull
    private IncubatorProjectDto project;
    @NotNull
    private Double marketing;
    @NotNull
    private Double payroll;
    @NotNull
    private Double equipment;
    @NotNull
    private Double utilities;
    @NotNull
    private Double material;
    @NotNull
    private Double insurance;
}
