package com.hypirion.io;

public class SignalInterceptorException extends Exception {

    public final String signame;

    public SignalInterceptorException(String signal, Throwable cause) {
        super(String.format("Unable to register for SIG%s", signal), cause);
        signame = signal;
    }
}
