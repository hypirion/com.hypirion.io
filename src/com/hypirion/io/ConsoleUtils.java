package com.hypirion.io;

import java.io.InputStream;
import java.io.Console;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import sun.nio.cs.StreamDecoder;

public final class ConsoleUtils {

    private static Field getPrivateField(Class klass, String fieldname)
        throws NoSuchFieldException {
        Field f = klass.getDeclaredField(fieldname);
        f.setAccessible(true);
        return f;
    }
    
    public static void rebindConsoleReader(InputStream is)
        throws NoSuchFieldException, ClassNotFoundException {
        Console console = System.console();
        Class c = console.getClass();
        Field readLock = getPrivateField(c, "readLock"),
            charset = getPrivateField(c, "cs"),
            reader = getPrivateField(c, "reader");
        Class rdr = Class.forName("java.io.Console$Reader");
        Constructor[] ctors = rdr.getDeclaredConstructors();
        Object streamDecoder =
            StreamDecoder.forInputStreamReader(is, readLock.get(console),
                                               charset.get(console)),
            newReader = ctors[0].newInstance(console, streamDecoder);
        reader.set(console, newReader);
    }

}
