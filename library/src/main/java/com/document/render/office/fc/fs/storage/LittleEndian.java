package com.document.render.office.fc.fs.storage;

public class LittleEndian {
    public static final int BYTE_SIZE = 1;
    public static final int SHORT_SIZE = 2;
    public static final int INT_SIZE = 4;
    public static final int LONG_SIZE = 8;
    public static final int DOUBLE_SIZE = 8;


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

}
