package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.InvestmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<InvestmentEntity, Long> {
}
