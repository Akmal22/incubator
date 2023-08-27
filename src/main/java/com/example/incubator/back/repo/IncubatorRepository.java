package com.example.incubator.back.repo;

import com.example.incubator.back.entity.data.CountryEntity;
import com.example.incubator.back.entity.data.IncubatorEntity;
import com.example.incubator.back.entity.user.UserEntity;
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

    Optional<IncubatorEntity> findByName(String name);

    List<IncubatorEntity> findAllByManager(UserEntity manager);
}
