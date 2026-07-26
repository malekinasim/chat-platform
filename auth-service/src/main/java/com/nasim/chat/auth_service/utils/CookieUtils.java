package com.nasim.chat.auth_service.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;


public class CookieUtils {
    public static void removedCookie(HttpServletResponse response, String cookieName, String path){
        // Tell the browser to remove JSESSIONID
        ResponseCookie deletedSessionCookie = ResponseCookie
                .from(cookieName, "")
                .httpOnly(true)
                .path(path)
                .maxAge(0)
                .build();
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                deletedSessionCookie.toString()
        );
    }
    public static ResponseCookie CreateCookie(String name,String value,String path,Duration maxAgeInSeconds){
        ResponseCookie cookie = ResponseCookie
                .from(name, value)
                .httpOnly(true)
                .secure(false) // true in production with HTTPS
                .sameSite("Lax")
                .path(path!=null ? path: "/")
                .maxAge(maxAgeInSeconds)
                .build();
        return cookie;
    }

}
