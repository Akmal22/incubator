package kz.incubator.backend.repo;

import kz.incubator.backend.entity.data.IncubatorEntity;
import kz.incubator.backend.entity.data.IncubatorProjectEntity;
import kz.incubator.backend.entity.user.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncubatorProjectRepository extends JpaRepository<IncubatorProjectEntity, Long> {
    List<IncubatorProjectEntity> findAllByIncubator(IncubatorEntity incubator);

    Optional<IncubatorProjectEntity> findByName(String name);

    @Query("select i from IncubatorProjectEntity i join fetch i.incubator where i.name = :name")
    Optional<IncubatorProjectEntity> findByNameFetchIncubator(String name);

    @Query("select i from IncubatorProjectEntity i " +
            "where lower(i.name) like lower(concat('%', :filterText, '%'))")
    List<IncubatorProjectEntity> findAllByFilterText(String filterText);

    @Query("select i from IncubatorProjectEntity i " +
            "where lower(i.name) like lower(concat('%', :filterText, '%'))")
    List<IncubatorProjectEntity> findAllByFilterText(String filterText, Pageable pageable);

    @Query("select i from IncubatorProjectEntity i " +
            "where i.incubator.manager = :manager " +
            "and lower(i.name) like lower(concat('%', :filterText, '%'))")
    List<IncubatorProjectEntity> findManagerProjectsByFilterText(UserEntity manager, String filterText, Pageable pageable);
}
