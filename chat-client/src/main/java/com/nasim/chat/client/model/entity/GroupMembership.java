package com.nasim.chat.client.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;

@Entity
@Table(name = "chat_group_member",uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_group",columnNames = {"user_id","group_id"})
})
@Getter
public class GroupMembership extends BaseEntity<Long>{
    @Column(name = "user_id",nullable = false)
    private String userId;
    @ManyToOne(targetEntity = ChatGroup.class,optional = false)
    @JoinColumn(name = "group_id")
    private ChatGroup group;
    @Enumerated(EnumType.STRING)
    private GroupRole role;
    @Column(name = "join_at",nullable = false)
    private Instant joinedAt;
}
