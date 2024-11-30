


package com.document.render.office.fc.util;


import com.document.render.office.fc.util.LittleEndian.BufferUnderrunException;

import java.io.IOException;
import java.io.InputStream;



public class IntegerField
        implements FixedField {
    private final int _offset;
    private int _value;



    public IntegerField(final int offset)
            throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("negative offset");
        }
        _offset = offset;
    }



    public IntegerField(final int offset, final int value)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value);
    }



    public IntegerField(final int offset, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        readFromBytes(data);
    }



    public IntegerField(final int offset, final int value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value, data);
    }



    public int get() {
        return _value;
    }



    public void set(final int value) {
        _value = value;
    }



    public void set(final int value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        _value = value;
        writeToBytes(data);
    }





    public void readFromBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        _value = LittleEndian.getInt(data, _offset);
    }



    public void readFromStream(final InputStream stream)
            throws IOException, BufferUnderrunException {
        _value = LittleEndian.readInt(stream);
    }



    public void writeToBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        LittleEndian.putInt(data, _offset, _value);
    }



    public String toString() {
        return String.valueOf(_value);
    }


}

