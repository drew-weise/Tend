package com.drewweise.provisions.user.repository;

import com.drewweise.provisions.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByCognitoUserId(String cognitoUserId);
}
