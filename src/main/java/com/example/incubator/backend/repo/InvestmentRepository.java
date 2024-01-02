package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import com.example.incubator.backend.entity.data.InvestmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<InvestmentEntity, Long> {
    @Query("select i from InvestmentEntity i join fetch i.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<InvestmentEntity> findAllByProjectName(String filterText);

    Optional<InvestmentEntity> findByProjectEntity(IncubatorProjectEntity project);
}
