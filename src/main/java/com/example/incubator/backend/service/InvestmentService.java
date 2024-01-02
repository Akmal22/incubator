package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.entity.data.InvestmentEntity;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.repo.InvestmentRepository;
import com.example.incubator.backend.service.dto.InvestmentDto;
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
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;

    public List<InvestmentDto> getAllInvestmentDtoByFilterText(String filterText) {
        return investmentRepository.findAllByProjectName(filterText).stream()
                .map(InvestmentService::convertInvestmentEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteInvestment(InvestmentDto investmentDto) {
        InvestmentEntity investmentEntity = investmentRepository.findById(investmentDto.getId())
                .orElseThrow(() -> {
                    log.error("Investment information with id [{}] not found", investmentDto.getId());
                    throw new IllegalArgumentException("investment info not found");
                });

        investmentRepository.delete(investmentEntity);

        return new ServiceResult(true, null);
    }

    public ServiceResult createInvestment(InvestmentDto investmentDto) {
        IncubatorProjectDto project = investmentDto.getProject();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving investment info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });
        Optional<InvestmentEntity> optionalInvestmentEntity = investmentRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalInvestmentEntity.isPresent()) {
            log.error("Error while creating investment info, investment info data for project [{}] already exists", incubatorProjectEntity.getName());
            return new ServiceResult(false, "Investment info for specified project already exists");
        }
        InvestmentEntity investmentEntity = new InvestmentEntity();
        investmentEntity.setProjectEntity(incubatorProjectEntity);
        investmentEntity.setInvestorsCount(investmentDto.getInvestorsCount());
        investmentEntity.setPercentageOfInvestedClients(investmentDto.getPercentageOfInvestedClients());

        investmentRepository.save(investmentEntity);
        return new ServiceResult(true, null);
    }

    public ServiceResult updateInvestment(InvestmentDto investmentDto) {
        InvestmentEntity investmentEntity = investmentRepository.findById(investmentDto.getId())
                .orElseThrow(() -> {
                    log.error("Investment information with id [{}] not found", investmentDto.getId());
                    throw new IllegalArgumentException("investment info not found");
                });

        IncubatorProjectDto project = investmentDto.getProject();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving investment info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });

        Optional<InvestmentEntity> optionalInvestmentEntity = investmentRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalInvestmentEntity.isPresent() && !optionalInvestmentEntity.get().getId().equals(investmentEntity.getId())) {
            log.error("Error while updating projects investment info. Project [{}] already has investment info entity", project.getName());
            return new ServiceResult(false, String.format("Project [%s] already has investment info entity", project.getName()));
        }

        investmentEntity.setProjectEntity(incubatorProjectEntity);
        investmentEntity.setInvestorsCount(investmentDto.getInvestorsCount());
        investmentEntity.setPercentageOfInvestedClients(investmentDto.getPercentageOfInvestedClients());
        investmentRepository.save(investmentEntity);

        return new ServiceResult(true, null);
    }

    public static InvestmentDto convertInvestmentEntity(InvestmentEntity investmentEntity) {
        return new InvestmentDto()
                .setId(investmentEntity.getId())
                .setProject(IncubatorProjectService.convertIncubatorProjectEntity(investmentEntity.getProjectEntity()))
                .setInvestorsCount(investmentEntity.getInvestorsCount())
                .setPercentageOfInvestedClients(investmentEntity.getPercentageOfInvestedClients());
    }
}
