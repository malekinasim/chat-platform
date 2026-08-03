package com.nasim.chat.client.model.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "message_attachment")
@Getter
@Setter
public class MessageAttachment {
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Message.class)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    @Column(name = "storage_key",unique = true,nullable = false)
    private String storageKey;
    @Column(name = "original_file_name",unique = true,nullable = false)
    private String fileName;
    @Column(name = "content_type",nullable = false)
    private String contentType;
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

}
