package com.hypirion.io;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.HashMap;
import java.util.concurrent.Callable;

class SignalInterceptorHelper {

    static void register(String signame, Callable fn) {
        Signal sig = new Signal(signame);
        WrappedCallable newHandler = new WrappedCallable(fn);
        SignalHandler oldHandler = Signal.handle(sig, newHandler);
        newHandler.attachOld(oldHandler);
    }

    private static class WrappedCallable implements SignalHandler {
        private Callable fn;
        private SignalHandler oldHandler;

        public WrappedCallable(Callable fn) {
            this.fn = fn;
            this.oldHandler = null;
        }

        public void attachOld(SignalHandler oldHandler) {
            this.oldHandler = oldHandler;
        }
        
        public void handle(Signal sig) {
            boolean cont = true;
            try {
                cont = (Boolean) fn.call();
            }
            catch (Exception e) {}
            finally {
                if (cont && (oldHandler != null)) {
                    oldHandler.handle(sig);
                }
            }
        }
    }
}
