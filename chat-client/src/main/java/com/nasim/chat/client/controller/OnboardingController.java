package com.nasim.chat.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OnboardingController {

    @GetMapping("/onboarding/phone")
    public String showPhonePage() {
        return "forward:/onboarding/phone.html";
    }
}
