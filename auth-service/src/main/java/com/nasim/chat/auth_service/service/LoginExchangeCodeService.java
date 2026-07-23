package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.InternalUser;

import java.util.List;

public interface LoginExchangeCodeService {
    public String create(String id, List<String> roles);

}
