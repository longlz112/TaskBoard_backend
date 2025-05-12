package com.longlz.taskboard.repository;

import com.longlz.taskboard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String Username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
