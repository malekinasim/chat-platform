package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.model.entity.AppUser;
import com.nasim.chat.auth_service.service.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    private AppUserService appUserService;
    @PostMapping("/active/ids")
    public List<Long> findAllActiveUserIds(List<Long> ids) {
        if(ids==null || ids.isEmpty() )
            return appUserService.findAll();
        else
            return appUserService.findAllById(ids);
    }
    @GetMapping("/{receiver}")
    public boolean userExists(@PathVariable Long receiver) {
        return appUserService.existsByUsername(receiver);
    }
}