package com.px.tool.infrastructure.exception;

public class PXException extends RuntimeException {
    private String message;

    public PXException(String message) {
        super(message);
        this.message = message;
    }

    public PXException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
}
