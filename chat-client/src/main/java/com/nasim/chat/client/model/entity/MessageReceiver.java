package com.nasim.chat.client.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "message_receiver", uniqueConstraints = {@UniqueConstraint(name = "uk_message_reciver", columnNames = {"receiver_id", "message_id"})})
@Getter
@Setter
public class MessageReceiver extends BaseEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Message.class)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;
    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_status", nullable = false)
    private ReceiverStatus receiverStatus;
    @Column(name = "delivered_at")
    private Instant deliveredAt;
    @Column(name = "send_at")
    private Instant sendAt;
    @Column(name = "read_at")
    private Instant readAt;
}
