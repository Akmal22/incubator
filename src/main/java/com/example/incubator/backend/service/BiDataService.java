package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.BaseDataEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.repo.BiDataRepository;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.service.dto.BaseDto;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public abstract class BiDataService<ENTITY extends BaseDataEntity, DTO extends BaseDto> {
    @Autowired
    private IncubatorProjectRepository incubatorProjectRepository;

    public List<DTO> findByIncubatorFilterText(String filterText) {
        return getRepository().findAllByProjectName(filterText).stream()
                .map(this::convertEntityIntoDto)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteData(DTO dto) {
        ENTITY entity = getRepository().findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("{} information with id [{}] not found", dto.getDataName(), dto.getId());
                    throw new IllegalArgumentException(String.format("%s info not found", dto.getDataName()));
                });

        getRepository().delete(entity);

        return new ServiceResult(true, null);
    }

    public ServiceResult createNewBIData(DTO dto) {
        IncubatorProjectDto project = dto.getProject();
        String dataName = dto.getDataName();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving {} info. No project with id [{}]", dataName, project.getId());
                    throw new IllegalArgumentException("No project found");
                });
        Optional<ENTITY> optionalEntity = getRepository().findByProjectEntity(incubatorProjectEntity);
        if (optionalEntity.isPresent()) {
            log.error("Error while creating {} info, {} info data for project [{}] already exists", dataName, dataName, incubatorProjectEntity.getName());
            return new ServiceResult(false, String.format("%s info for specified project already exists", dataName));
        }

        ENTITY entity = createEntityFromDto(dto);
        entity.setProjectEntity(incubatorProjectEntity);

        getRepository().save(entity);
        return new ServiceResult(true, null);
    }

    public ServiceResult updateBIData(DTO dto) {
        String dataName = dto.getDataName();
        ENTITY entity = getRepository().findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("{} information with id [{}] not found", dataName, dto.getId());
                    throw new IllegalArgumentException(String.format("%s info not found", dataName));
                });

        IncubatorProjectDto project = dto.getProject();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving {} info. No project with id [{}]", dataName, project.getId());
                    throw new IllegalArgumentException("No project found");
                });

        Optional<ENTITY> optionalEntity = getRepository().findByProjectEntity(incubatorProjectEntity);
        if (optionalEntity.isPresent() && !optionalEntity.get().getId().equals(entity.getId())) {
            log.error("Error while updating projects {} info. Project [{}] already has {} info entity", dataName, project.getName(), dataName);
            return new ServiceResult(false, String.format("Project [%s] already has %s info entity", project.getName(), dataName));
        }

        entity.setProjectEntity(incubatorProjectEntity);
        updateEntity(entity, dto);
        getRepository().save(entity);

        return new ServiceResult(true, null);
    }

    public abstract BiDataRepository<ENTITY> getRepository();

    public abstract void updateEntity(ENTITY entity, DTO dto);

    public abstract ENTITY createEntityFromDto(DTO dto);

    public abstract DTO convertEntityIntoDto(ENTITY entity);
}
