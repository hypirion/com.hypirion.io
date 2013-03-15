package com.hypirion.io;

import java.io.InputStream;
import java.io.OutputStream;

public class Pipe {

    private final Thread threadPumper;
    private final PipeThread pt;
    private final InputStream in;
    private final OutputStream out;
    private final Object readLock, writeLock;

    public Pipe(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
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
