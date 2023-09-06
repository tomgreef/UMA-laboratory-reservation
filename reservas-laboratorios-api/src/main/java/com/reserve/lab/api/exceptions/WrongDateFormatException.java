package com.reserve.lab.api.exceptions;

public class WrongDateFormatException extends RuntimeException {
    public WrongDateFormatException(String message) {
        super(message);
    }
}
