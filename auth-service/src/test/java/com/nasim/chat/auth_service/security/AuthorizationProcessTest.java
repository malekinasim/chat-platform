package com.nasim.chat.auth_service.security;


import com.nasim.chat.auth_service.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest
public class AuthorizationProcessTest {
    private final String BASE_URL="http://chat-client:8082";

    @Autowired
    private TokenService tokenService;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Test
    void requestWithoutJwtShouldReturn401(){


        TestRestTemplate template=new TestRestTemplate();
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(BASE_URL+"/api/chat/list/user-rooms").build();
        ResponseEntity<Void> responseEntity=template.getForEntity( uriComponents.toUri(), Void.class);
        assertEquals(HttpStatus.UNAUTHORIZED,responseEntity.getStatusCode());

    }
    @Test
    void validUserTokenShouldAccessChatEndpoint(){

        String token = tokenService.generateAccessToken(
                "test-user-123",
                List.of("USER"),
                List.of("chat-client")
        );

        TestRestTemplate template=new TestRestTemplate();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        HttpEntity<Void> httpEntity=new HttpEntity<>(httpHeaders);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(BASE_URL+"/api/chat/list/user-rooms").build();
        ResponseEntity<String> responseEntity=template.exchange(uriComponents.toUri(), GET,httpEntity,String.class);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }
    @Test
    void userTokenShouldNotAccessAdminEndpoint(){

        String token = tokenService.generateAccessToken(
                "test-user-123",
                List.of("USER"),
                List.of("chat-client")
        );

        TestRestTemplate template=new TestRestTemplate();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        HttpEntity<Void> httpEntity=new HttpEntity<>(httpHeaders);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(BASE_URL+"/api/admin/rooms").build();
        ResponseEntity<String> responseEntity=template.exchange(uriComponents.toUri(), GET,httpEntity,String.class);
        assertEquals(HttpStatus.FORBIDDEN,responseEntity.getStatusCode());
    }
    @Test
    void expiredTokenShouldBeRejected(){

        Instant now=Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://auth-service:8083")
                .subject("test-user-123")
                .audience(List.of("chat-client"))
                .issuedAt(now.minusSeconds(200))
                .expiresAt(now.minusSeconds(100))
                .claim("roles", List.of("ADMIN"))
                .build();
        String expiredToken = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        TestRestTemplate template=new TestRestTemplate();
        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.setBearerAuth(expiredToken);
        HttpEntity<Void> httpEntity=new HttpEntity<>(httpHeaders);
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(BASE_URL+"/api/admin/rooms").build();
        ResponseEntity<String> responseEntity=template.exchange(uriComponents.toUri(), GET,httpEntity,String.class);
        assertEquals(HttpStatus.UNAUTHORIZED,responseEntity.getStatusCode());

    }
}
