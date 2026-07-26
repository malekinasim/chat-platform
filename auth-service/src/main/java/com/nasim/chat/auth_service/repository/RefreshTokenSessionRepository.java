package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSession,Long> {
}
