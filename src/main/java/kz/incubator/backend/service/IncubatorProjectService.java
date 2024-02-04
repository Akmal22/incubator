package kz.incubator.backend.service;

import kz.incubator.backend.Converter;
import kz.incubator.backend.entity.data.IncubatorEntity;
import kz.incubator.backend.entity.data.IncubatorProjectEntity;
import kz.incubator.backend.entity.user.UserEntity;
import kz.incubator.backend.repo.IncubatorProjectRepository;
import kz.incubator.backend.repo.IncubatorRepository;
import kz.incubator.backend.repo.UserRepository;
import kz.incubator.backend.service.dto.ServiceResult;
import kz.incubator.backend.service.dto.incubator.IncubatorProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncubatorProjectService {
    private final IncubatorProjectRepository incubatorProjectRepository;
    private final IncubatorRepository incubatorRepository;
    private final UserRepository userRepository;

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
            return new ServiceResult(false, "Project with given name already exists");
        }
        IncubatorProjectEntity incubatorProjectEntity = new IncubatorProjectEntity();
        incubatorProjectEntity.setName(incubatorProjectDto.getName());
        incubatorProjectEntity.setIncubator(incubator);
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

    public List<IncubatorProjectDto> getManagerAllProjects(String managerLogin, String query, Pageable pageable) {
        Optional<UserEntity> optionalManager = userRepository.findByUsername(managerLogin);
        assertTrue(optionalManager.isPresent(), "manager not found");
        return incubatorProjectRepository.findManagerProjectsByFilterText(optionalManager.get(), query, pageable).stream()
                .map(IncubatorProjectService::convertIncubatorProjectEntity)
                .collect(Collectors.toList());
    }

    public List<IncubatorProjectDto> getAllProjects(String query, Pageable pageable) {
        return incubatorProjectRepository.findAllByFilterText(query, pageable).stream()
                .map(IncubatorProjectService::convertIncubatorProjectEntity)
                .collect(Collectors.toList());
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
                .setIncubatorDto(Converter.convertIncubatorEntity(incubatorProjectEntity.getIncubator()))
                .setClientsDto(Converter.convertClientsEntity(incubatorProjectEntity.getClientsEntity()))
                .setRevenueDto(Converter.convertRevenueEntity(incubatorProjectEntity.getRevenueEntity()))
                .setConsumedResourcesDto(Converter.convertConsumedResourcesEntity(incubatorProjectEntity.getConsumedResourcesEntity()))
                .setExpenseDto(Converter.convertExpenseEntity(incubatorProjectEntity.getExpenseEntity()))
                .setInvestmentDto(Converter.convertInvestmentEntity(incubatorProjectEntity.getInvestmentEntity()))
                .setStartDate(incubatorProjectEntity.getStartedDate())
                .setEndDate(incubatorProjectEntity.getEndDate());
    }
}
