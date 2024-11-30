

package com.document.render.office.fc.poifs.filesystem;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;



public final class DocumentOutputStream extends OutputStream {
    private final OutputStream _stream;
    private final int _limit;
    private int _written;


    DocumentOutputStream(OutputStream stream, int limit) {
        _stream = stream;
        _limit = limit;
        _written = 0;
    }


    public void write(int b)
            throws IOException {
        limitCheck(1);
        _stream.write(b);
    }


    public void write(byte b[])
            throws IOException {
        write(b, 0, b.length);
    }


    public void write(byte b[], int off, int len)
            throws IOException {
        limitCheck(len);
        _stream.write(b, off, len);
    }


    public void flush()
            throws IOException {
        _stream.flush();
    }


    public void close() {


    }


    void writeFiller(int totalLimit, byte fill)
            throws IOException {
        if (totalLimit > _written) {
            byte[] filler = new byte[totalLimit - _written];

            Arrays.fill(filler, fill);
            _stream.write(filler);
        }
    }

    private void limitCheck(int toBeWritten)
            throws IOException {
        if ((_written + toBeWritten) > _limit) {
            throw new IOException("tried to write too much data");
        }
        _written += toBeWritten;
    }
}
