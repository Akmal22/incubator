package kz.incubator.backend.repo;

import kz.incubator.backend.entity.data.ClientsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientsRepository extends BiDataRepository<ClientsEntity> {
    @Query("select c from ClientsEntity c join fetch c.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<ClientsEntity> findAllByProjectName(String filterText);
}
