package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.ConsumedResourcesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumedResourcesRepository extends JpaRepository<ConsumedResourcesEntity, Long> {
}
