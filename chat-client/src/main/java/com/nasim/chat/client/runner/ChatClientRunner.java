package com.nasim.chat.client.runner;


import com.nasim.chat.client.socket.client.ChatClientReceiver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class ChatClientRunner implements CommandLineRunner {


    private final ChatClientReceiver chatClientReceiver;

    public ChatClientRunner(ChatClientReceiver chatClientReceiver) {
        this.chatClientReceiver = chatClientReceiver;
    }
    @Override
    public void run(String... args) {

            chatClientReceiver.startListeningAsync();

            System.out.println("chat client started.");
    }


}