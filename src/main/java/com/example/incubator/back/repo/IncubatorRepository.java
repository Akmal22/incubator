package com.example.incubator.back.repo;

import com.example.incubator.back.entity.CountryEntity;
import com.example.incubator.back.entity.IncubatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncubatorRepository extends JpaRepository<IncubatorEntity, Long> {
    List<IncubatorEntity> findAllByCountry(CountryEntity country);

    Optional<IncubatorEntity> findByName(String name);
}
