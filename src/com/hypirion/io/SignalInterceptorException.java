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

/**
 * <code>SignalInterceptorException</code>s are thrown if the {@link
 * SignalInterceptor} class is unable to find the signal attempted to intercept,
 * or if the JVM doesn't implement the most common JVM signal handling
 * facilities.
 *
 * @author Jean Niklas L'orange
 * @since <code>com.hypirion.io 0.3.0</code>
 */

public class SignalInterceptorException extends Exception {

    /**
     * The name of the signal attempted to intercept, with <code>SIG</code>
     * omitted.
     */
    public final String signame;

    /**
     * Creates a new <code>SignalInterceptorException</code> where
     * <code>signal</code> is the attempted signal to intercept, with
     * <code>cause</code> as underlying reason for not being able to do so.
     */
    public SignalInterceptorException(String signal, Throwable cause) {
        super(String.format("Unable to register for SIG%s", signal), cause);
        signame = signal;
    }
}
