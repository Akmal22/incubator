package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.RevenueEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevenueRepository extends BiDataRepository<RevenueEntity> {
    @Query("select r from RevenueEntity r join fetch r.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<RevenueEntity> findAllByProjectName(String filterText);
}
