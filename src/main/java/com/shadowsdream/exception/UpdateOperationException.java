package com.shadowsdream.exception;

public class UpdateOperationException extends DaoOperationException {
    public UpdateOperationException(String message) {
        super(message);
    }

    public UpdateOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
