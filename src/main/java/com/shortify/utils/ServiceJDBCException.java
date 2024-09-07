package com.shortify.utils;

public class ServiceJDBCException extends RuntimeException {
    public ServiceJDBCException(String msg){
        super(msg);
    }
    public ServiceJDBCException(String msg, Throwable e){
        super(msg, e);
    }
}
