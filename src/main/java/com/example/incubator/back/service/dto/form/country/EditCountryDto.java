package com.example.incubator.back.service.dto.form.country;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditCountryDto {
    private Long id;
    @NotBlank
    private String countryName;
}
