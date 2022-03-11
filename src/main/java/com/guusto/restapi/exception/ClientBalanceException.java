package com.guusto.restapi.exception;

public class ClientBalanceException extends Exception{
    public ClientBalanceException() {
    }

    public ClientBalanceException(String message) {
        super(message);
    }

    public ClientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientBalanceException(Throwable cause) {
        super(cause);
    }

    public ClientBalanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
