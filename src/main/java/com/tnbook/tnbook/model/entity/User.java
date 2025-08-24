package com.tnbook.tnbook.model.entity;

import com.tnbook.tnbook.model.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails, OAuth2User {

    // --- Security-only fields (not persisted) ---
    @Transient
    private Collection<? extends GrantedAuthority> authorities = List.of();
    @Transient
    private Map<String, Object> attributes = Collections.emptyMap();

    // --- Persisted fields ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_expiration")
    private LocalDateTime verificationExpiration;

    public User (String email, String password, AuthProvider authProvider) {
        this.email = email;
        this.password = password;
        this.authProvider = authProvider;
    }

    public User(Long id, String email, String password, boolean enabled, AuthProvider authProvider, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.authProvider = authProvider;
        this.authorities = authorities;
    }

    // --- UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    // --- OAuth2User ---

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return email != null ? email : String.valueOf(id);
    }

    public static User create(User user, Map<String, Object> attributes) {
        User copy = new User();
        copy.setId(user.getId());
        copy.setEmail(user.getEmail());
        copy.setPassword(user.getPassword());
        copy.setEnabled(user.isEnabled());
        copy.setAuthProvider(user.getAuthProvider());
        copy.setProviderId(user.getProviderId());
        copy.setAuthorities(user.getAuthorities());
        copy.setAttributes(attributes != null ? attributes : Collections.emptyMap());
        return copy;
    }

}
