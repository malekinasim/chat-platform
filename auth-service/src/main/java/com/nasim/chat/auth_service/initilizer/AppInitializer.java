package com.nasim.chat.auth_service.initilizer;

import com.nasim.chat.auth_service.model.entity.Status;
import com.nasim.chat.auth_service.service.AppRegisterClientService;
import com.nasim.chat.auth_service.service.RoleService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppInitializer implements ApplicationRunner {

    private final RoleService roleService;
    private final AppRegisterClientService appRegisterClientService;

    public AppInitializer(RoleService roleService, AppRegisterClientService appRegisterClientService) {
        this.roleService = roleService;
        this.appRegisterClientService = appRegisterClientService;
    }

    @Override
    public void run(ApplicationArguments args) {
        roleService.createRoleIfNotExists("USER",
                "Default User Role");
        appRegisterClientService.registerClientIfNotExists(
               "chat-client","chat-client",
                "http://localhost:8082/auth/callback",
        "http://localhost:8082/onboarding/phone", Status.ACTIVE);
    }
}