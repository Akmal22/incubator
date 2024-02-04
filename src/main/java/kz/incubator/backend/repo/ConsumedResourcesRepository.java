package kz.incubator.backend.repo;

import kz.incubator.backend.entity.data.ConsumedResourcesEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumedResourcesRepository extends BiDataRepository<ConsumedResourcesEntity> {
    @Query("select c from ConsumedResourcesEntity c join fetch c.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<ConsumedResourcesEntity> findAllByProjectName(String filterText);
}
