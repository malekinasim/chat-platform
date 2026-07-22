package com.nasim.chat.security;


import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class JwtRoleConverterTest {
    private final JwtRoleConverter converter = new JwtRoleConverter();
    @Test
    void shouldConvertRolesClaimToGrantedAuthorities() {
        Jwt jwt = Jwt.withTokenValue("fake-token")
                .header("alg", "none")
                .subject("user-123")
                .claim("roles", List.of("USER", "ADMIN"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
        Collection<GrantedAuthority> authorities =
                converter.convert(jwt);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER", "ROLE_ADMIN");
    }
    @Test
    void shouldReturnEmptyAuthoritiesWhenRolesClaimIsMissing() {
        Jwt jwt = Jwt.withTokenValue("fake-token")
                .header("alg", "none")
                .subject("user-123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
        Collection<GrantedAuthority> authorities =
                converter.convert(jwt);
        assertThat(authorities).isEmpty();
    }
}