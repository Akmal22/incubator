package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.entity.data.RevenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueRepository extends JpaRepository<RevenueEntity, Long> {
    @Query("select r from RevenueEntity r join fetch r.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<RevenueEntity> findAllByProjectName(String filterText);

    Optional<RevenueEntity> findByProjectEntity(IncubatorProjectEntity project);
}
