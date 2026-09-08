package com.nasim.chat.auth_service.model.entity;

import com.nasim.chat.auth_service.model.convertor.AudienceListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "registered_client",uniqueConstraints = {@UniqueConstraint(name = "uk_client_id",columnNames = "client_id")})
@Getter
@Setter
public class AppRegisteredClient extends BaseEntity<Integer>{


    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;
    @Convert(converter = AudienceListConverter.class)
    @Column(name = "audience", nullable = false)
    private List<String> audience;

    @Column(name = "callback_url", nullable = false)
    private String callbackUrl;

    @Column(name = "onboarding_url", nullable = false)
    private String onboardingUrl;

    @Column(name = "origin_url", nullable = false)
    private String origin;

}