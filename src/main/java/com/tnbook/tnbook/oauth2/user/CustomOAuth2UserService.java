package com.tnbook.tnbook.oauth2.user;

import com.tnbook.tnbook.exception.OAuth2AuthenticationProcessingException;
import com.tnbook.tnbook.model.entity.User;
import com.tnbook.tnbook.model.enums.AuthProvider;
import com.tnbook.tnbook.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            log.error("Email not found from OAuth2 provider");
            throw new OAuth2AuthenticationProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, "Email not found from OAuth2 provider");
        }

        Optional<User> optionalUser = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        String stage;
        if (optionalUser.isEmpty()) {
            stage = "Signup";
        } else {
            stage = "Login";
        }

        User user;

        if("Signup".equals(stage)) {
            String userEmail = oAuth2UserInfo.getEmail();
            String providerId = oAuth2UserInfo.getProviderId();
            String name = oAuth2UserInfo.getName();
            String imageUrl = oAuth2UserInfo.getImageUrl();
            user = User.builder()
                    .email(userEmail)
                    .enabled(false)
                    .authProvider(AuthProvider.GOOGLE)
                    .providerId(providerId)
                    .build();
            this.userRepository.save(user);
        } else {
            AuthProvider expected = AuthProvider.valueOf(registrationId.toUpperCase());
            user = optionalUser.get();
            if (user.getAuthProvider() != expected) {
                throw new OAuth2AuthenticationProcessingException(HttpStatus.BAD_REQUEST, "Please sign in with " + user.getAuthProvider().name().toLowerCase());
            }

            if (!user.isEnabled()) {
                throw new OAuth2AuthenticationProcessingException(HttpStatus.FORBIDDEN, "Account is disabled.");
            }
            System.out.println("signup");
        }

        return User.create(user, oAuth2User.getAttributes());
    }
}
