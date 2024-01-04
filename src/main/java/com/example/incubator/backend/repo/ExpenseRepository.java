package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.ExpenseEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    @Query("select e from ExpenseEntity e join fetch e.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<ExpenseEntity> findAllByProjectName(String filterText);

    Optional<ExpenseEntity> findByProjectEntity(IncubatorProjectEntity project);
}
