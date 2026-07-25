package com.nasim.chat.auth_service.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;


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
    public static void CreateCookie(){

    }

}
