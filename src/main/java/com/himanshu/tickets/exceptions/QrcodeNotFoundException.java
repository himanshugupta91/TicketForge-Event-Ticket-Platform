package com.himanshu.tickets.exceptions;

public class QrcodeNotFoundException extends EventTicketException {
    public QrcodeNotFoundException() {
    }

    public QrcodeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public QrcodeNotFoundException(Throwable cause) {
        super(cause);
    }

    public QrcodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public QrcodeNotFoundException(String message) {
        super(message);
    }
}
