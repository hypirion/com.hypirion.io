package com.hypirion.io;

import java.util.concurrent.Callable;

public class SignalInterceptor {

    public static void register(String signame, Callable fn) throws SignalInterceptorException {
        try {
            SignalInterceptorHelper.register(signame, fn);
        } catch (Throwable e) { // Catching Linkage errors etc. here
            throw new SignalInterceptorException(signame, e);
        }
    }

    public static boolean tryRegister(String signame, Callable fn) {
        try {
            register(signame, fn);
            return true;
        } catch (SignalInterceptorException sie) {
            return false;
        }
    }
}
