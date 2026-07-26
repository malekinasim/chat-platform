package com.nasim.chat.auth_service.repository;

import com.nasim.chat.auth_service.model.entity.RefreshTokenSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSession,Long> {

    @Query(value = "select token from RefreshTokenSession token " +
            "inner join token.user u " +
            "inner join token.client c " +
            "where u.id= :userId and c.clientId")
    Optional<RefreshTokenSession> findCurrentRefreshTokenByUserIdAndClientId(Long userId,String clientId);
}
