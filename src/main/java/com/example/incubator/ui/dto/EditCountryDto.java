package com.example.incubator.ui.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditCountryDto {
    private Long id;
    @NotBlank
    private String countryName;
}
