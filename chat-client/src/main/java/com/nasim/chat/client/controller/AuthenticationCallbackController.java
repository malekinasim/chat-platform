package com.nasim.chat.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationCallbackController {

    @GetMapping("/auth/callback")
    public String authenticationCallback() {
        return "forward:/chatClient.html";
    }
}