package com.ssafy.drinkus.user.domain.oauth;

import com.ssafy.drinkus.user.domain.type.UserProvider;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUserEmail() {
        return (String) attributes.get("email");
    }


    @Override
    public UserProvider getUserProvider() {
        return UserProvider.google;
    }

    @Override
    public String getUserProviderId() {
        return getUserProvider() + "_" + getUserName();
    }

    @Override
    public String getUserName() {
        return (String) attributes.get("sub");
    }
}