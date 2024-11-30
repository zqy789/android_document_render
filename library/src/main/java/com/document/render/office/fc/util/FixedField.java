


package com.document.render.office.fc.util;


import com.document.render.office.fc.util.LittleEndian.BufferUnderrunException;

import java.io.IOException;
import java.io.InputStream;



public interface FixedField {



    public void readFromBytes(byte[] data)
            throws ArrayIndexOutOfBoundsException;



    public void readFromStream(InputStream stream)
            throws IOException, BufferUnderrunException;



    public void writeToBytes(byte[] data)
            throws ArrayIndexOutOfBoundsException;



    public String toString();
}

