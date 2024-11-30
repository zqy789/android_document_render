

package com.document.render.office.fc.hssf.record.cont;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.util.DelayableLittleEndianOutput;
import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianOutput;


final class UnknownLengthRecordOutput implements LittleEndianOutput {
    private static final int MAX_DATA_SIZE = RecordInputStream.MAX_RECORD_DATA_SIZE;

    private final LittleEndianOutput _originalOut;

    private final LittleEndianOutput _dataSizeOutput;
    private final byte[] _byteBuffer;
    private LittleEndianOutput _out;
    private int _size;

    public UnknownLengthRecordOutput(LittleEndianOutput out, int sid) {
        _originalOut = out;
        out.writeShort(sid);
        if (out instanceof DelayableLittleEndianOutput) {

            DelayableLittleEndianOutput dleo = (DelayableLittleEndianOutput) out;
            _dataSizeOutput = dleo.createDelayedOutput(2);
            _byteBuffer = null;
            _out = out;
        } else {

            _dataSizeOutput = out;
            _byteBuffer = new byte[RecordInputStream.MAX_RECORD_DATA_SIZE];
            _out = new LittleEndianByteArrayOutputStream(_byteBuffer, 0);
        }
    }


    public int getTotalSize() {
        return 4 + _size;
    }

    public int getAvailableSpace() {
        if (_out == null) {
            throw new IllegalStateException("Record already terminated");
        }
        return MAX_DATA_SIZE - _size;
    }


    public void terminate() {
        if (_out == null) {
            throw new IllegalStateException("Record already terminated");
        }
        _dataSizeOutput.writeShort(_size);
        if (_byteBuffer != null) {
            _originalOut.write(_byteBuffer, 0, _size);
            _out = null;
            return;
        }
        _out = null;
    }

    public void write(byte[] b) {
        _out.write(b);
        _size += b.length;
    }

    public void write(byte[] b, int offset, int len) {
        _out.write(b, offset, len);
        _size += len;
    }

    public void writeByte(int v) {
        _out.writeByte(v);
        _size += 1;
    }

    public void writeDouble(double v) {
        _out.writeDouble(v);
        _size += 8;
    }

    public void writeInt(int v) {
        _out.writeInt(v);
        _size += 4;
    }

    public void writeLong(long v) {
        _out.writeLong(v);
        _size += 8;
    }

    public void writeShort(int v) {
        _out.writeShort(v);
        _size += 2;
    }
}
