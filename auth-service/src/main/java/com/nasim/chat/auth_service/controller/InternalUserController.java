package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.model.dto.GeneralUserDetails;
import com.nasim.chat.auth_service.service.AppUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    private final AppUserService appUserService;

    public InternalUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/active/ids")
    public List<String> findAllActiveUserIds() {
        return appUserService.findAllActiveIds();
    }

    @PostMapping("/active/ids/filter")
    public List<String> findAllValidUserIds(@RequestBody List<Long> ids) {
        return appUserService.findAllActiveIdsById(ids);
    }

    @PostMapping("/details")
    public List<GeneralUserDetails> findUserDetails(
            @RequestBody @Valid @NotEmpty List<@Positive Long> ids
    ) {
        return appUserService.findUserDetails(ids);
    }

    @GetMapping("/{userId}/exists")
    public boolean userExists(@PathVariable Long userId) {
        return appUserService.activeUserExists(userId);
    }
}
