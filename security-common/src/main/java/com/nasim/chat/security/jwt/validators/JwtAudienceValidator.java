package com.nasim.chat.security.jwt.validators;

import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;

public class JwtAudienceValidator  implements OAuth2TokenValidator<Jwt> {
    private final String requiredAudience;

    public JwtAudienceValidator(String requiredAudience) {
        this.requiredAudience = requiredAudience;
    }

    @Override
    public @NonNull OAuth2TokenValidatorResult validate(Jwt token) {
        if (Objects.requireNonNull(token.getAudience()).contains(requiredAudience)) {
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "The token does not contain the required audience: "
                        + requiredAudience,
                null
        );

        return OAuth2TokenValidatorResult.failure(error);
    }
}
