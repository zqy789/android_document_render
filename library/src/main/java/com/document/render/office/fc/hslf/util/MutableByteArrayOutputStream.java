

package com.document.render.office.fc.hslf.util;

import java.io.ByteArrayOutputStream;



public final class MutableByteArrayOutputStream extends ByteArrayOutputStream {

    public int getBytesWritten() {
        return -1;
    }


    public void write(byte[] b) {
    }

    public void write(int b) {
    }


    public void overwrite(byte[] b, int startPos) {
    }
}
