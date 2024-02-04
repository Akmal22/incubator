package kz.incubator.backend.service.dto;

import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientsDto implements BaseDto {
    private final String dataName = "client";
    private Long id;
    private IncubatorProjectDto project;
    private Integer applications;
    private Integer accepted;
    private Integer graduated;
    private Integer failed;
}
