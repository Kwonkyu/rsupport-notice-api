package com.rsupport.notice.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthenticatedJwtResponse {

    @JsonProperty("jwtAccessToken")
    private final String accessToken;

    @JsonProperty("jwtRefreshToken")
    private final String refreshToken;

}
