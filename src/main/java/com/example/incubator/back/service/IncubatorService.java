package com.example.incubator.back.service;

import com.example.incubator.back.entity.data.CountryEntity;
import com.example.incubator.back.entity.data.IncubatorEntity;
import com.example.incubator.back.entity.data.IncubatorProjectEntity;
import com.example.incubator.back.repo.CountryRepository;
import com.example.incubator.back.repo.IncubatorProjectRepository;
import com.example.incubator.back.repo.IncubatorRepository;
import com.example.incubator.back.service.dto.IncubatorDto;
import com.example.incubator.back.service.dto.IncubatorProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class IncubatorService {
    private final CountryRepository countryRepository;
    private final IncubatorRepository incubatorRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;

    public List<String> getCountryNames() {
        return countryRepository.findAll().stream()
                .map(CountryEntity::getName)
                .collect(Collectors.toList());
    }

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

    public IncubatorProjectDto getIncubatorProject(String incubatorProjectName) {
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findByName(incubatorProjectName).orElseThrow(() -> new IllegalArgumentException("Incubator project not found"));

        return convertIncubatorProjectEntity(incubatorProjectEntity);
    }

    private IncubatorProjectDto convertIncubatorProjectEntity(IncubatorProjectEntity incubatorProjectEntity) {
        return new IncubatorProjectDto()
                .setName(incubatorProjectEntity.getName())
                .setIncome(incubatorProjectEntity.getIncome())
                .setExpenses(incubatorProjectEntity.getExpenses())
                .setResidentApplications(incubatorProjectEntity.getResidentApplications())
                .setAcceptedResidents(incubatorProjectEntity.getAcceptedResidentApplications())
                .setGraduatedResidents(incubatorProjectEntity.getGraduatedResidentsCount());
    }
}
