package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.ConsumedResourcesEntity;
import com.example.incubator.backend.repo.ConsumedResourcesRepository;
import com.example.incubator.backend.service.dto.ConsumedResourcesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumedResourcesService extends BiDataService<ConsumedResourcesEntity, ConsumedResourcesDto> {
    private final ConsumedResourcesRepository consumedResourcesRepository;

    @Override
    public ConsumedResourcesRepository getRepository() {
        return consumedResourcesRepository;
    }

    public void updateEntity(ConsumedResourcesEntity consumedResourcesEntity, ConsumedResourcesDto consumedResourcesDto) {
        consumedResourcesEntity.setInvolvedManagers(consumedResourcesDto.getInvolvedManagers());
        consumedResourcesEntity.setInvolvedMentors(consumedResourcesDto.getInvolvedMentors());
        consumedResourcesEntity.setInvolvedCoaches(consumedResourcesDto.getInvolvedCoaches());
        consumedResourcesEntity.setUsedServices(consumedResourcesDto.getUsedServices());
        consumedResourcesEntity.setRentSpace(consumedResourcesDto.getRentSpace());
    }

    public ConsumedResourcesEntity createEntityFromDto(ConsumedResourcesDto consumedResourcesDto) {
        ConsumedResourcesEntity consumedResourcesEntity = new ConsumedResourcesEntity();
        consumedResourcesEntity.setInvolvedManagers(consumedResourcesDto.getInvolvedManagers());
        consumedResourcesEntity.setInvolvedMentors(consumedResourcesDto.getInvolvedMentors());
        consumedResourcesEntity.setInvolvedCoaches(consumedResourcesDto.getInvolvedCoaches());
        consumedResourcesEntity.setUsedServices(consumedResourcesDto.getUsedServices());
        consumedResourcesEntity.setRentSpace(consumedResourcesDto.getRentSpace());

        return consumedResourcesEntity;
    }

    public ConsumedResourcesDto convertEntityIntoDto(ConsumedResourcesEntity entity) {
        return new ConsumedResourcesDto()
                .setId(entity.getId())
                .setProject(IncubatorProjectService.convertIncubatorProjectEntity(entity.getProjectEntity()))
                .setInvolvedManagers(entity.getInvolvedManagers())
                .setInvolvedMentors(entity.getInvolvedMentors())
                .setInvolvedCoaches(entity.getInvolvedCoaches())
                .setUsedServices(entity.getUsedServices())
                .setRentSpace(entity.getRentSpace());
    }
}
