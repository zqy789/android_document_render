

package com.document.render.office.fc.util;

import java.io.IOException;
import java.io.InputStream;


public class LittleEndian implements LittleEndianConsts {

    private LittleEndian() {

    }


    public static short getShort(byte[] data, int offset) {
        int b0 = data[offset] & 0xFF;
        int b1 = data[offset + 1] & 0xFF;
        return (short) ((b1 << 8) + (b0 << 0));
    }



    public static int getUShort(byte[] data, int offset) {
        int b0 = data[offset] & 0xFF;
        int b1 = data[offset + 1] & 0xFF;
        return (b1 << 8) + (b0 << 0);
    }


    public static short getShort(byte[] data) {
        return getShort(data, 0);
    }


    public static int getUShort(byte[] data) {
        return getUShort(data, 0);
    }


    public static int getInt(byte[] data, int offset) {
        int i = offset;
        int b0 = data[i++] & 0xFF;
        int b1 = data[i++] & 0xFF;
        int b2 = data[i++] & 0xFF;
        int b3 = data[i++] & 0xFF;
        return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
    }



    public static int getInt(byte[] data) {
        return getInt(data, 0);
    }



    public static long getUInt(byte[] data, int offset) {
        long retNum = getInt(data, offset);
        return retNum & 0x00FFFFFFFFl;
    }


    public static long getUInt(byte[] data) {
        return getUInt(data, 0);
    }


    public static long getLong(byte[] data, int offset) {
        long result = 0;

        for (int j = offset + LONG_SIZE - 1; j >= offset; j--) {
            result <<= 8;
            result |= 0xff & data[j];
        }
        return result;
    }


    public static float getFloat(byte[] data, int offset) {
        return Float.intBitsToFloat(getInt(data, offset));
    }


    public static double getDouble(byte[] data, int offset) {
        return Double.longBitsToDouble(getLong(data, offset));
    }


    public static void putShort(byte[] data, int offset, short value) {
        int i = offset;
        data[i++] = (byte) ((value >>> 0) & 0xFF);
        data[i++] = (byte) ((value >>> 8) & 0xFF);
    }


    public static void putByte(byte[] data, int offset, int value) {
        data[offset] = (byte) value;
    }


    public static void putUShort(byte[] data, int offset, int value) {
        int i = offset;
        data[i++] = (byte) ((value >>> 0) & 0xFF);
        data[i++] = (byte) ((value >>> 8) & 0xFF);
    }


    public static void putShort(byte[] data, short value) {
        putShort(data, 0, value);
    }



    public static void putInt(byte[] data, int offset, int value) {
        int i = offset;
        data[i++] = (byte) ((value >>> 0) & 0xFF);
        data[i++] = (byte) ((value >>> 8) & 0xFF);
        data[i++] = (byte) ((value >>> 16) & 0xFF);
        data[i++] = (byte) ((value >>> 24) & 0xFF);
    }



    public static void putInt(byte[] data, int value) {
        putInt(data, 0, value);
    }



    public static void putLong(byte[] data, int offset, long value) {
        int limit = LONG_SIZE + offset;
        long v = value;

        for (int j = offset; j < limit; j++) {
            data[j] = (byte) (v & 0xFF);
            v >>= 8;
        }
    }



    public static void putDouble(byte[] data, int offset, double value) {
        putLong(data, offset, Double.doubleToLongBits(value));
    }


    public static short readShort(InputStream stream) throws IOException, BufferUnderrunException {

        return (short) readUShort(stream);
    }

    public static int readUShort(InputStream stream) throws IOException, BufferUnderrunException {

        int ch1 = stream.read();
        int ch2 = stream.read();
        if ((ch1 | ch2) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }


    public static int readInt(InputStream stream)
            throws IOException, BufferUnderrunException {
        int ch1 = stream.read();
        int ch2 = stream.read();
        int ch3 = stream.read();
        int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }


    public static long readLong(InputStream stream)
            throws IOException, BufferUnderrunException {
        int ch1 = stream.read();
        int ch2 = stream.read();
        int ch3 = stream.read();
        int ch4 = stream.read();
        int ch5 = stream.read();
        int ch6 = stream.read();
        int ch7 = stream.read();
        int ch8 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
            throw new BufferUnderrunException();
        }

        return
                ((long) ch8 << 56) +
                        ((long) ch7 << 48) +
                        ((long) ch6 << 40) +
                        ((long) ch5 << 32) +
                        ((long) ch4 << 24) +
                        (ch3 << 16) +
                        (ch2 << 8) +
                        (ch1 << 0);
    }


    public static int ubyteToInt(byte b) {
        return b & 0xFF;
    }


    public static int getUnsignedByte(byte[] data, int offset) {
        return data[offset] & 0xFF;
    }


    public static byte[] getByteArray(byte[] data, int offset, int size) {
        byte[] copy = new byte[size];
        System.arraycopy(data, offset, copy, 0, size);

        return copy;
    }


    public static final class BufferUnderrunException extends IOException {

        BufferUnderrunException() {
            super("buffer underrun");
        }
    }
}
