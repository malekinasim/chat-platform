package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.PendingRegistration;
import com.nasim.chat.auth_service.model.dto.GeneralUserDetails;
import com.nasim.chat.auth_service.model.entity.AppUser;
import jakarta.validation.constraints.Pattern;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AppUserService {

    AppUser findById(Long userId);

    @Transactional(rollbackFor = Exception.class)
    AppUser createAppUser(String name, String email, String phoneNumber);

    AppUser completeRegistration( String phoneNumber, PendingRegistration userInfo);
    

    List<String> findAllActiveIds();

    List<String> findAllActiveIdsById(List<Long> ids);

    List<GeneralUserDetails> findUserDetails(List<Long> ids);

    boolean activeUserExists(Long userId);
}
