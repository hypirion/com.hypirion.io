package com.hypirion.io;

import java.io.InputStream;
import java.io.OutputStream;

public class Pipe {

    public static final int DEFAULT_BUFFER_SIZE = 1024;

    private final Thread threadPumper;
    private final PipeThread pt;
    private final InputStream in;
    private final OutputStream out;
    private final byte[] data;
    private final Object readLock, writeLock;

    public Pipe(InputStream in, OutputStream out) {
        this(in, out, DEFAULT_BUFFER_SIZE);
    }

    public Pipe(InputStream in, OutputStream out, int bufsize) {
        this.in = in;
        this.out = out;
        data = new byte[bufsize];
        pt = new PipeThread();
        threadPumper = new Thread(pt);
        threadPumper.setName(String.format("PipeThread %s %s", in.hashCode(),
                                           out.hashCode()));
        threadPumper.setDaemon(true);
        readLock = new Object();
        writeLock = new Object();
    }

    public void join() throws InterruptedException {
        threadPumper.join();
    }

    public void start() {

    }

    public void stop() {
        stop(true);
    }

    public void stop(boolean block) {
        
    }

    private class PipeThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                
            }
        }
    }
}
