package com.hypirion.io;

import java.io.InputStream;
import java.io.Console;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sun.nio.cs.StreamDecoder;
import java.nio.charset.Charset;

public final class ConsoleUtils {

    private static Field getPrivateField(Class klass, String fieldname)
        throws NoSuchFieldException {
        Field f = klass.getDeclaredField(fieldname);
        f.setAccessible(true);
        return f;
    }
    
    public static void rebindConsoleReader(InputStream is)
        throws NoSuchFieldException, ClassNotFoundException,
               IllegalAccessException, InstantiationException,
               InvocationTargetException {
        Console console = System.console();
        Class c = console.getClass();
        Field readLock = getPrivateField(c, "readLock"),
            charset = getPrivateField(c, "cs"),
            reader = getPrivateField(c, "reader");
        Class rdr = Class.forName("java.io.Console$Reader");
        Constructor[] ctors = rdr.getDeclaredConstructors();
        Object streamDecoder =
            StreamDecoder.forInputStreamReader(is, readLock.get(console),
                                               (Charset) charset.get(console)),
            newReader = ctors[0].newInstance(console, streamDecoder);
        reader.set(console, newReader);
    }

}
