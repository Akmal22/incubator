package com.example.incubator.back.repo;

import com.example.incubator.back.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.password = :password where u.username = :username")
    void updateUserPassword(String password, String username);
}
