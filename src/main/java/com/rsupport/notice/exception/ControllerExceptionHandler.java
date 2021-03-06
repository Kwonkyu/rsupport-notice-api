package com.rsupport.notice.exception;

import com.rsupport.notice.controller.bind.ApiResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleIllegalArgument(IllegalArgumentException exception) {
        return ApiResponse.fail(exception.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleJwt(JwtException jwtException) {
        return ApiResponse.fail(jwtException.getMessage());
    }

}
