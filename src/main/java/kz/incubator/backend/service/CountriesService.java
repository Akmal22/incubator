package kz.incubator.backend.service;

import kz.incubator.backend.Converter;
import kz.incubator.backend.entity.data.CountryEntity;
import kz.incubator.backend.repo.CountryRepository;
import kz.incubator.backend.service.dto.CountryDto;
import kz.incubator.backend.service.dto.ServiceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CountriesService {
    private final CountryRepository countryRepository;

    public List<String> getCountryNames() {
        return countryRepository.findAll().stream()
                .map(CountryEntity::getName)
                .collect(Collectors.toList());
    }

    public List<CountryDto> getAllCountries(String filterText) {
        return countryRepository.findAllByFilterText(filterText).stream()
                .map(Converter::convertCountryEntity)
                .collect(Collectors.toList());
    }

    public List<CountryDto> pageSearch(String filterText, Pageable pageable) {
        return countryRepository.findAllByFilterTextPageable(filterText, pageable).stream()
                .map(Converter::convertCountryEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteCountry(CountryDto countryDto) {
        Optional<CountryEntity> country = countryRepository.findById(countryDto.getId());

        if (country.isEmpty()) {
            log.error("Country with id [{}] not found", countryDto.getId());
            return new ServiceResult(false, "Country not found");
        }

        countryRepository.delete(country.get());

        return new ServiceResult(true, null);
    }

    public ServiceResult createCountry(CountryDto countryDto) {
        Optional<CountryEntity> optionalCountryEntity = countryRepository.findByName(countryDto.getCountryName());

        if (optionalCountryEntity.isPresent()) {
            log.error("Country with name [{}] already exists ", countryDto.getId());
            return new ServiceResult(false, "Country already exists");
        }

        CountryEntity country = new CountryEntity();
        country.setName(countryDto.getCountryName());
        countryRepository.save(country);

        return new ServiceResult(true, null);
    }

    public ServiceResult updateCountry(CountryDto countryDto) {
        Optional<CountryEntity> country = countryRepository.findById(countryDto.getId());

        if (country.isEmpty()) {
            log.error("Country with id [{}] not found", countryDto.getId());
            return new ServiceResult(false, "Country not found");
        }

        CountryEntity countryEntity = country.get();
        countryEntity.setName(countryDto.getCountryName());
        countryRepository.save(countryEntity);

        return new ServiceResult(true, null);
    }
}
