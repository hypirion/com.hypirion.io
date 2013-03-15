package com.hypirion.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Pipe {

    public static final int DEFAULT_BUFFER_SIZE = 1024;

    private final Thread pumper;
    private final InputStream in;
    private final OutputStream out;
    private final Object lock;
    private volatile boolean currentlyRunning, stopped;

    public Pipe(InputStream in, OutputStream out) {
        this(in, out, DEFAULT_BUFFER_SIZE);
    }

    public Pipe(InputStream in, OutputStream out, int bufsize) {
        this.in = in;
        this.out = out;
        Runnable pt = new PipeOutputStream(bufsize);
        pumper = new Thread(pt);
        pumper.setName(String.format("PipeThread %s %s", in.hashCode(),
                                           out.hashCode()));
        pumper.setDaemon(true);
        pumper.start();
        lock = new Object();
        currentlyRunning = false;
        stopped = false;
    }

    public void join() throws InterruptedException {
        pumper.join();
    }

    public synchronized void start() {
        if (!stopped) {
            synchronized (lock) {
                currentlyRunning = true;
                lock.notify(); // Wake up the pumper if it's waiting.
            }
        }
    }

    public synchronized void pause() throws InterruptedException {
        pause(true);
    }

    public synchronized void pause(boolean block) throws InterruptedException {
        if (!stopped) {
            synchronized (lock) {
                currentlyRunning = false;
                lock.notify();
                if (block) {
                    lock.wait();
                    // Wait for signal from pumper, which will start after we
                    // release the lock
                }
            }
        } else if (block) {
            join();
        }
    }

    public synchronized void stop() throws InterruptedException {
        stop(true);
    }

    public synchronized void stop(boolean block) throws InterruptedException {
        if (!stopped) {
            synchronized (lock) {
                currentlyRunning = false;
                stopped = true;
                lock.notify();
            }
        }
        if (block) {
            join();
        }
    }

    private class PipeOutputStream implements Runnable {
        private final byte[] data;

        public PipeOutputStream(int bufsize) {
            data = new byte[bufsize];
        }

        @Override
        public void run() {
            try {
                outer:
                while (true) {
                    synchronized (lock) {
                        while (!currentlyRunning) {
                            if (stopped) {
                                break outer;
                            }
                            lock.wait();
                            lock.notify();
                        }
                    }
                    int count = in.read(data);
                    if (count < 0) {
                        break;
                    }
                    out.write(data, 0, count);
                }
            }
            catch (Exception e) {
                // Die silently for now.
            }
            finally {
                try {
                    out.close();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace(); // Well yeah
                }
            }
        }
    }
}
