

package com.document.render.office.fc.hwpf.model.io;

import com.document.render.office.fc.util.Internal;

import java.io.ByteArrayOutputStream;


@Internal
public final class HWPFOutputStream extends ByteArrayOutputStream {

    int _offset;

    public HWPFOutputStream() {
        super();
    }

    public int getOffset() {
        return _offset;
    }

    public synchronized void reset() {
        super.reset();
        _offset = 0;
    }

    public synchronized void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        _offset += len;
    }

    public synchronized void write(int b) {
        super.write(b);
        _offset++;
    }
}
