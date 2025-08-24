package com.tnbook.tnbook.oauth2;

import com.tnbook.tnbook.service.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    public OAuth2AuthenticationFailureHandler() {
        this.httpCookieOAuth2AuthorizationRequestRepository = new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String targetUrl;

        try {
            targetUrl = CookieUtils
                    .getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                    .map(Cookie::getValue)
                    .orElse("/");
        } catch (Exception e) {
            System.err.println("Failed to read redirect URI cookie: " + e.getMessage());
            targetUrl = "/";
        }

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        try {
            this.httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        } catch (Exception e) {
            System.err.println("Failed to clean up OAuth2 cookies: " + e.getMessage());
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
