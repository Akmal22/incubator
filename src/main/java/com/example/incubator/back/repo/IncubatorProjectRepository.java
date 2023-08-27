package com.example.incubator.back.repo;

import com.example.incubator.back.entity.data.IncubatorEntity;
import com.example.incubator.back.entity.data.IncubatorProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncubatorProjectRepository extends JpaRepository<IncubatorProjectEntity, Long> {
    List<IncubatorProjectEntity> findAllByIncubator(IncubatorEntity incubator);

    Optional<IncubatorProjectEntity> findByName(String name);

    @Query("select i from IncubatorProjectEntity i " +
            "where lower(i.name) like lower(concat('%', :filterText, '%'))")
    List<IncubatorProjectEntity> findAllByFilterText(String filterText);
}
