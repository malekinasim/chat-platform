package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.service.AppRegisterClientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@RestController
@RequestMapping("/auth")
public class LoginController {
    private  final AppRegisterClientService registerClientService;

    public LoginController(AppRegisterClientService registerClientService) {
        this.registerClientService = registerClientService;
    }

    @GetMapping("/login")
    public void login(
            @RequestParam("app_client_id") String appClientId,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {


        AppRegisteredClient appRegisterClient= registerClientService.
                findActiveClient(appClientId).orElseThrow(
                        ()-> new CustomException("invalid client id ","INVALID_CLIENT_ID")
        );

        request.getSession()
                .setAttribute("APP_CLIENT_ID", appRegisterClient.getClientId());

        response.sendRedirect("/oauth2/authorization/google");
    }
}
