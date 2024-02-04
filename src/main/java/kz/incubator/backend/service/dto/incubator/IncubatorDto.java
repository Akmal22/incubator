package kz.incubator.backend.service.dto.incubator;

import kz.incubator.backend.service.dto.CountryDto;
import kz.incubator.backend.service.dto.user.UserDto;
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
