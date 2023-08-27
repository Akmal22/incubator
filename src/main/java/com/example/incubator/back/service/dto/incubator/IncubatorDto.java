package com.example.incubator.back.service.dto.incubator;

import com.example.incubator.back.service.dto.country.CountryDto;
import com.example.incubator.back.service.dto.user.UserDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
public class IncubatorDto {
    private Long id;
    private String incubatorName;
    private CountryDto country;
    private UserDto manager;
    private String founder;
    private LocalDate founded;
    private List<IncubatorProjectDto> incubatorProjects;
}
