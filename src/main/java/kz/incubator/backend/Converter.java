package kz.incubator.backend;

import kz.incubator.backend.entity.data.ClientsEntity;
import kz.incubator.backend.entity.data.ConsumedResourcesEntity;
import kz.incubator.backend.entity.data.CountryEntity;
import kz.incubator.backend.entity.data.ExpenseEntity;
import kz.incubator.backend.entity.data.IncubatorEntity;
import kz.incubator.backend.entity.data.InvestmentEntity;
import kz.incubator.backend.entity.data.RevenueEntity;
import kz.incubator.backend.entity.user.UserEntity;
import kz.incubator.backend.service.dto.ClientsDto;
import kz.incubator.backend.service.dto.ConsumedResourcesDto;
import kz.incubator.backend.service.dto.CountryDto;
import kz.incubator.backend.service.dto.ExpenseDto;
import kz.incubator.backend.service.dto.InvestmentDto;
import kz.incubator.backend.service.dto.RevenueDto;
import kz.incubator.backend.service.dto.incubator.IncubatorDto;
import kz.incubator.backend.service.dto.user.UserDto;

import java.util.Optional;

public interface Converter {
    static UserDto convertUserEntity(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setUuid(user.getUuid());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());

        return userDto;
    }

    static CountryDto convertCountryEntity(CountryEntity country) {
        CountryDto countryDto = new CountryDto();
        countryDto.setId(country.getId());
        countryDto.setCountryName(country.getName());

        return countryDto;
    }

    static IncubatorDto convertIncubatorEntity(IncubatorEntity incubator) {
        return new IncubatorDto()
                .setId(incubator.getId())
                .setIncubatorName(incubator.getName())
                .setCountry(convertCountryEntity(incubator.getCountry()))
                .setManager(convertUserEntity(incubator.getManager()))
                .setFounded(incubator.getFounded())
                .setFounder(incubator.getFounder());
    }

    static ClientsDto convertClientsEntity(ClientsEntity clientsEntity) {
        return Optional.ofNullable(clientsEntity)
                .map(c -> new ClientsDto()
                        .setId(c.getId())
                        .setApplications(c.getApplications())
                        .setAccepted(c.getAccepted())
                        .setGraduated(c.getGraduated())
                        .setFailed(c.getFailed()))
                .orElse(null);

    }

    static RevenueDto convertRevenueEntity(RevenueEntity revenueEntity) {
        return Optional.ofNullable(revenueEntity)
                .map(r -> new RevenueDto()
                        .setId(r.getId())
                        .setLeaseRevenue(r.getLeaseRevenue())
                        .setServiceRevenue(r.getServiceRevenue())
                        .setSponsorshipRevenue(r.getSponsorshipRevenue())
                        .setGrantRevenue(r.getGrantRevenue()))
                .orElse(null);
    }

    static ConsumedResourcesDto convertConsumedResourcesEntity(ConsumedResourcesEntity entity) {
        return Optional.ofNullable(entity)
                .map(e -> new ConsumedResourcesDto()
                        .setId(e.getId())
                        .setInvolvedManagers(e.getInvolvedManagers())
                        .setInvolvedMentors(e.getInvolvedMentors())
                        .setInvolvedCoaches(e.getInvolvedCoaches())
                        .setUsedServices(e.getUsedServices())
                        .setRentSpace(e.getRentSpace()))
                .orElse(null);
    }

    static ExpenseDto convertExpenseEntity(ExpenseEntity expenseEntity) {
        return Optional.ofNullable(expenseEntity)
                .map(e -> new ExpenseDto()
                        .setId(e.getId())
                        .setMarketing(e.getMarketing())
                        .setEquipment(e.getEquipment())
                        .setPayroll(e.getPayroll())
                        .setUtilities(e.getUtilities())
                        .setMaterial(e.getMaterial())
                        .setInsurance(e.getInsurance()))
                .orElse(null);
    }

    static InvestmentDto convertInvestmentEntity(InvestmentEntity investmentEntity) {
        return Optional.ofNullable(investmentEntity)
                .map(i -> new InvestmentDto()
                        .setId(i.getId())
                        .setInvestorsCount(i.getInvestorsCount())
                        .setPercentageOfInvestedClients(i.getPercentageOfInvestedClients()))
                .orElse(null);
    }
}
