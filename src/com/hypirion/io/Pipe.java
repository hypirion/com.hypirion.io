package com.hypirion.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

public class Pipe {

    public static final int DEFAULT_BUFFER_SIZE = 1024;

    private final Thread threadPumper;
    private final PipeThread pt;
    private final InputStream in;
    private final OutputStream out;
    private final int bufsize;
    private final Object lock;
    private volatile boolean currentlyRunning, stopped;

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
        threadPumper.start();
        lock = new Object();
        currentlyRunning = false;
        stopped = false;
    }

    public void join() throws InterruptedException {
        threadPumper.join();
    }

    public synchronized void start() {
        synchronized (lock) {
            currentlyRunning = true;
            lock.notify(); // Wake up the pumper if it's waiting.
        }
    }

    public synchronized void pause() throws InterruptedException {
        pause(true);
    }

    public synchronized void pause(boolean block) throws InterruptedException {
        synchronized (lock) {
            currentlyRunning = false;
            lock.notify();
            if (block) {
                lock.wait();
                // Wait for signal from pumper, which will start after we
                // release the lock
            }
        }
    }

    public synchronized void stop() throws InterruptedException {
        stop(true);
    }

    public synchronized void stop(boolean block) throws InterruptedException {
        synchronized (lock) {
            currentlyRunning = false;
            stopped = true;
            lock.notify();
        }
        if (block) {
            join();
        }
    }

    private class PipeThread implements Runnable {
        private final byte[] data;

        public PipeThread() {
            data = new byte[bufsize];
        }

        @Override
        public void run() {
            outer:
            while (true) {
                synchronized (lock) {
                    while (!currentlyRunning) {
                        if (stopped) {
                            break outer;
                        }
                        lock.wait();
                    }
                }
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
