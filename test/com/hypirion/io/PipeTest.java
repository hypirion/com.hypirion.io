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
    public void testMultipleStreams() throws Exception {
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
}
