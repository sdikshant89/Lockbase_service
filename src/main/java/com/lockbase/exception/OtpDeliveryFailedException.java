package com.lockbase.exception;

public class OtpDeliveryFailedException extends RuntimeException{
    public OtpDeliveryFailedException(String message) {
        super(message);
    }
    public OtpDeliveryFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
