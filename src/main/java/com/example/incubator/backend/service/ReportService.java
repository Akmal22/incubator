package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.CountryEntity;
import com.example.incubator.backend.entity.data.IncubatorEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.repo.CountryRepository;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.repo.IncubatorRepository;
import com.example.incubator.backend.service.dto.incubator.IncubatorDto;
import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {
    private final IncubatorRepository incubatorRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;
    private final CountryRepository countryRepository;

    public List<String> getIncubatorNamesByCountry(String countryName) {
        CountryEntity country = countryRepository.findByName(countryName).orElseThrow(() -> new IllegalArgumentException("Country not found"));
        return incubatorRepository.findAllByCountry(country).stream()
                .map(IncubatorEntity::getName)
                .collect(Collectors.toList());
    }

    public List<String> getIncubatorProjectsByIncubator(String incubatorName) {
        IncubatorEntity incubator = incubatorRepository.findByName(incubatorName).orElseThrow(() -> new IllegalArgumentException("Incubator not found"));

        return incubatorProjectRepository.findAllByIncubator(incubator).stream()
                .map(IncubatorProjectEntity::getName)
                .collect(Collectors.toList());
    }

    public IncubatorDto getIncubator(String incubatorName) {
        IncubatorEntity incubator = incubatorRepository.findByName(incubatorName).orElseThrow(() -> new IllegalArgumentException("Incubator not found"));
        List<IncubatorProjectEntity> incubatorProjects = incubatorProjectRepository.findAllByIncubator(incubator);

        return new IncubatorDto()
                .setIncubatorName(incubator.getName())
                .setFounder(incubator.getFounder())
                .setFounded(incubator.getFounded())
                .setIncubatorProjects(incubatorProjects.stream()
                        .map(this::convertIncubatorProjectEntity)
                        .collect(Collectors.toList()));
    }

    public List<IncubatorDto> getIncubatorsByCountry(String countryName) {
        CountryEntity country = countryRepository.findByName(countryName).orElseThrow(() -> new IllegalArgumentException("Country not found"));
        return incubatorRepository.findAllByCountryFetchIncubators(country).stream()
                .map(this::convertIncubatorEntity)
                .collect(Collectors.toList());
    }

    public IncubatorProjectDto getIncubatorProject(String incubatorProjectName) {
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findByNameFetchIncubator(incubatorProjectName).orElseThrow(() -> new IllegalArgumentException("Incubator project not found"));

        return convertIncubatorProjectEntity(incubatorProjectEntity);
    }

    private IncubatorProjectDto convertIncubatorProjectEntity(IncubatorProjectEntity incubatorProjectEntity) {
        return new IncubatorProjectDto()
                .setName(incubatorProjectEntity.getName())
                .setStartDate(incubatorProjectEntity.getStartedDate())
                .setEndDate(incubatorProjectEntity.getEndDate())
                .setIncubatorDto(new IncubatorDto()
                        .setIncubatorName(incubatorProjectEntity.getIncubator().getName())
                        .setFounded(incubatorProjectEntity.getIncubator().getFounded())
                        .setFounder(incubatorProjectEntity.getIncubator().getFounder()));
    }

    private IncubatorDto convertIncubatorEntity(IncubatorEntity incubator) {
        return new IncubatorDto()
                .setIncubatorName(incubator.getName())
                .setFounder(incubator.getFounder())
                .setFounded(incubator.getFounded())
                .setIncubatorProjects(incubator.getIncubatorProjects().stream()
                        .map(this::convertIncubatorProjectEntity)
                        .collect(Collectors.toList()));
    }
}
