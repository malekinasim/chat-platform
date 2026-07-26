package com.nasim.chat.auth_service.service.impl;

import com.nasim.chat.auth_service.exceptions.InvalidExchangeCodeException;
import com.nasim.chat.auth_service.model.dto.LoginExchangeCode;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryLoginExchangeCodeService implements LoginExchangeCodeService {
    private final Map<String, LoginExchangeCode> exchangeCodes = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String create(String userId,String clientId, List<String> roles,List<String> allowedAudiences) {
        LoginExchangeCode loginData = new LoginExchangeCode(
                userId,
                clientId,
                List.copyOf(roles),
                allowedAudiences,
                Instant.now().plusSeconds(60)
        );

        String code;

        do {
            code = generateCode();
        } while (exchangeCodes.putIfAbsent(code, loginData) != null);

        return code;
    }


    private String generateCode() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    @Override
    public LoginExchangeCode consume(String code) {
        LoginExchangeCode loginData = exchangeCodes.remove(code);

        if (loginData == null) {
            throw new InvalidExchangeCodeException();
        }

        if (loginData.expiresAt().isBefore(Instant.now())) {
            throw new InvalidExchangeCodeException();
        }

        return loginData;
    }
}
