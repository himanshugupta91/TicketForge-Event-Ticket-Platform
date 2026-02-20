package com.himanshu.tickets.exceptions;

public class TicketsSoldOutException extends EventTicketException {
    public TicketsSoldOutException() {
    }

    public TicketsSoldOutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TicketsSoldOutException(Throwable cause) {
        super(cause);
    }

    public TicketsSoldOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TicketsSoldOutException(String message) {
        super(message);
    }
}
