package com.nasim.chat.auth_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Table(name = "user_identities",uniqueConstraints={
        @UniqueConstraint(name="uk_identity_issuer_subject",
                columnNames = {"issuer","subject"})
}
)
@Getter
@Setter
public class UserIdentities extends BaseEntity<UUID> {

    @Column(name = "issuer",nullable = false,length = 500)
    private String issuer;
    @Column(name = "subject",nullable = false,length = 500)
    private String subject;
    @Column(name = "provider",nullable = false,length = 50)
    private String provider;
    @Column(name = "provider_email",length = 320)
    private String providerEmail;
    @ManyToOne(targetEntity = AppUsers.class,fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUsers appUsers;
}
