

package com.document.render.office.fc.hssf.record.crypto;

import com.document.render.office.fc.hssf.record.BiffHeaderInput;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianInputStream;

import java.io.InputStream;



public final class Biff8DecryptingStream implements BiffHeaderInput, LittleEndianInput {

    private final LittleEndianInput _le;
    private final Biff8RC4 _rc4;

    public Biff8DecryptingStream(InputStream in, int initialOffset, Biff8EncryptionKey key) {
        _rc4 = new Biff8RC4(initialOffset, key);

        if (in instanceof LittleEndianInput) {

            _le = (LittleEndianInput) in;
        } else {

            _le = new LittleEndianInputStream(in);
        }
    }

    public int available() {
        return _le.available();
    }


    public int readRecordSID() {
        int sid = _le.readUShort();
        _rc4.skipTwoBytes();
        _rc4.startRecord(sid);
        return sid;
    }


    public int readDataSize() {
        int dataSize = _le.readUShort();
        _rc4.skipTwoBytes();
        return dataSize;
    }

    public double readDouble() {
        long valueLongBits = readLong();
        double result = Double.longBitsToDouble(valueLongBits);
        if (Double.isNaN(result)) {
            throw new RuntimeException("Did not expect to read NaN");
        }
        return result;
    }

    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) {
        _le.readFully(buf, off, len);
        _rc4.xor(buf, off, len);
    }


    public int readUByte() {
        return _rc4.xorByte(_le.readUByte());
    }

    public byte readByte() {
        return (byte) _rc4.xorByte(_le.readUByte());
    }


    public int readUShort() {
        return _rc4.xorShort(_le.readUShort());
    }

    public short readShort() {
        return (short) _rc4.xorShort(_le.readUShort());
    }

    public int readInt() {
        return _rc4.xorInt(_le.readInt());
    }

    public long readLong() {
        return _rc4.xorLong(_le.readLong());
    }
}
