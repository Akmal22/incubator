package com.example.incubator.back.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceResult {
    private boolean success;
    private String errorMessage;
}
