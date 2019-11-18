package com.shadowsdream.exception;

public class SelectOperationException extends DaoOperationException {
    public SelectOperationException(String message) {
        super(message);
    }

    public SelectOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
