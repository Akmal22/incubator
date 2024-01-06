package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.ConsumedResourcesEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsumedResourcesRepository extends BiDataRepository<ConsumedResourcesEntity> {
    @Query("select c from ConsumedResourcesEntity c join fetch c.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<ConsumedResourcesEntity> findAllByProjectName(String filterText);
}
