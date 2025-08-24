package com.tnbook.tnbook.oauth2.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public abstract String getProviderId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
