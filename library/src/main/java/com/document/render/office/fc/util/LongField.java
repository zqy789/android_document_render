


package com.document.render.office.fc.util;


import com.document.render.office.fc.util.LittleEndian.BufferUnderrunException;

import java.io.IOException;
import java.io.InputStream;



public class LongField
        implements FixedField {
    private final int _offset;
    private long _value;



    public LongField(final int offset)
            throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset: "
                    + offset);
        }
        _offset = offset;
    }



    public LongField(final int offset, final long value)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value);
    }



    public LongField(final int offset, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        readFromBytes(data);
    }



    public LongField(final int offset, final long value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value, data);
    }



    public long get() {
        return _value;
    }



    public void set(final long value) {
        _value = value;
    }



    public void set(final long value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        _value = value;
        writeToBytes(data);
    }





    public void readFromBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        _value = LittleEndian.getLong(data, _offset);
    }



    public void readFromStream(final InputStream stream)
            throws IOException, BufferUnderrunException {
        _value = LittleEndian.readLong(stream);
    }



    public void writeToBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        LittleEndian.putLong(data, _offset, _value);
    }



    public String toString() {
        return String.valueOf(_value);
    }


}

