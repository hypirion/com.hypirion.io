/*
 * Copyright (c) 2013 Jean Niklas L'orange. All rights reserved.
 *
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file LICENSE at the root of this distribution.
 *
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 *
 * You must not remove this notice, or any other, from this software.
 */

package com.hypirion.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringWriter;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.hypirion.io.Pipe;

import org.junit.Test;
import static org.junit.Assert.*;

public class PipeTest {

    /**
     * Tests that an InputStream with random ascii characters will be completely
     * piped through the pipe.
     */
    @Test(timeout=1000)
    public void testBasicStreamCapabilities() throws Exception {
        PipedOutputStream sink = new PipedOutputStream();
        PipedInputStream source = new PipedInputStream(sink);
        String inString = RandomStringUtils.randomAscii(37);
        InputStream inStream = IOUtils.toInputStream(inString);
        Pipe p = new Pipe(inStream, sink);
        p.start();
        p.join();
        inStream.close();
        sink.close();
        String outString = IOUtils.toString(source);
        source.close();
        assertEquals(inString, outString);
    }

    /**
     * Test that multiple InputStreams with random ascii characters will be
     * completely piped through the pipe, and not close the OutputStream.
     */
    @Test(timeout=1000)
    public void testStreamConcatenation() throws Exception {
        PipedOutputStream sink = new PipedOutputStream();
        PipedInputStream source = new PipedInputStream(sink);
        String totalString = "";
        for (int i = 0; i < 10; i++) {
            String inString = RandomStringUtils.randomAscii(37);
            totalString += inString;
            InputStream inStream = IOUtils.toInputStream(inString);
            Pipe p = new Pipe(inStream, sink);
            p.start();
            p.join();
            inStream.close();
        }
        sink.close();
        String outString = IOUtils.toString(source);
        source.close();
        assertEquals(totalString, outString);
    }

    /**
     * Test that basic reader/writer capabilities work as expected.
     */
    @Test(timeout=1000)
    public void testBasicReaderCapabilities() throws Exception {
        String input = RandomStringUtils.random(37);
        StringReader rdr = new StringReader(input);
        StringWriter wrt = new StringWriter();
        Pipe p = new Pipe(rdr, wrt);
        p.start();
        p.join();
        String output = wrt.toString();
        rdr.close();
        wrt.close();
        assertEquals(input, output);
    }

    /**
     * Test that reading from multiple readers doesn't change or stop the
     * writer.
     */
    @Test(timeout=1000)
    public void testReaderConcatenation() throws Exception {
        String input = "";
        StringWriter wrt = new StringWriter();
        for (int i = 0; i < 10; i++) {
            String thisInput = RandomStringUtils.random(37);
            input += thisInput;
            StringReader rdr = new StringReader(thisInput);
            Pipe p = new Pipe(rdr, wrt);
            p.start();
            p.join();
            rdr.close();
        }
        String output = wrt.toString();
        wrt.close();
        assertEquals(input, output);
    }

    /**
     * Test that multiple pipes running concurrently won't leave any chars
     * behind.
     */
    @Test(timeout = 2000)
    public void testConcurrentReaderPiping() throws Exception {
        final int charCount = 30;
        final char[] vals = "123456789".toCharArray();
        final int n = vals.length;
        Reader[] readers = new Reader[n];
        // Generate n readers with charCount equal elements in them.
        for (int i = 0; i < n; i++) {
            String s = "";
            for (int j = 0; j < charCount; j++) {
                s += vals[i];
            }
            StringReader sr = new StringReader(s);
            readers[i] = new SlowReader(sr);
        }
        StringWriter wrt = new StringWriter();
        Pipe[] pipes = new Pipe[n];
        for (int i = 0; i < n; i++) {
            pipes[i] = new Pipe(readers[i], wrt);
        }
        for (int i = 0; i < n; i++) {
            pipes[i].start();
        }
        for (int i = 0; i < n; i++) {
            pipes[i].join();
            readers[i].close();
        }

        // Count up elements and ensure that we've got the correct amount of
        // characters of each type.
        String out = wrt.toString();
        char[] output = out.toCharArray();
        wrt.close();
        for (char v : vals) {
            int sum = 0;
            for (char c : output) {
                if (c == v) {
                    sum++;
                }
            }
            assertEquals(charCount, sum);
        }
    }

    static class SlowReader extends Reader {
        Reader r;

        public SlowReader(Reader r) {
            this.r = r;
        }

        @Override
        synchronized public void close() throws IOException {
            r.close();
        }

        @Override
        synchronized public int read(char[] cbuf, int off, int len)
            throws IOException {
            try {
                Thread.yield();
            } catch (Exception e) {}
            return r.read(cbuf, off, 1);
        }
    }
}
