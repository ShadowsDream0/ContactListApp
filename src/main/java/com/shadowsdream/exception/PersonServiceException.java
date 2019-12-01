package com.shadowsdream.exception;

public class PersonServiceException extends ServiceException {
    public PersonServiceException(String message) {
        super(message);
    }

    public PersonServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
