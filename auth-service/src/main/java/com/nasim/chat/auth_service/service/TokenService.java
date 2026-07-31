package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.AuthenticationTokens;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.model.entity.AppUser;

import java.security.NoSuchAlgorithmException;

public interface TokenService {
    AuthenticationTokens generatesAuthenticationTokens(LoginExchangeCode loginData);
    AuthenticationTokens generatesAuthenticationTokens(String rawRefreshToken);
    void revokeRefreshToken(String rawRefreshToken);
    AuthenticationTokens generatesAuthenticationTokens(AppUser user, AppRegisteredClient client) ;
}
