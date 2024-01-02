package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.CountryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByName(String name);

    @Query("select c from CountryEntity c " +
            "where lower(c.name) like lower(concat('%', :filterText, '%'))")
    List<CountryEntity> findAllByFilterText(String filterText);


    @Query("select c from CountryEntity c " +
            "where lower(c.name) like lower(concat('%', :filterText, '%'))")
    List<CountryEntity> findAllByFilterTextPageable(String filterText, Pageable pageable);
}
