package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.entity.data.RevenueEntity;
import com.example.incubator.backend.repo.IncubatorProjectRepository;
import com.example.incubator.backend.repo.RevenueRepository;
import com.example.incubator.backend.service.dto.RevenueDto;
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
public class RevenueService {
    private final RevenueRepository revenueRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;

    public List<RevenueDto> getAllRevenueDtoByFilterText(String filterText) {
        return revenueRepository.findAllByProjectName(filterText).stream()
                .map(RevenueService::convertRevenueEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteRevenue(RevenueDto revenueDto) {
        RevenueEntity revenueEntity = revenueRepository.findById(revenueDto.getId())
                .orElseThrow(() -> {
                    log.error("Revenue information with id [{}] not found", revenueDto.getId());
                    throw new IllegalArgumentException("Revenue info not found");
                });

        revenueRepository.delete(revenueEntity);

        return new ServiceResult(true, null);
    }

    public ServiceResult createRevenue(RevenueDto revenueDto) {
        IncubatorProjectDto project = revenueDto.getIncubatorProjectDto();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving revenue info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });
        Optional<RevenueEntity> optionalRevenueEntity = revenueRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalRevenueEntity.isPresent()) {
            log.error("Error while creating revenue info, revenue info data for project [{}] already exists", incubatorProjectEntity.getName());
            return new ServiceResult(false, "Revenue info for specified project already exists");
        }
        RevenueEntity revenueEntity = new RevenueEntity();
        revenueEntity.setProjectEntity(incubatorProjectEntity);
        revenueEntity.setLeaseRevenue(revenueDto.getLeaseRevenue());
        revenueEntity.setServiceRevenue(revenueDto.getServiceRevenue());
        revenueEntity.setSponsorshipRevenue(revenueDto.getSponsorshipRevenue());
        revenueEntity.setGrantRevenue(revenueDto.getGrantRevenue());

        revenueRepository.save(revenueEntity);
        return new ServiceResult(true, null);
    }

    public ServiceResult updateRevenue(RevenueDto revenueDto) {
        RevenueEntity revenueEntity = revenueRepository.findById(revenueDto.getId())
                .orElseThrow(() -> {
                    log.error("Revenue information with id [{}] not found", revenueDto.getId());
                    throw new IllegalArgumentException("Revenue info not found");
                });

        IncubatorProjectDto project = revenueDto.getIncubatorProjectDto();
        IncubatorProjectEntity incubatorProjectEntity = incubatorProjectRepository.findById(project.getId())
                .orElseThrow(() -> {
                    log.error("Error while saving revenue info. No project with id [{}]", project.getId());
                    throw new IllegalArgumentException("No project found");
                });

        Optional<RevenueEntity> optionalRevenueEntity = revenueRepository.findByProjectEntity(incubatorProjectEntity);
        if (optionalRevenueEntity.isPresent() && !optionalRevenueEntity.get().getId().equals(revenueEntity.getId())) {
            log.error("Error while updating projects revenue info. Project [{}] already has revenue info entity", project.getName());
            return new ServiceResult(false, String.format("Project [%s] already has revenue info entity", project.getName()));
        }

        revenueEntity.setProjectEntity(incubatorProjectEntity);
        revenueEntity.setLeaseRevenue(revenueDto.getLeaseRevenue());
        revenueEntity.setServiceRevenue(revenueDto.getServiceRevenue());
        revenueEntity.setSponsorshipRevenue(revenueDto.getSponsorshipRevenue());
        revenueEntity.setGrantRevenue(revenueDto.getGrantRevenue());
        revenueRepository.save(revenueEntity);

        return new ServiceResult(true, null);
    }

    public static RevenueDto convertRevenueEntity(RevenueEntity revenueEntity) {
        return new RevenueDto()
                .setId(revenueEntity.getId())
                .setIncubatorProjectDto(IncubatorProjectService.convertIncubatorProjectEntity(revenueEntity.getProjectEntity()))
                .setLeaseRevenue(revenueEntity.getLeaseRevenue())
                .setServiceRevenue(revenueEntity.getServiceRevenue())
                .setSponsorshipRevenue(revenueEntity.getSponsorshipRevenue())
                .setGrantRevenue(revenueEntity.getGrantRevenue());
    }
}
