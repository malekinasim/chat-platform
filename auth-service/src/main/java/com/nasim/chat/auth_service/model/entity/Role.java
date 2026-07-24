package com.nasim.chat.auth_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles",uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_name",columnNames = "name")
})
@Getter
@Setter
public class Role extends BaseEntity<Integer> {
    @Column(name = "name",nullable = false,length = 50)
    private String name;
    @Column(name = "description",length = 500)
    private String description;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<AppUser> members = new HashSet<>();
}
