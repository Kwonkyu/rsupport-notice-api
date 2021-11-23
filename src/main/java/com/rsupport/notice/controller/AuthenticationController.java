package com.rsupport.notice.controller;

import com.rsupport.notice.controller.bind.ApiResponse;
import com.rsupport.notice.controller.bind.LoginRequest;
import com.rsupport.notice.util.AuthenticatedJwtResponse;
import com.rsupport.notice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticatedJwtResponse>> authenticate(@Valid @RequestBody LoginRequest request) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        AuthenticatedJwtResponse authenticatedJwtResponse = jwtUtil.issueJwt(authenticate.getName());
        return ResponseEntity.ok(ApiResponse.success(authenticatedJwtResponse));
    }

}
