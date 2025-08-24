package com.tnbook.tnbook.oauth2;

import com.tnbook.tnbook.config.AppProperties;
import com.tnbook.tnbook.model.entity.User;
import com.tnbook.tnbook.repository.UserRepository;
import com.tnbook.tnbook.service.JwtService;
import com.tnbook.tnbook.service.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@AllArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final AppProperties appProperties;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = Optional.empty();

        try {
            redirectUri = CookieUtils
                    .getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                    .map(Cookie::getValue);
        } catch (Exception e) {
            System.err.println("Failed to read redirect URI cookie: " + e.getMessage());
        }

        System.out.println("Redirect URI from cookie: " + redirectUri.orElse("NOT_FOUND"));
        System.out.println("Authorized redirect URIs: " + this.appProperties.getOauth2().getAuthorizedRedirectUris());

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        User principal = (User) authentication.getPrincipal();

        User user = this.userRepository.findByEmail(principal.getEmail()).orElseThrow(
                () -> new RuntimeException("User with email " + principal.getEmail() + " not found")
        );

        if(!user.isEnabled()) {
            targetUrl = "http://localhost:4200/oauth2/google/signup/redirect";
            System.out.println("Redirect changed to " + targetUrl);
        }

        user.setEnabled(true);
        this.userRepository.save(user);
        String token = this.jwtService.generateToken(user);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        this.httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return this.appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
