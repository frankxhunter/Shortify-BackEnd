package com.frank.shortify.exceptions;

public class GoogleUnauthorizedException extends RuntimeException {
    public GoogleUnauthorizedException(String msg) {
        super(msg);
    }
}
