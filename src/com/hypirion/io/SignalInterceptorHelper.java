/*
 * Copyright (c) 2013 Jean Niklas L'orange. All rights reserved.
 *
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file LICENSE at the root of this distribution.
 *
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 *
 * You must not remove this notice, or any other, from this software.
 */

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
