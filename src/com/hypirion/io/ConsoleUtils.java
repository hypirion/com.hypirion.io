package com.hypirion.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.Console;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class ConsoleUtils {

    public static boolean setEcho(boolean on) throws Exception {
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
    
    public static char[] readPassword(InputStream is) throws Exception {
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
