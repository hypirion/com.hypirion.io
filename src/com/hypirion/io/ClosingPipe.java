package com.hypirion.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.Reader;
import java.io.Closeable;
import java.io.IOException;

public class ClosingPipe extends Pipe {

    protected final Thread closer;

    public ClosingPipe(InputStream in, OutputStream out) {
        this(in, out, DEFAULT_BUFFER_SIZE);
    }

    public ClosingPipe(InputStream in, OutputStream out, int bufsize) {
        super(in, out, bufsize);
        closer = setupCloser(out);
    }

    public ClosingPipe(Reader in, Writer out) {
        this(in, out, DEFAULT_BUFFER_SIZE);
    }

    public ClosingPipe(Reader in, Writer out, int bufsize) {
        super(in, out, bufsize);
        closer = setupCloser(out);
    }

    private Thread setupCloser(Closeable out) {
        Runnable pt = new OutCloser(out);
        Thread closer = new Thread(pt);
        closer.setName(String.format("ClosingPipeCloser %d", this.hashCode()));
        closer.setDaemon(true);
        closer.start();
        return closer;
    }

    private class OutCloser implements Runnable {
        private final Closeable out;

        public OutCloser(Closeable out) {
            this.out = out;
        }

        @Override
        public void run() {
            try {
                ClosingPipe.this.join();
                out.close();
            }
            catch (InterruptedException ie) {
                run(); // We're not giving up that easily.
            }
            catch (IOException ioe) {
                // The closable is somehow broken, leave it be.
            }
        }
    }
}
