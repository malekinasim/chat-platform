package com.nasim.chat.auth_service.config;



import com.nasim.chat.security.config.JwtResourceServerConfiguration;
import com.nasim.chat.security.jwt.decoder.JwtDecoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration(proxyBeanMethods = false)
public class JwtKeyConfiguration {
     private final  RSAPublicKey publicKey;

    public JwtKeyConfiguration(@Lazy RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Bean
       public RSAPrivateKey jwtPrivateKey(@Value("${security.jwt.private-key-file}")
                                             Resource privateKeyResource) throws IOException {
            return RsaKeyConverters.pkcs8().convert(
                    privateKeyResource.getInputStream()
            );
      }
    @Bean
    public JwtEncoder jwtEncoder(RSAPrivateKey privateKey) {

        return NimbusJwtEncoder.withKeyPair(publicKey,privateKey).build();
    }
}
