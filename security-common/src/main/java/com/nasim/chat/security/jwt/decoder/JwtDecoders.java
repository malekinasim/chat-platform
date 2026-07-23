package com.nasim.chat.security.jwt.decoder;

import com.nasim.chat.security.JwtSecurityProperties;
import com.nasim.chat.security.jwt.validators.JwtAudienceValidator;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPublicKey;

public final class JwtDecoders {

    private JwtDecoders() {
    }

    public static JwtDecoder create(
            RSAPublicKey publicKey,
            JwtSecurityProperties properties) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withPublicKey(publicKey).build();

        OAuth2TokenValidator<Jwt>  defaultWithIssuer= JwtValidators
                .createDefaultWithIssuer(properties.getIssuer());

        OAuth2TokenValidator<Jwt> audValidator=new JwtAudienceValidator(properties
                .getAudience());
        OAuth2TokenValidator<Jwt> validatorChain=new DelegatingOAuth2TokenValidator<>(
              defaultWithIssuer,
              audValidator
        );

        decoder.setJwtValidator(validatorChain);

        return decoder;
    }
}