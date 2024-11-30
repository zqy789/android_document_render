
package com.document.render.office.thirdpart.emf.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class DCTInputStream extends FilterInputStream {


    public DCTInputStream(InputStream input) {
        super(input);
    }


    public int read() throws IOException {
        throw new IOException(getClass()
                + ": read() not implemented, use readImage().");
    }
}
