package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.IncubatorEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.repo.IncubatorRepository;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncubatorProjectService {
    private final IncubatorProjectRepository incubatorProjectRepository;
    private final IncubatorRepository incubatorRepository;

    public List<IncubatorProjectDto> findAllByFilterText(String filterText) {
        return incubatorProjectRepository.findAllByFilterText(filterText).stream()
                .map(IncubatorProjectService::convertIncubatorProjectEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult createIncubatorProject(IncubatorProjectDto incubatorProjectDto) {
        IncubatorEntity incubator = getIncubator(incubatorProjectDto.getIncubatorDto().getId());

        Optional<IncubatorProjectEntity> optionalIncubatorProjectEntity = incubatorProjectRepository.findByName(incubatorProjectDto.getName());
        if (optionalIncubatorProjectEntity.isPresent()) {
            log.error("Incubator project with name [{}] already exists", incubatorProjectDto.getName());
            return new ServiceResult(false, "Incubator with given name already exists");
        }
        IncubatorProjectEntity incubatorProjectEntity = new IncubatorProjectEntity();
        incubatorProjectEntity.setName(incubatorProjectDto.getName());
        incubatorProjectEntity.setIncubator(incubator);
        incubatorProjectEntity.setIncome(incubatorProjectDto.getIncome());
        incubatorProjectEntity.setExpenses(incubatorProjectDto.getExpenses());
        incubatorProjectEntity.setResidentApplications(incubatorProjectDto.getResidentApplications());
        incubatorProjectEntity.setAcceptedResidentApplications(incubatorProjectDto.getResidentApplications());
        incubatorProjectEntity.setGraduatedResidentsCount(incubatorProjectDto.getGraduatedResidents());
        incubatorProjectEntity.setStartedDate(incubatorProjectDto.getStartDate());
        incubatorProjectEntity.setEndDate(incubatorProjectDto.getEndDate());

        incubatorProjectRepository.save(incubatorProjectEntity);

        return new ServiceResult(true, null);
    }

    public ServiceResult updateIncubatorProject(IncubatorProjectDto incubatorProjectDto) {
        IncubatorEntity incubator = getIncubator(incubatorProjectDto.getIncubatorDto().getId());
        IncubatorProjectEntity incubatorProject = getIncubatorProject(incubatorProjectDto.getId());

        incubatorProject.setName(incubatorProjectDto.getName());
        incubatorProject.setIncubator(incubator);
        incubatorProject.setIncome(incubatorProjectDto.getIncome());
        incubatorProject.setExpenses(incubatorProjectDto.getExpenses());
        incubatorProject.setResidentApplications(incubatorProjectDto.getResidentApplications());
        incubatorProject.setAcceptedResidentApplications(incubatorProjectDto.getResidentApplications());
        incubatorProject.setGraduatedResidentsCount(incubatorProjectDto.getGraduatedResidents());
        incubatorProject.setStartedDate(incubatorProjectDto.getStartDate());
        incubatorProject.setEndDate(incubatorProjectDto.getEndDate());

        incubatorProjectRepository.save(incubatorProject);

        return new ServiceResult(true, null);
    }

    public ServiceResult deleteIncubatorProject(IncubatorProjectDto incubatorProjectDto) {
        IncubatorProjectEntity incubatorProject = getIncubatorProject(incubatorProjectDto.getId());
        incubatorProjectRepository.delete(incubatorProject);

        return new ServiceResult(true, null);
    }

    private IncubatorProjectEntity getIncubatorProject(long projectId) {
        return incubatorProjectRepository.findById(projectId).orElseThrow(() -> {
            log.error("Incubator project with id [{}] not found", projectId);
            throw new IllegalArgumentException("Incubator project not found");
        });
    }

    private IncubatorEntity getIncubator(long incubatorId) {
        return incubatorRepository.findById(incubatorId).orElseThrow(() -> {
            log.error("Incubator with id [{}] not found", incubatorId);
            throw new IllegalArgumentException("Incubator not found");
        });
    }

    public static IncubatorProjectDto convertIncubatorProjectEntity(IncubatorProjectEntity incubatorProjectEntity) {
        return new IncubatorProjectDto()
                .setId(incubatorProjectEntity.getId())
                .setName(incubatorProjectEntity.getName())
                .setIncubatorDto(IncubatorService.convertIncubatorEntity(incubatorProjectEntity.getIncubator()))
                .setIncome(incubatorProjectEntity.getIncome())
                .setExpenses(incubatorProjectEntity.getExpenses())
                .setResidentApplications(incubatorProjectEntity.getResidentApplications())
                .setAcceptedResidents(incubatorProjectEntity.getAcceptedResidentApplications())
                .setGraduatedResidents(incubatorProjectEntity.getGraduatedResidentsCount())
                .setStartDate(incubatorProjectEntity.getStartedDate())
                .setEndDate(incubatorProjectEntity.getEndDate());
    }
}
