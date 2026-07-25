package com.nasim.chat.auth_service.initilizer;

import com.nasim.chat.auth_service.service.RoleService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements ApplicationRunner {

    private final RoleService roleService;

    public RoleInitializer(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) {
        roleService.createRoleIfNotExists("ROLE_USER","Default User Role");
    }
}