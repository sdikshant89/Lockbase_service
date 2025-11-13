package com.lockbase.exception;

public class OtpSendFailedException extends RuntimeException{
    public OtpSendFailedException(String message) {
        super(message);
    }
    public OtpSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
