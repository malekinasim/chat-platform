package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSession,Long> {

    @Query("""

            select token
        from RefreshTokenSession token
        where token.user.id = :userId
          and token.client.clientId = :clientId
          and token.revokedAt is null
          and token.expiresAt > CURRENT_TIMESTAMP
        """)
    Optional<RefreshTokenSession> findCurrentRefreshTokenByUserIdAndClientId(
            @Param("userId") Long userId,
            @Param("clientId") String clientId
    );
    @Query("""
            select token
        from RefreshTokenSession token
        where token.tokenHash = :tokenHash
          and token.revokedAt is null
          and token.expiresAt > CURRENT_TIMESTAMP
        """)
    Optional<RefreshTokenSession> findNonExpiredByHashToken(@Param("tokenHash") String tokenHash);
}