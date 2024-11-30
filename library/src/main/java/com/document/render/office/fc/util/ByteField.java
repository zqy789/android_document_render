


package com.document.render.office.fc.util;


import com.document.render.office.fc.util.LittleEndian.BufferUnderrunException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;



public class ByteField
        implements FixedField {
    private static final byte _default_value = 0;
    private final int _offset;
    private byte _value;



    public ByteField(final int offset)
            throws ArrayIndexOutOfBoundsException {
        this(offset, _default_value);
    }



    public ByteField(final int offset, final byte value)
            throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException(
                    "offset cannot be negative");
        }
        _offset = offset;
        set(value);
    }



    public ByteField(final int offset, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset);
        readFromBytes(data);
    }



    public ByteField(final int offset, final byte value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        this(offset, value);
        writeToBytes(data);
    }



    public byte get() {
        return _value;
    }



    public void set(final byte value) {
        _value = value;
    }



    public void set(final byte value, final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        set(value);
        writeToBytes(data);
    }





    public void readFromBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        _value = data[_offset];
    }



    public void readFromStream(final InputStream stream)
            throws IOException, BufferUnderrunException {

        int ib = stream.read();
        if (ib < 0) {
            throw new BufferUnderflowException();
        }
        _value = (byte) ib;
    }



    public void writeToBytes(final byte[] data)
            throws ArrayIndexOutOfBoundsException {
        data[_offset] = _value;
    }



    public String toString() {
        return String.valueOf(_value);
    }


}

