package com.example.incubator.back.service;

import com.example.incubator.back.entity.data.CountryEntity;
import com.example.incubator.back.repo.CountryRepository;
import com.example.incubator.back.service.dto.form.country.EditCountryDto;
import com.example.incubator.back.service.dto.ServiceResult;
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

    public List<EditCountryDto> getAllCountries(String filterText) {
        return countryRepository.findAll().stream()
                .map(this::convertCountryEntity)
                .collect(Collectors.toList());
    }

    public ServiceResult deleteCountry(EditCountryDto editCountryDto) {
        Optional<CountryEntity> country = countryRepository.findById(editCountryDto.getId());

        if (country.isEmpty()) {
            return new ServiceResult(false, "Country not found");
        }

        countryRepository.delete(country.get());

        return new ServiceResult(true, null);
    }

    public ServiceResult createCountry(EditCountryDto editCountryDto) {
        CountryEntity country = new CountryEntity();
        country.setName(editCountryDto.getCountryName());
        countryRepository.save(country);

        return new ServiceResult(true, null);
    }

    public ServiceResult updateCountry(EditCountryDto editCountryDto) {
        Optional<CountryEntity> country = countryRepository.findById(editCountryDto.getId());

        if (country.isEmpty()) {
            return new ServiceResult(false, "Country not found");
        }

        CountryEntity countryEntity = country.get();
        countryEntity.setName(editCountryDto.getCountryName());
        countryRepository.save(countryEntity);

        return new ServiceResult(true, null);
    }

    private EditCountryDto convertCountryEntity(CountryEntity country) {
        EditCountryDto editCountryDto = new EditCountryDto();
        editCountryDto.setId(country.getId());
        editCountryDto.setCountryName(country.getName());

        return editCountryDto;
    }
}
