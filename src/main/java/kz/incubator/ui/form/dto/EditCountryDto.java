package kz.incubator.ui.form.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditCountryDto {
    private Long id;
    @NotBlank
    private String countryName;
}
