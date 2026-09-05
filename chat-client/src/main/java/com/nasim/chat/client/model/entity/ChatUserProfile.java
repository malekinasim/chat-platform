package com.nasim.chat.client.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_user_profile")
@Getter
@Setter
public class ChatUserProfile {
    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "avatar_url")
    private String avatarUrl;
}
