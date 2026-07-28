package com.nasim.chat.client.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_group", uniqueConstraints = {@UniqueConstraint(name = "uk-chat-group-code",columnNames = "code")})
@Getter
@Setter
public class ChatGroup extends BaseEntity<Long>{
    @Column(name = "name", length = 200,nullable = false)
    private String groupName;
    @Column(name = "code", length = 50,nullable = false)
    private String groupCode;
}
