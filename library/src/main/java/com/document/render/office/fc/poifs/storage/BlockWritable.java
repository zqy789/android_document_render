


package com.document.render.office.fc.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;



public interface BlockWritable {



    public void writeBlocks(final OutputStream stream)
            throws IOException;
}

