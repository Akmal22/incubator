package kz.incubator.backend.repo;

import kz.incubator.backend.entity.data.IncubatorProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BiDataRepository<T> extends JpaRepository<T, Long> {
    List<T> findAllByProjectName(String filterText);

    Optional<T> findByProjectEntity(IncubatorProjectEntity incubatorProjectEntity);
}
