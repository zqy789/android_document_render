
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;
import java.io.InputStream;


public class ByteCountInputStream extends ByteOrderInputStream {

    private int index;

    private int[] size;

    private long len;


    public ByteCountInputStream(InputStream in, boolean littleEndian,
                                int stackDepth) {
        super(in, littleEndian);
        size = new int[stackDepth];
        index = -1;
        len = 0;
    }

    public int read() throws IOException {

        if (index == -1) {
            len++;
            return super.read();
        }


        if (size[index] <= 0) {
            return -1;
        }


        size[index]--;

        len++;
        return super.read();
    }


    public void pushBuffer(int len) {
        if (index >= size.length - 1) {
            System.err
                    .println("ByteCountInputStream: trying to push more buffers than stackDepth: "
                            + size.length);
            return;
        }

        if (index >= 0) {
            if (size[index] < len) {
                System.err
                        .println("ByteCountInputStream: trying to set a length: "
                                + len
                                + ", longer than the underlying buffer: "
                                + size[index]);
                return;
            }
            size[index] -= len;
        }
        index++;
        size[index] = len;
    }


    public byte[] popBuffer() throws IOException {
        if (index >= 0) {
            int len = size[index];
            if (len > 0) {
                return readByte(len);
            } else if (len < 0) {
                System.err.println("ByteCountInputStream: Internal Error");
            }
            index--;
        }
        return null;
    }


    public long getLength() {
        return (index >= 0) ? size[index] : len;
    }
}
