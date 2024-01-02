package com.example.incubator.backend.service.dto.clients;

import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ClientsDto {
    private Long id;
    private IncubatorProjectDto incubatorProjectDto;
    private Integer applications;
    private Integer accepted;
    private Integer graduated;
    private Integer failed;
}
