package kz.incubator.ui.form.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EditProjectDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private IncubatorDto incubator;
    @NotNull
    private LocalDate startDate;
    private LocalDate endDate;
}
