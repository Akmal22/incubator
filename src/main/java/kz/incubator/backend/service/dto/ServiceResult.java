package kz.incubator.backend.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceResult {
    private boolean success;
    private String errorMessage;
}
