/*
 * Copyright (c) 2013 Jean Niklas L'orange. All rights reserved.
 *
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 *
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 *
 * You must not remove this notice, or any other, from this software.
 */

package com.hypirion.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * A revivable input stream is an unbuffered input stream wrapping another input
 * stream. Its primary feature is that it allows to "kill" blocking
 * <code>.read</code> calls by calling <code>.kill</code>. Reading from the
 * stream can be resumed by calling <code>.resurrect</code>.
 *
 * The common use for this is to avoid closing an input stream, while still be
 * able to cancel a blocking <code>.read</code> where you must use an input
 * stream. This is useful if you need to send a message to the thread which
 * attempts to read from the input stream.
 *
 * @author Jean Niklas L'orange
 * @since <code>com.hypirion.io 0.1.0</code>
 */

public class RevivableInputStream extends InputStream {
    private InputStream in;

    private volatile boolean killed;
    private volatile boolean streamClosed;
    private volatile int data;
    private volatile boolean beenRead;
    private final Object dataLock;
    private volatile boolean threadCrashed;
    private volatile IOException threadException;

    private final ThreadReader reader;
    private final Thread readerThread;

    /**
     * Creates a new <code>RevivableInputStream</code> which wraps
     * <code>in</code>, giving it power to be killed and resurrected.
     */
    public RevivableInputStream(InputStream in) {
        this.in = in;
        killed = false;
        streamClosed = false;
        beenRead = true;
        dataLock = new Object();
        threadCrashed = false;
        threadException = null;
        data = -2;
        reader = new ThreadReader();
        readerThread = new Thread(reader);
        readerThread.setDaemon(true);
        readerThread.setName("RevivableReader " + in.hashCode());
        readerThread.start();
    }

    /**
     * Returns the number of bytes than can be read from this input stream
     * without blocking.
     *
     * Will as of now return 0.
     *
     * @return 0
     */
    public synchronized int available() {
        return 0;
    }

    /**
     * Closes this revivable input stream and the underlying input stream, and
     * releases any with system resources (threads, memory) associated with this
     * stream.
     *
     * @exception IOException if the underlying <code>InputStream</code> throws
     * an <code>IOEXception</code>.
     */
    public synchronized void close() throws IOException {
        synchronized (dataLock) {
            in.close();
            dataLock.notifyAll();
        }
    }

    /**
     * Reads the next byte of data from this revivable input stream. The value
     * byte is returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. This method blocks until no data is available, the end
     * of the stream is detected, an exception is thrown or if the reviable
     * input stream is (potentially temporarily) killed.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     * stream is reached or the stream is killed.
     * @exception IOException if the underlying <code>InputStream</code> throws
     * an <code>IOException</code> when attempted to read. This exception will
     * be thrown every time read is called until the stream is closed.
     */
    public synchronized int read() throws IOException {
        synchronized (dataLock) {
            try {
                while (beenRead && !killed && !streamClosed && !threadCrashed) {
                    dataLock.wait();
                }
            }
            catch (InterruptedException ie) {
                throw new InterruptedIOException();
            }
            if (streamClosed)
                return -1;
            if (threadCrashed)
                throw threadException;
            if (killed)
                return -1;
            int val = data;
            beenRead = true;
            dataLock.notifyAll();
            return val;
        }
    }

    public synchronized int read(byte[] b, int off, int len)
        throws IOException {
        if (b.length - off < 0 || len == 0)
            return 0;
        int v = read();
        if (v < 0)
            return -1;
        b[off] = (byte) v;
        return 1;
    }

    /**
     * Kills this revivable input stream. Makes current and future read calls
     * immediately return -1. The input stream may be revived through
     * {@link #resurrect()}. If this revivable input stream is already killed,
     * this method does nothing.
     *
     * @see #resurrect()
     */
    public void kill() {
        synchronized (dataLock) {
            killed = true;
            dataLock.notifyAll();
        }
    }

    /**
     * Resurrects a killed revivable input stream. This makes it possible to
     * read from this input stream once again. If this revivable input stream is
     * not killed, this method does nothing.
     *
     * @see #kill()
     */
    public synchronized void resurrect() {
        killed = false;
    }

    private class ThreadReader implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    data = in.read();
                    if (data == -1){
                        synchronized (dataLock){
                            streamClosed = true;
                            dataLock.notifyAll();
                            return;
                        }
                    }
                }
                catch (IOException ioe) {
                    synchronized (dataLock) {
                        threadCrashed = true;
                        threadException = ioe; // TODO: Proper wrapping here.
                        return;
                    }
                }

                synchronized (dataLock) {
                    beenRead = false;
                    dataLock.notifyAll();
                    try {
                        while (!beenRead) {
                            dataLock.wait();
                        }
                    }
                    catch (InterruptedException ie) {
                        threadCrashed = true;
                        threadException = new InterruptedIOException();
                        // TODO: Use "real"  exception
                        return;
                    }
                }
                // Data has been read, new iteration.
            }
        }
    }
}
