package com.rsupport.notice.exception;


public class FileNotFoundException extends IllegalArgumentException {
    public FileNotFoundException(String hash) {
        super(String.format("File with id %s not found.", hash));
    }
}
