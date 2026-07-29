package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.entity.Role;

public interface RoleService {

    Role createRoleIfNotExists(String roleName, String description);
}