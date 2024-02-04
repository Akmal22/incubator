package kz.incubator.backend.repo;

import kz.incubator.backend.entity.data.ExpenseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends BiDataRepository<ExpenseEntity> {
    @Query("select e from ExpenseEntity e join fetch e.projectEntity p where lower(p.name) like lower(concat('%', :filterText, '%'))")
    List<ExpenseEntity> findAllByProjectName(String filterText);
}
