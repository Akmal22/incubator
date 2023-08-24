package com.example.incubator.back.repo;

import com.example.incubator.back.entity.IncubatorEntity;
import com.example.incubator.back.entity.IncubatorProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncubatorProjectRepository extends JpaRepository<IncubatorProjectEntity, Long> {
    List<IncubatorProjectEntity> findAllByIncubator(IncubatorEntity incubator);

    Optional<IncubatorProjectEntity> findByName(String name);
}
