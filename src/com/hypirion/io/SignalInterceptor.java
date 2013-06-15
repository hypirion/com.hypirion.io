/*
 * Copyright (c) 2013 Jean Niklas L'orange. All rights reserved.
 *
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 *
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 *
 * You must not remove this notice, or any other, from this software.
 */

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
