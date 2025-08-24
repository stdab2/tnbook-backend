package com.tnbook.tnbook.config;

import com.tnbook.tnbook.oauth2.*;
import com.tnbook.tnbook.oauth2.user.CustomOAuth2UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final static String OAUTH2_BASE_URI = "/oauth2/authorize";
    private final static String OAUTH2_REDIRECTION_ENDPOINT = "/oauth2/callback/*";

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/", "/error").permitAll()
                        .requestMatchers("/auth/**", "/oauth2/**").permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                .baseUri(OAUTH2_BASE_URI)
                                .authorizationRequestRepository(this.httpCookieOAuth2AuthorizationRequestRepository)
                                .authorizationRequestResolver(new CustomAuthorizationRequestResolver(this.clientRegistrationRepository, OAUTH2_BASE_URI))
                        )
                        .redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig
                                .baseUri(OAUTH2_REDIRECTION_ENDPOINT)
                        )
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(this.customOAuth2UserService)
                        )
                        .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig.accessTokenResponseClient(authorizationCodeTokenResponseClient()))
                        .successHandler(this.oAuth2AuthenticationSuccessHandler)
                        .failureHandler(this.oAuth2AuthenticationFailureHandler)
                )

                .authenticationProvider(this.authenticationProvider)
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeTokenResponseClient() {
        OAuth2AccessTokenResponseHttpMessageConverter tokenResponseHttpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        tokenResponseHttpMessageConverter.setAccessTokenResponseConverter(new CustomAccessTokenResponseConverter());

        RestClient restClient = RestClient.builder()
                .messageConverters(converters -> {
                    converters.clear();
                    converters.add(new FormHttpMessageConverter());
                    converters.add(tokenResponseHttpMessageConverter);
                })
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
                .build();

        var client  = new RestClientAuthorizationCodeTokenResponseClient();
        client.setRestClient(restClient);
        return client;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://talknbook.com", "http://localhost:4200", "http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
