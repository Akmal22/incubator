package kz.incubator.backend.service;

import kz.incubator.backend.Converter;
import kz.incubator.backend.entity.data.CountryEntity;
import kz.incubator.backend.entity.data.IncubatorEntity;
import kz.incubator.backend.entity.user.Role;
import kz.incubator.backend.entity.user.UserEntity;
import kz.incubator.backend.repo.CountryRepository;
import kz.incubator.backend.repo.IncubatorRepository;
import kz.incubator.backend.repo.UserRepository;
import kz.incubator.backend.service.dto.ServiceResult;
import kz.incubator.backend.service.dto.incubator.IncubatorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final CountriesService countriesService;

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
                .map(Converter::convertIncubatorEntity)
                .collect(Collectors.toList());
    }

    public List<IncubatorDto> getManagerIncubatorsPageable(String managerLogin, Pageable pageable) {
        UserEntity manager = userRepository.findByUsernameAndRole(managerLogin, Role.ROLE_BI_MANAGER)
                .orElseThrow(() -> {
                    log.error("Manager with login {} not found", managerLogin);
                    throw new UsernameNotFoundException("User not found");
                });

        return incubatorRepository.findAllByManager(manager, pageable).stream()
                .map(Converter::convertIncubatorEntity)
                .collect(Collectors.toList());
    }

    public List<IncubatorDto> getAllIncubators(Pageable pageable) {
        return incubatorRepository.findAll(pageable).stream()
                .map(Converter::convertIncubatorEntity)
                .collect(Collectors.toList());
    }

    private IncubatorEntity getIncubator(long id) {
        return incubatorRepository.findById(id).orElseThrow(() ->
        {
            log.error("Incubator with id [{}] not found", id);
            throw new IllegalArgumentException("Incubator not oun");
        });
    }
}
