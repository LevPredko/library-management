package com.example.library.exception;

public class BookUnavailableException extends RuntimeException {
    public BookUnavailableException(String message) {
        super(message);
    }
}
