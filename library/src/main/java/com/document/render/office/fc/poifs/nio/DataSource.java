

package com.document.render.office.fc.poifs.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;


public abstract class DataSource {
    public abstract ByteBuffer read(int length, long position) throws IOException;

    public abstract void write(ByteBuffer src, long position) throws IOException;

    public abstract long size() throws IOException;


    public abstract void close() throws IOException;


    public abstract void copyTo(OutputStream stream) throws IOException;
}
