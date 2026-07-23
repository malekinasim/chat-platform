package com.nasim.chat.auth_service.handler;


import com.nasim.chat.auth_service.service.InternalUserService;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import com.nasim.chat.auth_service.model.InternalUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OidcLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private static final String CLIENT_CALLBACK_URL =
            "http://localhost:8082/auth/callback";

    private final InternalUserService internalUserService;
    private final LoginExchangeCodeService exchangeCodeService;

    public OidcLoginSuccessHandler(
            InternalUserService internalUserService,
            LoginExchangeCodeService exchangeCodeService
    ) {
        this.internalUserService = internalUserService;
        this.exchangeCodeService = exchangeCodeService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        if (!(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Expected an authenticated OIDC user"
            );
            return;
        }

        // The external identity validated by Spring Security
        String issuer = oidcUser.getIssuer().toString();
        String externalSubject = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        // Convert the Google identity into our internal identity
        InternalUser internalUser = internalUserService.findOrCreate(
                issuer,
                externalSubject,
                email,
                name
        );

        // Create our own short-lived, single-use exchange code
        String exchangeCode = exchangeCodeService.create(internalUser.id(), internalUser.roles());

        // Remove the temporary session used during the OIDC process
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        // Tell the browser to remove JSESSIONID
        ResponseCookie deletedSessionCookie = ResponseCookie
                .from("JSESSIONID", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                deletedSessionCookie.toString()
        );

        // Redirect with our one-time code—not a JWT
        String redirectUrl = UriComponentsBuilder
                .fromUriString(CLIENT_CALLBACK_URL)
                .queryParam("code", exchangeCode)
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
