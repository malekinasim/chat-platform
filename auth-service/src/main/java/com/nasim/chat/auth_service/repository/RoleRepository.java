package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {
}
