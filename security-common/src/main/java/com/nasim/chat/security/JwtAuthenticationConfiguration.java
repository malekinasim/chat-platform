package com.nasim.chat.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.security.interfaces.RSAPublicKey;

@Configuration(proxyBeanMethods = false)
public class JwtAuthenticationConfiguration {

    @Bean
    public JwtRoleConverter jwtRoleConverter(){
        return  new JwtRoleConverter();
    }
    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {

        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtAuthenticationConverter converter=new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this.jwtRoleConverter());
        return converter;
    }



}
