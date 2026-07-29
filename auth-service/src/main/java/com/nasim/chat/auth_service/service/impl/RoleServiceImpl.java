package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.model.entity.Role;
import com.nasim.chat.auth_service.repository.RoleRepository;
import com.nasim.chat.auth_service.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Override
    public Role createRoleIfNotExists(String roleName, String description) {
        return roleRepository.findByName(roleName).orElseGet(()->{
            Role role = new Role();
            role.setName(roleName);
            if(StringUtils.hasText(description))
                role.setDescription(description);
            roleRepository.save(role);
            return  role;
        });
    }
}