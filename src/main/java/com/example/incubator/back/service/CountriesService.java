package com.example.incubator.back.service;

import com.example.incubator.back.entity.data.CountryEntity;
import com.example.incubator.back.repo.CountryRepository;
import com.example.incubator.back.service.dto.ServiceResult;
import com.example.incubator.back.service.dto.country.CountryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                .map(CountriesService::convertCountryEntity)
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

    public static CountryDto convertCountryEntity(CountryEntity country) {
        CountryDto countryDto = new CountryDto();
        countryDto.setId(country.getId());
        countryDto.setCountryName(country.getName());

        return countryDto;
    }
}
