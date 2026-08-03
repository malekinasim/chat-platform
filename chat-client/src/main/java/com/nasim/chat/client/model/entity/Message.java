package com.nasim.chat.client.model.entity;

import com.nasim.chat.model.dto.DeliveryType;
import com.nasim.chat.model.dto.MessageContentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="message")
@Getter
@Setter
public class Message extends BaseEntity<Long>{
  @Column(name="text_content")
  private String textContent;
  @Column(name = "destination_id")
  private String destinationId;

  @OneToOne(fetch = FetchType.LAZY,targetEntity = Message.class)
  @JoinColumn(name = "reply_to_message_id")
  private Message replyToMessage;

  @Column(name = "sender_id",nullable = false)
  private String senderId;

  @Enumerated(EnumType.STRING)
  @Column(name = "content_type",nullable = false)
  private MessageContentType messageContentType;

  @Enumerated(EnumType.STRING)
  @Column(name = "delivery_type" ,nullable = false)
  private DeliveryType deliveryType;
}
