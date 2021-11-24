package com.rsupport.notice.configuration;

import com.rsupport.notice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private String parseJwtRequest(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) return "";
        String[] token = authorization.split(" ");
        if (token.length < 2) return "";
        return token[1];
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = parseJwtRequest(request);
        String subject = jwtUtil.parseSubjectFromJwt(jwt);
        if(!subject.isBlank()) {
            PreAuthenticatedAuthenticationToken authenticationToken = new PreAuthenticatedAuthenticationToken(subject, "AUTHENTICATED_BY_JWT");
            authenticationToken.setAuthenticated(true);
            SecurityContextHolder.setContext(new SecurityContextImpl(authenticationToken));
        }
        filterChain.doFilter(request, response);
    }
}
