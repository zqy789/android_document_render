

package com.document.render.office.fc.util;


public interface LittleEndianInput {
    int available();

    byte readByte();

    int readUByte();

    short readShort();

    int readUShort();

    int readInt();

    long readLong();

    double readDouble();

    void readFully(byte[] buf);

    void readFully(byte[] buf, int off, int len);
}
