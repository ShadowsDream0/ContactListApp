package com.shadowsdream.exception;

public class PersonServiceException extends Exception {
    public PersonServiceException(String message) {
        super(message);
    }

    public PersonServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
