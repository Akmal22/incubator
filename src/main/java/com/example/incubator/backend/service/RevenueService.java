package com.example.incubator.backend.service;

import com.example.incubator.backend.entity.data.RevenueEntity;
import com.example.incubator.backend.repo.RevenueRepository;
import com.example.incubator.backend.service.dto.RevenueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevenueService extends BiDataService<RevenueEntity, RevenueDto> {
    private final RevenueRepository revenueRepository;

    @Override
    public RevenueRepository getRepository() {
        return revenueRepository;
    }

    @Override
    public void updateEntity(RevenueEntity revenueEntity, RevenueDto revenueDto) {
        revenueEntity.setLeaseRevenue(revenueDto.getLeaseRevenue());
        revenueEntity.setServiceRevenue(revenueDto.getServiceRevenue());
        revenueEntity.setSponsorshipRevenue(revenueDto.getSponsorshipRevenue());
        revenueEntity.setGrantRevenue(revenueDto.getGrantRevenue());
    }

    @Override
    public RevenueEntity createEntityFromDto(RevenueDto revenueDto) {
        RevenueEntity revenueEntity = new RevenueEntity();
        revenueEntity.setLeaseRevenue(revenueDto.getLeaseRevenue());
        revenueEntity.setServiceRevenue(revenueDto.getServiceRevenue());
        revenueEntity.setSponsorshipRevenue(revenueDto.getSponsorshipRevenue());
        revenueEntity.setGrantRevenue(revenueDto.getGrantRevenue());

        return revenueEntity;
    }

    @Override
    public RevenueDto convertEntityIntoDto(RevenueEntity revenueEntity) {
        return new RevenueDto()
                .setId(revenueEntity.getId())
                .setProject(IncubatorProjectService.convertIncubatorProjectEntity(revenueEntity.getProjectEntity()))
                .setLeaseRevenue(revenueEntity.getLeaseRevenue())
                .setServiceRevenue(revenueEntity.getServiceRevenue())
                .setSponsorshipRevenue(revenueEntity.getSponsorshipRevenue())
                .setGrantRevenue(revenueEntity.getGrantRevenue());
    }
}
