package com.nasim.chat.client.websocket;

import com.nasim.chat.client.socket.client.TcpChatGateway;
import com.nasim.chat.model.ChatMessageDto;
import com.nasim.chat.model.OutgoingChatRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
@Controller
public class ChatBrowserController {
    private final TcpChatGateway tcpChatGateway;

    public ChatBrowserController(TcpChatGateway tcpChatGateway) {
        this.tcpChatGateway = tcpChatGateway;
    }
    @MessageMapping("/chat/public")
    public void sendPublic(ChatMessageDto messageDto, Principal principal) {
          //TODO return fix it after config JWT and OIDC
        tcpChatGateway.send(OutgoingChatRequest.broadcastText(
                principal!=null ? principal.getName():"test-user", messageDto.text()));
    }

    @MessageMapping("/chat/room/{room-code}")
    public void sendToRoom(ChatMessageDto messageDto, @DestinationVariable("room-code") String roomCode, Principal principal) {
        //TODO return fix it after config JWT and OIDC
        tcpChatGateway.send(OutgoingChatRequest.groupText(
                principal!=null ? principal.getName():"test-user",roomCode, messageDto.text()));
    }
}
