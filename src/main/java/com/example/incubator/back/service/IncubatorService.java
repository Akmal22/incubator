package com.example.incubator.back.service;

import com.example.incubator.back.entity.data.CountryEntity;
import com.example.incubator.back.entity.data.IncubatorEntity;
import com.example.incubator.back.entity.user.UserEntity;
import com.example.incubator.back.repo.CountryRepository;
import com.example.incubator.back.repo.IncubatorProjectRepository;
import com.example.incubator.back.repo.IncubatorRepository;
import com.example.incubator.back.repo.UserRepository;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.back.service.dto.report.IncubatorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class IncubatorService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final IncubatorRepository incubatorRepository;
    private final IncubatorProjectRepository incubatorProjectRepository;

    public ServiceResult createIncubator(IncubatorDto incubatorDto) {
        String incubatorName = incubatorDto.getIncubatorName();
        Optional<IncubatorEntity> optionalIncubatorEntity = incubatorRepository.findByName(incubatorName);
        if (optionalIncubatorEntity.isPresent()) {
            log.error("Error while creating incubator, there is already existing incubator with name [{}]", incubatorName);
            return new ServiceResult(false, "Incubator with specified name already exists");
        }

        CountryEntity country = countryRepository.findById(incubatorDto.getCountry().getId())
                .orElseThrow(() -> {
                    log.error("Country with id [{}] not found", incubatorDto.getCountry().getId());
                    throw new IllegalArgumentException("Country not found");
                });

        UserEntity manager = userRepository.findByUuid(incubatorDto.getManager().getUuid())
                .orElseThrow(() -> {
                    log.error("User with uuid [{}] not found", incubatorDto.getManager().getUuid());
                    throw new IllegalArgumentException("Manager not found");
                });

        IncubatorEntity incubator = new IncubatorEntity();
        incubator.setName(incubatorName);
        incubator.setCountry(country);
        incubator.setManager(manager);
        incubator.setFounder(incubatorDto.getFounder());
        incubator.setFounded(incubatorDto.getFounded());

        incubatorRepository.save(incubator);
        return new ServiceResult(true, null);
    }

    public ServiceResult updateIncubator(IncubatorDto incubatorDto) {
        IncubatorEntity incubator = getIncubator(incubatorDto.getId());

        CountryEntity country = countryRepository.findById(incubatorDto.getCountry().getId())
                .orElseThrow(() -> {
                    log.error("Country with id [{}] not found", incubatorDto.getCountry().getId());
                    throw new IllegalArgumentException("Country not found");
                });

        UserEntity manager = userRepository.findByUuid(incubatorDto.getManager().getUuid())
                .orElseThrow(() -> {
                    log.error("User with uuid [{}] not found", incubatorDto.getManager().getUuid());
                    throw new IllegalArgumentException("Manager not found");
                });

        incubator.setName(incubatorDto.getIncubatorName());
        incubator.setCountry(country);
        incubator.setManager(manager);
        incubator.setFounder(incubatorDto.getFounder());
        incubator.setFounded(incubatorDto.getFounded());
        incubatorRepository.save(incubator);

        return new ServiceResult(true, null);
    }

    public ServiceResult deleteIncubator(IncubatorDto incubatorDto) {
        IncubatorEntity incubator = getIncubator(incubatorDto.getId());
        incubatorRepository.delete(incubator);

        return new ServiceResult(true, null);
    }

    public List<IncubatorDto> getIncubatorsByFilterText(String filterText) {
        return incubatorRepository.findAllByFilterText(filterText).stream()
                .map(this::convertIncubatorEntity)
                .collect(Collectors.toList());
    }

    private IncubatorEntity getIncubator(long id) {
        return incubatorRepository.findById(id).orElseThrow(() ->
        {
            log.error("Incubator with id [{}] not found", id);
            throw new IllegalArgumentException("Incubator not oun");
        });
    }

    private IncubatorDto convertIncubatorEntity(IncubatorEntity incubator) {
        return new IncubatorDto()
                .setId(incubator.getId())
                .setIncubatorName(incubator.getName())
                .setCountry(CountriesService.convertCountryEntity(incubator.getCountry()))
                .setManager(UserService.convertUserEntity(incubator.getManager()))
                .setFounded(incubator.getFounded())
                .setFounder(incubator.getFounder());
    }
}
