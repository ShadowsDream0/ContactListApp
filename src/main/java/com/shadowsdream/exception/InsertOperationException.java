package com.shadowsdream.exception;

public class InsertOperationException extends DaoOperationException {

    public InsertOperationException(String message) {
        super(message);
    }

    public InsertOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
