package com.nasim.chat.security.config;

import com.nasim.chat.security.jwt.resolver.CompositeBearerTokenResolver;
import com.nasim.chat.security.jwt.convertor.JwtRoleConverter;
import com.nasim.chat.security.jwt.decoder.JwtDecoders;
import com.nasim.chat.security.jwt.resolver.CookieBearerTokenResolver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;


@Configuration(proxyBeanMethods = false)
public class JwtResourceServerConfiguration {

    @Bean
    public JwtRoleConverter jwtRoleConverter() {
        return new JwtRoleConverter();
    }


    @Bean
    BearerTokenResolver bearerTokenResolver() {
        BearerTokenResolver headerResolver =
                new DefaultBearerTokenResolver();

        BearerTokenResolver cookieResolver =
                new CookieBearerTokenResolver("access_token");

        return new CompositeBearerTokenResolver(
                headerResolver,
                cookieResolver
        );
    }

    @Bean
    public RSAPublicKey jwtPublicKey(@Value("${security.jwt.public-key-file}") Resource publicKeyResource) throws IOException {
        return RsaKeyConverters.x509()
                .convert(publicKeyResource.getInputStream());
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey jwtPublicKey ,
                                 @Value("${security.jwt.issuer}") String issuer,
                                 @Value("${security.jwt.audience}") String audience) {

        return JwtDecoders.create(
                jwtPublicKey,
                issuer,audience
        );
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(
            JwtRoleConverter jwtRoleConverter) {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwtRoleConverter);

        return converter;
    }
    ResourceServerAuthorizationRules defaultAuthorizationRules() {
        return authorize -> {
            // No public or role-specific endpoints by default.

        };
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            BearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            ObjectProvider<ResourceServerAuthorizationRules> rulesProvider)
            throws Exception {

        ResourceServerAuthorizationRules rules =
                rulesProvider.getIfAvailable(this::defaultAuthorizationRules);

        http.authorizeHttpRequests(authorize -> {
                    rules.configure(authorize);
                    authorize.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(bearerTokenResolver)
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }


}
