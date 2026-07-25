package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.repository.RoleRepository;
import com.nasim.chat.auth_service.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            if(StringUtils.hasText(description))
                role.setDescription(description);
            roleRepository.save(role);
        }
    }
}