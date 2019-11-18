package com.shadowsdream.exception;

public class DeleteOperationException extends DaoOperationException {
    public DeleteOperationException(String message) {
        super(message);
    }

    public DeleteOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
