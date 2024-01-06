package com.example.incubator.backend.repo;

import com.example.incubator.backend.entity.data.ClientsEntity;
import com.example.incubator.backend.entity.data.IncubatorProjectEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientsRepository extends BiDataRepository<ClientsEntity> {
    @Query("select c from ClientsEntity c join fetch c.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<ClientsEntity> findAllByProjectName(String filterText);
}
