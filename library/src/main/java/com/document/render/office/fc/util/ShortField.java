


package com.document.render.office.fc.util;


import com.document.render.office.fc.util.LittleEndian.BufferUnderrunException;

import java.io.IOException;
import java.io.InputStream;



public class ShortField
        implements FixedField {
    private final int _offset;
    private short _value;



    public ShortField(final int offset)
            throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset: "
                    + offset);
        }
        _offset = offset;
    }



    public ShortField(final int offset, final short value)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value);
    }



    public ShortField(final int offset, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        readFromBytes(data);
    }



    public ShortField(final int offset, final short value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        set(value, data);
    }



    public short get() {
        return _value;
    }



    public void set(final short value) {
        _value = value;
    }



    public void set(final short value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        _value = value;
        writeToBytes(data);
    }





    public void readFromBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        _value = LittleEndian.getShort(data, _offset);
    }



    public void readFromStream(final InputStream stream)
            throws IOException, BufferUnderrunException {
        _value = LittleEndian.readShort(stream);
    }



    public void writeToBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        LittleEndian.putShort(data, _offset, _value);
    }



    public String toString() {
        return String.valueOf(_value);
    }


}

