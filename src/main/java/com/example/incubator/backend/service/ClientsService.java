package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.ClientsEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.repo.ClientsRepository;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.service.dto.ServiceResult;
import com.example.incubator.backend.service.dto.clients.ClientsDto;
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
public class ClientsService {
    private final ClientsRepository clientsRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;
    private final IncubatorProjectService incubatorProjectService;

    public List<ClientsDto> findByIncubatorFilterText(String filterText) {
        return clientsRepository.findAllByProjectName(filterText)
                .stream()
                .map(ClientsService::convertClientsEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteClientDto(ClientsDto clientsDto) {
        ClientsEntity clientsEntity = clientsRepository.findById(clientsDto.getId())
                .orElseThrow(() -> {
                    log.error("Client information with id [{}] not found", clientsDto.getId());
                    throw new IllegalArgumentException("Client info not found");
                });

        clientsRepository.delete(clientsEntity);

        return new ServiceResult(true, null);
    }

    public ServiceResult createClientsDto(ClientsDto clientsDto) {
        IncubatorProjectDto project = clientsDto.getIncubatorProjectDto();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving clients info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });
        Optional<ClientsEntity> optionalClientsEntity = clientsRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalClientsEntity.isPresent()) {
            log.error("Error while creating clients info, clients info data for project [{}] already exists", incubatorProjectEntity.getName());
            return new ServiceResult(false, "Clients info for specified project already exists");
        }
        ClientsEntity clientsEntity = new ClientsEntity();
        clientsEntity.setProjectEntity(incubatorProjectEntity);
        clientsEntity.setApplications(clientsDto.getApplications());
        clientsEntity.setAccepted(clientsDto.getAccepted());
        clientsEntity.setGraduated(clientsDto.getGraduated());
        clientsEntity.setFailed(clientsDto.getFailed());

        clientsRepository.save(clientsEntity);
        return new ServiceResult(true, null);
    }

    public ServiceResult updateClientsDto(ClientsDto clientsDto) {
        ClientsEntity clientsEntity = clientsRepository.findById(clientsDto.getId())
                .orElseThrow(() -> {
                    log.error("Client information with id [{}] not found", clientsDto.getId());
                    throw new IllegalArgumentException("Client info not found");
                });

        IncubatorProjectDto project = clientsDto.getIncubatorProjectDto();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving clients info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });

        Optional<ClientsEntity> optionalClientsEntity = clientsRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalClientsEntity.isPresent() && !optionalClientsEntity.get().getId().equals(clientsEntity.getId())) {
            log.error("Error while updating projects clients info. Project [{}] already has clients info entity", project.getName());
            return new ServiceResult(false, String.format("Project [%s] already has clients info entity", project.getName()));
        }

        clientsEntity.setProjectEntity(incubatorProjectEntity);
        clientsEntity.setApplications(clientsDto.getApplications());
        clientsEntity.setAccepted(clientsDto.getAccepted());
        clientsEntity.setGraduated(clientsDto.getGraduated());
        clientsEntity.setFailed(clientsDto.getFailed());
        clientsRepository.save(clientsEntity);

        return new ServiceResult(true, null);
    }

    public static ClientsDto convertClientsEntity(ClientsEntity clientsEntity) {
        return new ClientsDto()
                .setId(clientsEntity.getId())
                .setIncubatorProjectDto(IncubatorProjectService.convertIncubatorProjectEntity(clientsEntity.getProjectEntity()))
                .setApplications(clientsEntity.getApplications())
                .setAccepted(clientsEntity.getAccepted())
                .setGraduated(clientsEntity.getGraduated())
                .setFailed(clientsEntity.getFailed());
    }
}
