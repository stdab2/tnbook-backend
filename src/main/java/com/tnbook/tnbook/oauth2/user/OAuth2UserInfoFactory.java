package com.tnbook.tnbook.oauth2.user;

import com.tnbook.tnbook.exception.OAuth2AuthenticationProcessingException;
import com.tnbook.tnbook.model.enums.AuthProvider;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.name())) {
            return new GoogleOAuth2UserInfo(attributes);
        } /*else if (registrationId.equalsIgnoreCase(AuthProvider.LINKEDIN.name())) {
            return new LinkedinOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(AuthProvider.TWITTER.name())) {
            return new TwitterOAuth2UserInfo(attributes);
        }*/ else {
            throw new OAuth2AuthenticationProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Login with %s is not supported.", registrationId));
        }
    }
}
