package com.nasim.chat.auth_service.handler;


import com.nasim.chat.auth_service.exceptions.CustomException;
import com.nasim.chat.auth_service.model.dto.AuthenticationResolution;
import com.nasim.chat.auth_service.model.dto.AuthenticationStatus;
import com.nasim.chat.auth_service.model.dto.InternalUser;
import com.nasim.chat.auth_service.model.entity.AppRegisteredClient;
import com.nasim.chat.auth_service.service.AppRegisterClientService;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
public class OidcLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private static final String PENDING_REGISTRATION =
            "PENDING_REGISTRATION";

    private final InternalUserService internalUserService;
    private final LoginExchangeCodeService exchangeCodeService;
    private final AppRegisterClientService appRegisterClientService;
    public OidcLoginSuccessHandler(
            InternalUserService internalUserService,
            LoginExchangeCodeService exchangeCodeService, AppRegisterClientService appRegisterClientService
    ) {
        this.internalUserService = internalUserService;
        this.exchangeCodeService = exchangeCodeService;
        this.appRegisterClientService = appRegisterClientService;
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
        Object clientId = request.getSession().getAttribute("APP_CLIENT_ID");
        if(clientId==null || !StringUtils.hasText(clientId.toString()))
            throw new CustomException("invalid client id ","INVALID_CLIENT_ID");

        AppRegisteredClient client= appRegisterClientService.findActiveClient(clientId.toString())
                .orElseThrow(
               ()-> new CustomException("invalid client id ","INVALID_CLIENT_ID")
        );

        // Convert the Google identity into our internal identity
        AuthenticationResolution authenticationResolution =
                internalUserService.resolve(
                        oidcUser.getIssuer().toString(),
                        oidcUser.getSubject(),
                        oidcUser.getEmail(),
                        oidcUser.getFullName(),
                        Boolean.TRUE.equals(oidcUser.getEmailVerified()),
                        oidcUser.getIssuer().getHost()
                );
        if (authenticationResolution.authenticationStatus()
                == AuthenticationStatus.EXISTING_USER) {

            InternalUser internalUser =
                    authenticationResolution.internalUser();

            String exchangeCode = exchangeCodeService.create(
                    internalUser.id(),
                    client.getClientId(),
                    internalUser.roles(),
                    List.of( client.getAudience())
            );

            removeOIDCLoginData(response, request);

            ResponseCookie cookie = CookieUtils.CreateCookie("LOGIN_EXCHANGE_CODE", exchangeCode,
                    "/api/auth/token/exchange",Duration.ofSeconds(60),true,false,"Lax");
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            response.sendRedirect(client.getCallbackUrl());
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
                    "APP_CLIENT_ID",
                    client.getClientId()
            );

            onboardingSession.setAttribute(
                    PENDING_REGISTRATION,
                    authenticationResolution.pendingRegistration()
            );

            response.sendRedirect(client.getOnboardingUrl());
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
