package com.nasim.chat.auth_service.service;

import com.nasim.chat.auth_service.model.dto.AuthenticationTokens;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;

public interface TokenService {
    AuthenticationTokens generatesAuthenticationTokens(LoginExchangeCode loginData);
    AuthenticationTokens generatesAuthenticationTokens(String rawRefreshToken);

}
