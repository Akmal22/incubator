package com.example.incubator.ui.form.dto;

import com.example.incubator.backend.service.dto.CountryDto;
import com.example.incubator.backend.service.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EditIncubatorDto {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private UserDto manager;
    @NotNull
    private CountryDto country;
    @NotNull
    private LocalDate founded;
    @NotBlank
    private String founder;
}
