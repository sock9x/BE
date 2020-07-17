package com.px.tool.infrastructure.model.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenInfo {
    @JsonProperty(value = "access_token")
    private String accessToken;

    @JsonProperty(value = "token_type")
    private String tokenType;

    @JsonProperty(value = "expires_in")
    private long expireAt;

    @JsonProperty(value = "refresh_token")
    private String refreshToken;

    private long refreshTokenExpireAt;
}
