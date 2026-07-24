package com.nasim.chat.auth_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles",uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_name",columnNames = "name")
})
@Getter
@Setter
public class Roles extends BaseEntity<UUID> {
    @Column(name = "name",nullable = false,length = 50)
    private String name;
    @Column(name = "description",length = 500)
    private String description;
    @ManyToMany(targetEntity = AppUsers.class,fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<AppUsers> members;
}
