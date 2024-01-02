package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.ConsumedResourcesEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsumedResourcesRepository extends JpaRepository<ConsumedResourcesEntity, Long> {
    @Query("select c from ConsumedResourcesEntity c join fetch c.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<ConsumedResourcesEntity> findAllByProjectName(String filterText);

    Optional<ConsumedResourcesEntity> findByProjectEntity(IncubatorProjectEntity project);
}
