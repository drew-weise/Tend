package com.drewweise.provisions.user.repository;

import com.drewweise.provisions.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
