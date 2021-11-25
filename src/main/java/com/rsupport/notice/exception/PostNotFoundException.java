package com.rsupport.notice.exception;


public class PostNotFoundException extends IllegalArgumentException {
    public PostNotFoundException(long id) {
        super(String.format("Post with given id %s not found.", id));
    }
}
