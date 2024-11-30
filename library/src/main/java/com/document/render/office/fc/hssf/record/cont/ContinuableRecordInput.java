

package com.document.render.office.fc.hssf.record.cont;


import com.document.render.office.fc.hssf.record.ContinueRecord;
import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.util.LittleEndianInput;



public class ContinuableRecordInput implements LittleEndianInput {
    private final RecordInputStream _in;

    public ContinuableRecordInput(RecordInputStream in) {
        _in = in;
    }

    public int available() {
        return _in.available();
    }

    public byte readByte() {
        return _in.readByte();
    }

    public int readUByte() {
        return _in.readUByte();
    }

    public short readShort() {
        return _in.readShort();
    }

    public int readUShort() {
        int ch1 = readUByte();
        int ch2 = readUByte();
        return (ch2 << 8) + (ch1 << 0);
    }

    public int readInt() {
        int ch1 = _in.readUByte();
        int ch2 = _in.readUByte();
        int ch3 = _in.readUByte();
        int ch4 = _in.readUByte();
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    public long readLong() {
        int b0 = _in.readUByte();
        int b1 = _in.readUByte();
        int b2 = _in.readUByte();
        int b3 = _in.readUByte();
        int b4 = _in.readUByte();
        int b5 = _in.readUByte();
        int b6 = _in.readUByte();
        int b7 = _in.readUByte();
        return (((long) b7 << 56) +
                ((long) b6 << 48) +
                ((long) b5 << 40) +
                ((long) b4 << 32) +
                ((long) b3 << 24) +
                (b2 << 16) +
                (b1 << 8) +
                (b0 << 0));
    }

    public double readDouble() {
        return _in.readDouble();
    }

    public void readFully(byte[] buf) {
        _in.readFully(buf);
    }

    public void readFully(byte[] buf, int off, int len) {
        _in.readFully(buf, off, len);
    }

}
