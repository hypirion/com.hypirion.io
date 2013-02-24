package com.hypirion.io;

import java.io.InputStream;
import java.io.IOException;

public class RevivableInputStream extends InputStream {
    protected boolean killed;
    protected InputStream in;

    public RevivableInputStream(InputStream in) {
        this.in = in;
        killed = false;
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

    public int read() throws IOException {
        if (killed)
            return -1;
        else
            return in.read();
    }

    public int read(byte[] b) throws IOException {
        if (killed)
            return -1;
        else
            return in.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (killed)
            return -1;
        else
            return in.read(b, off, len);
    }

    public void reset() throws IOException {
        in.reset();
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    public void kill() {
        killed = true;
    }

    public void ressurect() {
        killed = false;
    }
}
