package com.test.restapi.exception;

public class GiftCardException extends Exception{
    public GiftCardException() {
    }

    public GiftCardException(String message) {
        super(message);
    }

    public GiftCardException(String message, Throwable cause) {
        super(message, cause);
    }

    public GiftCardException(Throwable cause) {
        super(cause);
    }

    public GiftCardException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
