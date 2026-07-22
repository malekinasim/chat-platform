package com.nasim.chat.auth_service.config;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration(proxyBeanMethods = false)
public class JwtKeyConfiguration {
      @Bean
       public RSAPrivateKey jwtPrivateKey(@Value("${security.jwt.private-key-file}")
                                             Resource privateKeyResource) throws IOException {
            return RsaKeyConverters.pkcs8().convert(
                    privateKeyResource.getInputStream()
            );
      }
    @Bean
    public RSAPublicKey jwtPublicKey(@Value("${security.jwt.public-key-file}")
                                    Resource publicKeyResource) throws IOException {
        return RsaKeyConverters.x509().convert(publicKeyResource.getInputStream());
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAPublicKey publicKey, RSAPrivateKey privateKey) {

        return NimbusJwtEncoder.withKeyPair(publicKey,privateKey).build();
    }
}
