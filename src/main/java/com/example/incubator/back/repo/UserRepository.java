package com.example.incubator.back.repo;

import com.example.incubator.back.entity.Role;
import com.example.incubator.back.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUuid(String uuid);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    @Query("select u from UserEntity u " +
            "where u.role<>'ROLE_ADMIN' " +
            "and (lower(u.username) like lower(concat('%', :filterText, '%')) " +
            "or lower(u.email) like lower(concat('%', :filterText, '%')))   ")
    List<UserEntity> findAllByFilterText(String filterText);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.password = :password where u.username = :username")
    void updateUserPassword(String password, String username);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.email = :email, u.role= :role where u.uuid = :uuid")
    void updateUser(String email, Role role, String uuid);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.email = :email, u.role = :role, u.password = :password where u.uuid = :uuid")
    void updateUser(String email, Role role, String password, String uuid);
}
