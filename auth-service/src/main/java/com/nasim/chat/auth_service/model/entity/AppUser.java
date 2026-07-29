package com.nasim.chat.auth_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name ="app_users",uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_phone_number",columnNames = {"phone_number"})})
@Getter
@Setter
public class AppUser extends BaseEntity<Long>{
    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "display_name", length = 200)
    private String displayName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "phone_verified_at")
    private Instant phoneVerifiedAt;

    @ManyToMany(targetEntity = Role.class,fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles= new HashSet<>();

}
