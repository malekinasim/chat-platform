package com.nasim.chat.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class JwtSecurityProperties {

    private  final String issuer;

    private final String audience;

    private final Resource publicKeyResource ;

    public JwtSecurityProperties(@Value("${security.jwt.issuer}")
                                 String issuer,
                                 @Value("${security.jwt.audience}") String audience,
                                 @Value("${security.jwt.public-key-file}") Resource publicKeyResource) {
        this.issuer = issuer;
        this.audience = audience;
        this.publicKeyResource = publicKeyResource;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAudience() {
        return audience;
    }

    public Resource getPublicKeyResource() {
        return publicKeyResource;
    }
}
