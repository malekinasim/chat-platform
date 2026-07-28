package com.nasim.chat.client.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;

@Entity
@Table(name = "chat-group-member",uniqueConstraints = {
        @UniqueConstraint(name = "uk-user-group",columnNames = {"user-id","group-id"})
})
@Getter
public class GroupMembership extends BaseEntity<Long>{
    @Column(name = "user-id",nullable = false)
    private String userId;
    @ManyToOne(targetEntity = ChatGroup.class,optional = false)
    @JoinColumn(name = "group-id")
    private ChatGroup group;

    @Enumerated(EnumType.STRING)
    private GroupRole role;
    @Column(name = "join-at",nullable = false)
    private Instant joinedAt;
    @Column(name = "active",nullable = false, length = 1)
    private boolean active;
}
