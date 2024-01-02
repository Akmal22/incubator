package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.CountryEntity;
import com.example.incubator.backend.entity.data.IncubatorEntity;
import com.example.incubator.backend.entity.user.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncubatorRepository extends JpaRepository<IncubatorEntity, Long> {
    @Query("select i from IncubatorEntity i " +
            "where (lower(i.name) like lower(concat('%', :filterText, '%')) " +
            "or lower(i.founder) like lower(concat('%', :filterText, '%')))")
    List<IncubatorEntity> findAllByFilterText(String filterText);

    List<IncubatorEntity> findAllByCountry(CountryEntity country);

    @Query("SELECT i from IncubatorEntity i join fetch i.incubatorProjects where i.country = :country")
    List<IncubatorEntity> findAllByCountryFetchIncubators(CountryEntity country);

    Optional<IncubatorEntity> findByName(String name);

    List<IncubatorEntity> findAllByManager(UserEntity manager, Pageable pageable);
}
