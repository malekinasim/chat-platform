package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {

    Optional<AppUser> findByPhoneNumber(String phoneNumber);

    List<AppUser> findAllByActiveTrue();

    List<AppUser> findAllByIdInAndActiveTrue(List<Long> ids);

    boolean existsByIdAndActiveTrue(Long id);
}
