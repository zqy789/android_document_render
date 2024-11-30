

package com.document.render.office.fc.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class LittleEndianInputStream extends FilterInputStream implements LittleEndianInput {
    public LittleEndianInputStream(InputStream is) {
        super(is);
    }

    private static void checkEOF(int value) {
        if (value < 0) {
            throw new RuntimeException("Unexpected end-of-file");
        }
    }

    public int available() {
        try {
            return super.available();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte readByte() {
        return (byte) readUByte();
    }

    public int readUByte() {
        int ch;
        try {
            ch = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkEOF(ch);
        return ch;
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public int readInt() {
        int ch1;
        int ch2;
        int ch3;
        int ch4;
        try {
            ch1 = in.read();
            ch2 = in.read();
            ch3 = in.read();
            ch4 = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkEOF(ch1 | ch2 | ch3 | ch4);
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    public long readLong() {
        int b0;
        int b1;
        int b2;
        int b3;
        int b4;
        int b5;
        int b6;
        int b7;
        try {
            b0 = in.read();
            b1 = in.read();
            b2 = in.read();
            b3 = in.read();
            b4 = in.read();
            b5 = in.read();
            b6 = in.read();
            b7 = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkEOF(b0 | b1 | b2 | b3 | b4 | b5 | b6 | b7);
        return (((long) b7 << 56) +
                ((long) b6 << 48) +
                ((long) b5 << 40) +
                ((long) b4 << 32) +
                ((long) b3 << 24) +
                (b2 << 16) +
                (b1 << 8) +
                (b0 << 0));
    }

    public short readShort() {
        return (short) readUShort();
    }

    public int readUShort() {
        int ch1;
        int ch2;
        try {
            ch1 = in.read();
            ch2 = in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        checkEOF(ch1 | ch2);
        return (ch2 << 8) + (ch1 << 0);
    }

    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) {
        int max = off + len;
        for (int i = off; i < max; i++) {
            int ch;
            try {
                ch = in.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            checkEOF(ch);
            buf[i] = (byte) ch;
        }
    }
}
