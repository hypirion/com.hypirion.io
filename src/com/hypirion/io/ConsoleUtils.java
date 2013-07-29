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

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The ConsoleUtils class is a static collection of methods used to modify or
 * use the console in rather unconventional ways. It is mainly a way to set on
 * and off echoing in the console, either manually or through password reading
 * from an arbitrary InputStream.
 *
 * @author Jean Niklas L'orange
 * @see java.io.Console
 * @since <code>com.hypirion.io 0.3.0</code>
 */

public final class ConsoleUtils {

    /**
     * A static method which peeks into the {@link java.io.Console} class and
     * manually turns on or off echoing in the given console.
     *
     * @param on whether to set echoing on or off.
     *
     * @exception NoSuchMethodException if Java's own console class have no
     * private method named "echo".
     * @exception NoSuchFieldException if Java's own console class have no
     * private field named "echoOff".
     * @exception InvocationTargetException if Java's own console class have a
     * private method named "echo", but that this method tries to invoke that
     * method erroneously.
     * @exception IllegalAccessException if the access restrictions prohibits
     * this method from modifying and using private methods in Java's own
     * console class.
     */
    public static synchronized boolean setEcho(boolean on)
        throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        Class params[] = new Class[1];
        params[0] = Boolean.TYPE;
        Method echo = Console.class.getDeclaredMethod("echo", params);
        echo.setAccessible(true);
        boolean res = (Boolean) echo.invoke(null, on);
        Field echoOff = Console.class.getDeclaredField("echoOff");
        echoOff.setAccessible(true);
        echoOff.set(null, res);
        return res;
    }
    
    /**
     * A static method which will read from a given InputStream while it turns
     * echo off in the JVM console. Works just as {@link
     * java.io.Console#readPassword}.
     *
     * @param is the InputStream to read from.
     *
     * @exception NoSuchMethodException if Java's own console class have no
     * private method named "echo".
     * @exception NoSuchFieldException if Java's own console class have no
     * private field named "echoOff".
     * @exception InvocationTargetException if Java's own console class have a
     * private method named "echo", but that this method tries to invoke that
     * method erroneously.
     * @exception IllegalAccessException if the access restrictions prohibits
     * {@link #setEcho(boolean)} from modifying and using private methods in
     * Java's own console class.
     * @exception IOException if the given <code>InputStream</code> throws an
     * <code>IOException</code>.
     */
    public static char[] readPassword(InputStream is) throws
        NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException, IOException {
        try {
            synchronized(is) {
                setEcho(false);
                InputStreamReader isr = new InputStreamReader(is);
                char[] buf = new char[32];
                int len = 0;
                loop:
                while (true) {
                    int c = isr.read();
                    switch (c) {
                    case -1:
                    case '\n':
                        break loop;
                    case '\r':
                        continue;
                    }
                    buf[len] = (char) c;
                    len++;
                    if (len == buf.length) {
                        char[] fresh = new char[2*len];
                        System.arraycopy(buf, 0, fresh, 0, len);
                        buf = fresh;
                    }
                }
                char[] out = new char[len];
                System.arraycopy(buf, 0, out, 0, len);
                return out;
            }
        }
        finally {
            setEcho(true);            
        }
    }
}
