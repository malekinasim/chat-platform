package com.nasim.chat.auth_service.handler;


import com.nasim.chat.auth_service.model.dto.AuthenticationResolution;
import com.nasim.chat.auth_service.model.dto.AuthenticationStatus;
import com.nasim.chat.auth_service.model.dto.InternalUser;
import com.nasim.chat.auth_service.service.InternalUserService;
import com.nasim.chat.auth_service.service.LoginExchangeCodeService;
import com.nasim.chat.auth_service.service.impl.InternalUserServiceImpl;
import com.nasim.chat.auth_service.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class OidcLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private static final String CLIENT_CALLBACK_URL =
            "http://localhost:8082/auth/callback";

    private static final String CLIENT_PHONE_ONBOARDING_URL =
            "http://localhost:8082/onboarding/phone";

    private static final String PENDING_REGISTRATION =
            "PENDING_REGISTRATION";

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
        String provider = oidcUser.getIssuer().getHost();
        boolean emailVerified =
                Boolean.TRUE.equals(oidcUser.getEmailVerified());

        // Convert the Google identity into our internal identity

        AuthenticationResolution authenticationResolution =
                internalUserService.resolve(
                        oidcUser.getIssuer().toString(),
                        oidcUser.getSubject(),
                        "GOOGLE",
                        oidcUser.getEmail(),
                        emailVerified,
                        oidcUser.getFullName()
                );
        if (authenticationResolution.authenticationStatus()
                == AuthenticationStatus.EXISTING_USER) {

            InternalUser internalUser =
                    authenticationResolution.internalUser();

            String exchangeCode = exchangeCodeService.create(
                    internalUser.id(),
                    internalUser.roles()
            );

            removeOIDCLoginData(response, request);

            ResponseCookie cookie = CookieUtils.CreateCookie("LOGIN_EXCHANGE_CODE", exchangeCode,
                    "/api/auth/token/exchange",Duration.ofSeconds(60));
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            response.sendRedirect(CLIENT_CALLBACK_URL);
        }else {

            HttpSession oidcSession = request.getSession(false);

            if (oidcSession != null) {
                oidcSession.invalidate();
            }

            SecurityContextHolder.clearContext();

            HttpSession onboardingSession = request.getSession(true);

            onboardingSession.setMaxInactiveInterval(
                    InternalUserServiceImpl.ONBOARDING_SESSION_TTL_SECONDS
            );

            onboardingSession.setAttribute(
                    PENDING_REGISTRATION,
                    authenticationResolution.pendingRegistration()
            );

            response.sendRedirect(CLIENT_PHONE_ONBOARDING_URL);
        }

    }

    private void removeOIDCLoginData(HttpServletResponse response, HttpServletRequest request) {
        // Remove the temporary session used during the OIDC process
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        CookieUtils.removedCookie(response, "JSESSIONID", "/");

    }
}
