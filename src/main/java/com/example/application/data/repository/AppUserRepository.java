package com.example.application.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.data.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsernameIgnoreCase(String username);
}
