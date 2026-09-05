package com.nasim.chat.auth_service.controller;

import com.nasim.chat.auth_service.model.dto.GeneralUserDetails;
import com.nasim.chat.auth_service.service.AppUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {
    public static final int MAX_USER_DETAILS_BATCH_SIZE = 100;

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
            @RequestBody @Valid @NotEmpty
            @Size(max = MAX_USER_DETAILS_BATCH_SIZE,
                    message = "at most " + MAX_USER_DETAILS_BATCH_SIZE + " user IDs may be requested")
            List<@Positive Long> ids
    ) {
        return appUserService.findUserDetails(ids);
    }

    @GetMapping("/{userId}/exists")
    public boolean userExists(@PathVariable Long userId) {
        return appUserService.activeUserExists(userId);
    }
}
