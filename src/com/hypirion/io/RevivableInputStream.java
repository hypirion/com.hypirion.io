package com.hypirion.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

public class RevivableInputStream extends InputStream {
    protected boolean killed;
    protected InputStream in;

    protected boolean streamClosed;
    protected byte data;
    protected volatile boolean newData;

    protected final Object lock;
    protected final RevivableReader reader;

    public RevivableInputStream(InputStream in) {
        this.in = in;
        killed = false;
        streamClosed = false;
        newData = false;
        lock = new Object();
        reader = new RevivableReader(this);
        // Fire up new thread around here.
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public synchronized int read() throws IOException {
        synchronized (lock) {
            if (killed || streamClosed)
                return -1;
            try {
                lock.wait();
            }
            catch (InterruptedException ie) {
                throw new InterruptedIOException();
            }
            if (killed || streamClosed)
                return -1;
            int val = data;
            // Some startup of the reader here
            return data;
        }
    }

    public synchronized int read(byte[] b) throws IOException {
        synchronized (lock) {
            if (killed || streamClosed)
                return -1;
            try {
                lock.wait();
            }
            catch (InterruptedException ie) {
                throw new InterruptedIOException();
            }
            if (killed || streamClosed || data == -1)
                return -1;
            b[0] = data;
            // Some startup of the reader here
            return 1;
        }
    }

    public synchronized int read(byte[] b, int off, int len) throws IOException{
        synchronized (lock) {
            if (killed || streamClosed)
                return -1;
            try {
                lock.wait();
            }
            catch (InterruptedException ie) {
                throw new InterruptedIOException();
            }
            if (killed || streamClosed || data == -1)
                return -1;
            if (len == 0)
                return 0;
            b[off] = data;
            // Some startup of the reader here
            return 1;
        }
    }

    public void reset() throws IOException {
        in.reset();
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    public void kill() {
        synchronized (lock) {
            killed = true;
            lock.notifyAll();
        }
    }

    public synchronized void ressurect() {
        killed = false;
    }

    private class RevivableReader implements Runnable {
        public RevivableReader(RevivableInputStream ris) {
            // Do some magic here
        }

        @Override
        public void run() {
            // And here. obviously.
        }
    }
}
