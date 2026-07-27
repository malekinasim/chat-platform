package com.nasim.chat.auth_service.service;

public interface RoleService {

    void createRoleIfNotExists(String roleName, String description);
}