package kz.incubator.backend.service;

import kz.incubator.backend.entity.data.InvestmentEntity;
import kz.incubator.backend.repo.InvestmentRepository;
import kz.incubator.backend.service.dto.InvestmentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentService extends BiDataService<InvestmentEntity, InvestmentDto> {
    private final InvestmentRepository investmentRepository;

    @Override
    public InvestmentRepository getRepository() {
        return investmentRepository;
    }

    @Override
    public void updateEntity(InvestmentEntity investmentEntity, InvestmentDto investmentDto) {
        investmentEntity.setInvestorsCount(investmentDto.getInvestorsCount());
        investmentEntity.setPercentageOfInvestedClients(investmentDto.getPercentageOfInvestedClients());
    }

    @Override
    public InvestmentEntity createEntityFromDto(InvestmentDto investmentDto) {
        InvestmentEntity investmentEntity = new InvestmentEntity();
        investmentEntity.setInvestorsCount(investmentDto.getInvestorsCount());
        investmentEntity.setPercentageOfInvestedClients(investmentDto.getPercentageOfInvestedClients());

        return investmentEntity;
    }

    @Override
    public InvestmentDto convertEntity(InvestmentEntity investmentEntity) {
        return new InvestmentDto()
                .setId(investmentEntity.getId())
                .setProject(convertProjectEntity(investmentEntity.getProjectEntity()))
                .setInvestorsCount(investmentEntity.getInvestorsCount())
                .setPercentageOfInvestedClients(investmentEntity.getPercentageOfInvestedClients());
    }
}
