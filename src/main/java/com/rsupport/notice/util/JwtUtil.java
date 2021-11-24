package com.rsupport.notice.util;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${JWT_SECRET}") private String jwtSecret;

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    private JwtBuilder buildUntilExpirationDate(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuer("rsupport-notice-api")
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, jwtSecret);
    }

    public AuthenticatedJwtResponse issueJwt(String username) {
        JwtBuilder jwtBuilder = buildUntilExpirationDate(username);
        String accessToken = jwtBuilder
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .compact();
        String refreshToken = jwtBuilder
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .compact();
        return new AuthenticatedJwtResponse(accessToken, refreshToken);
    }

    public String parseSubjectFromJwt(String token) {
        if(token == null || token.isBlank()) return "";

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException exception) {
            return "";
        }
    }

}
