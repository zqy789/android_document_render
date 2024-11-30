
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;


public class DecompressableInputStream extends DecodingInputStream {

    private boolean decompress;

    private InflaterInputStream iis;

    private InputStream in;

    private byte[] b = null;

    private int len = 0;

    private int i = 0;


    public DecompressableInputStream(InputStream input) {
        super();
        in = input;
        decompress = false;

        try {
            len = in.available();
            b = new byte[len];
            in.read(b);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public int read() throws IOException {

        if (i >= len) {
            return -1;
        }
        return (int) (b[i++] & 0x000000FF);
    }

    public long skip(long n) throws IOException {

        i += n;
        return n;
    }


    public void startDecompressing() throws IOException {
        decompress = true;
        iis = new InflaterInputStream(in);
    }
}
