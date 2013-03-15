package com.hypirion.io;

import java.io.InputStream;
import java.io.OutputStream;

public class Pipe {

    public static final int DEFAULT_BUFFER_SIZE = 1024;

    private final Thread threadPumper;
    private final PipeThread pt;
    private final InputStream in;
    private final OutputStream out;
    private final int bufsize;
    private final Object dataLock;

    public Pipe(InputStream in, OutputStream out) {
        this(in, out, DEFAULT_BUFFER_SIZE);
    }

    public Pipe(InputStream in, OutputStream out, int bufsize) {
        this.in = in;
        this.out = out;
        this.bufsize = bufsize;
        pt = new PipeThread();
        threadPumper = new Thread(pt);
        threadPumper.setName(String.format("PipeThread %s %s", in.hashCode(),
                                           out.hashCode()));
        threadPumper.setDaemon(true);
        dataLock = new Object();
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
        private final byte[] data;

        public PipeThread() {
            data = new byte[bufsize];
        }

        @Override
        public void run() {
            while (true) {
                synchronized (dataLock) {
                    int count = in.read(data);
                    if (count < 0) {
                        out.close();
                        break;
                    }
                    out.write(data, 0, count);
                }
            }
        }
    }
}
