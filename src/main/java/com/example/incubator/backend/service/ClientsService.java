package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.ClientsEntity;
import com.example.incubator.backend.repo.ClientsRepository;
import com.example.incubator.backend.service.dto.ClientsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsService extends BiDataService<ClientsEntity, ClientsDto> {
    private final ClientsRepository clientsRepository;

    @Override
    public ClientsRepository getRepository() {
        return clientsRepository;
    }

    @Override
    public void updateEntity(ClientsEntity clientsEntity, ClientsDto clientsDto) {
        clientsEntity.setApplications(clientsDto.getApplications());
        clientsEntity.setAccepted(clientsDto.getAccepted());
        clientsEntity.setGraduated(clientsDto.getGraduated());
        clientsEntity.setFailed(clientsDto.getFailed());
    }

    @Override
    public ClientsEntity createEntityFromDto(ClientsDto clientsDto) {
        ClientsEntity clientsEntity = new ClientsEntity();
        clientsEntity.setApplications(clientsDto.getApplications());
        clientsEntity.setAccepted(clientsDto.getAccepted());
        clientsEntity.setGraduated(clientsDto.getGraduated());
        clientsEntity.setFailed(clientsDto.getFailed());

        return clientsEntity;
    }

    @Override
    public ClientsDto convertEntityIntoDto(ClientsEntity clientsEntity) {
        return new ClientsDto()
                .setId(clientsEntity.getId())
                .setProject(IncubatorProjectService.convertIncubatorProjectEntity(clientsEntity.getProjectEntity()))
                .setApplications(clientsEntity.getApplications())
                .setAccepted(clientsEntity.getAccepted())
                .setGraduated(clientsEntity.getGraduated())
                .setFailed(clientsEntity.getFailed());
    }
}
