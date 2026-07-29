package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {

    Optional<AppUser> findByPhoneNumber(String phoneNumber);
}
