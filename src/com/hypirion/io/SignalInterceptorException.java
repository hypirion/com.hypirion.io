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

public class SignalInterceptorException extends Exception {

    public final String signame;

    public SignalInterceptorException(String signal, Throwable cause) {
        super(String.format("Unable to register for SIG%s", signal), cause);
        signame = signal;
    }
}
