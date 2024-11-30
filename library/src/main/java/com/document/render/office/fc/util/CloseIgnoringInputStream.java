

package com.document.render.office.fc.util;

import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;

import java.io.FilterInputStream;
import java.io.InputStream;



public class CloseIgnoringInputStream extends FilterInputStream {
    public CloseIgnoringInputStream(InputStream in) {
        super(in);
    }

    public void close() {

        return;
    }
}
