package com.rsupport.notice.controller.bind;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest {

    @JsonProperty("username")
    @NotBlank
    private String username;

    @JsonProperty("password")
    @NotBlank
    private String password;

}
