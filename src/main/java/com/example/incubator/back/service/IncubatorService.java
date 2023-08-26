package com.example.incubator.back.service;

import com.example.incubator.back.repo.CountryRepository;
import com.example.incubator.back.repo.IncubatorProjectRepository;
import com.example.incubator.back.repo.IncubatorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class IncubatorService {
    private final CountryRepository countryRepository;
    private final IncubatorRepository incubatorRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;

}
