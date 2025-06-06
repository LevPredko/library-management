package com.example.library.exception;

public class MemberLimitExceededException extends RuntimeException {
    public MemberLimitExceededException(String message) {
        super(message);
    }
}
