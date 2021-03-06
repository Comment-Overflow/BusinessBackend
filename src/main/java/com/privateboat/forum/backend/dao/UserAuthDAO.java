package com.privateboat.forum.backend.dao;

import com.privateboat.forum.backend.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthDAO extends JpaRepository<UserAuth, Long> {
    Boolean existsByEmail(String email);

    Optional<UserAuth> findByEmail(String email);
}
