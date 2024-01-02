package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.RevenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevenueRepository extends JpaRepository<RevenueEntity, Long> {
}
