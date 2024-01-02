package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.ConsumedResourcesEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.repo.ConsumedResourcesRepository;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.service.dto.ConsumedResourcesDto;
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
public class ConsumedResourcesService {
    private final ConsumedResourcesRepository consumedResourcesRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;

    public List<ConsumedResourcesDto> getAllConsumedResourcesByFilterText(String filterText) {
        return consumedResourcesRepository.findAllByProjectName(filterText).stream()
                .map(ConsumedResourcesService::convertConsumedResourcesEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteConsumedResources(ConsumedResourcesDto consumedResourcesDto) {
        ConsumedResourcesEntity consumedResourcesEntity = consumedResourcesRepository.findById(consumedResourcesDto.getId())
                .orElseThrow(() -> {
                    log.error("Consumed resources information with id [{}] not found", consumedResourcesDto.getId());
                    throw new IllegalArgumentException("Consumed resources info not found");
                });

        consumedResourcesRepository.delete(consumedResourcesEntity);

        return new ServiceResult(true, null);
    }

    public ServiceResult createConsumedResources(ConsumedResourcesDto consumedResourcesDto) {
        IncubatorProjectDto project = consumedResourcesDto.getProject();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving consumed resources info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });
        Optional<ConsumedResourcesEntity> optionalConsumedResourcesEntity = consumedResourcesRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalConsumedResourcesEntity.isPresent()) {
            log.error("Error while creating consumed resources info, consumed resources info data for project [{}] already exists", incubatorProjectEntity.getName());
            return new ServiceResult(false, "Consumed resources info for specified project already exists");
        }
        ConsumedResourcesEntity consumedResourcesEntity = new ConsumedResourcesEntity();
        consumedResourcesEntity.setProjectEntity(incubatorProjectEntity);
        consumedResourcesEntity.setInvolvedManagers(consumedResourcesDto.getInvolvedManagers());
        consumedResourcesEntity.setInvolvedMentors(consumedResourcesDto.getInvolvedMentors());
        consumedResourcesEntity.setInvolvedCoaches(consumedResourcesDto.getInvolvedCoaches());
        consumedResourcesEntity.setUsedServices(consumedResourcesDto.getUsedServices());
        consumedResourcesEntity.setRentSpace(consumedResourcesDto.getRentSpace());

        consumedResourcesRepository.save(consumedResourcesEntity);
        return new ServiceResult(true, null);
    }

    public ServiceResult updateConsumedResources(ConsumedResourcesDto consumedResourcesDto) {
        ConsumedResourcesEntity consumedResourcesEntity = consumedResourcesRepository.findById(consumedResourcesDto.getId())
                .orElseThrow(() -> {
                    log.error("Consumed resources information with id [{}] not found", consumedResourcesDto.getId());
                    throw new IllegalArgumentException("Consumed resources info not found");
                });

        IncubatorProjectDto project = consumedResourcesDto.getProject();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving consumed resources info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });

        Optional<ConsumedResourcesEntity> optionalConsumedResourcesEntity = consumedResourcesRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalConsumedResourcesEntity.isPresent() && !optionalConsumedResourcesEntity.get().getId().equals(consumedResourcesEntity.getId())) {
            log.error("Error while updating projects consumed resources info. Project [{}] already has consumed resources info entity", project.getName());
            return new ServiceResult(false, String.format("Project [%s] already has consumed resources info entity", project.getName()));
        }

        consumedResourcesEntity.setProjectEntity(incubatorProjectEntity);
        consumedResourcesEntity.setInvolvedManagers(consumedResourcesDto.getInvolvedManagers());
        consumedResourcesEntity.setInvolvedMentors(consumedResourcesDto.getInvolvedMentors());
        consumedResourcesEntity.setInvolvedCoaches(consumedResourcesDto.getInvolvedCoaches());
        consumedResourcesEntity.setUsedServices(consumedResourcesDto.getUsedServices());
        consumedResourcesEntity.setRentSpace(consumedResourcesDto.getRentSpace());

        consumedResourcesRepository.save(consumedResourcesEntity);

        return new ServiceResult(true, null);
    }

    public static ConsumedResourcesDto convertConsumedResourcesEntity(ConsumedResourcesEntity consumedResourcesEntity) {
        return new ConsumedResourcesDto()
                .setId(consumedResourcesEntity.getId())
                .setProject(IncubatorProjectService.convertIncubatorProjectEntity(consumedResourcesEntity.getProjectEntity()))
                .setInvolvedManagers(consumedResourcesEntity.getInvolvedManagers())
                .setInvolvedMentors(consumedResourcesEntity.getInvolvedMentors())
                .setInvolvedCoaches(consumedResourcesEntity.getInvolvedCoaches())
                .setUsedServices(consumedResourcesEntity.getUsedServices())
                .setRentSpace(consumedResourcesEntity.getRentSpace());
    }
}
