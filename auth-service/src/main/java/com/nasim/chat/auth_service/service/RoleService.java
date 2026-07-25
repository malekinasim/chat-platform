package com.nasim.chat.auth_service.service;

import org.springframework.transaction.annotation.Transactional;

public interface RoleService {

    void createRoleIfNotExists(String roleName, String description);
}