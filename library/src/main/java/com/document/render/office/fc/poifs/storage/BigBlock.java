


package com.document.render.office.fc.poifs.storage;



import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;

import java.io.IOException;
import java.io.OutputStream;


abstract class BigBlock
        implements BlockWritable {

    protected POIFSBigBlockSize bigBlockSize;

    protected BigBlock(POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
    }



    protected void doWriteData(final OutputStream stream, final byte[] data)
            throws IOException {
        stream.write(data);
    }



    abstract void writeData(final OutputStream stream)
            throws IOException;





    public void writeBlocks(final OutputStream stream)
            throws IOException {
        writeData(stream);
    }


}

