package com.nasim.chat.auth_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "registered_client")
@Getter
@Setter
public class RegisteredClient extends BaseEntity<Integer>{


    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(nullable = false)
    private String audience;

    @Column(name = "callback_url", nullable = false)
    private String callbackUrl;

    @Column(name = "onboarding_url", nullable = false)
    private String onboardingUrl;

    @Column(nullable = false)
    private boolean active;
}