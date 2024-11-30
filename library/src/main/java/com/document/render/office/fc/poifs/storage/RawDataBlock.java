


package com.document.render.office.fc.poifs.storage;


import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.IOUtils;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.IOException;
import java.io.InputStream;



public class RawDataBlock
        implements ListManagedBlock {
    private static POILogger log = POILogFactory.getLogger(RawDataBlock.class);
    private byte[] _data;
    private boolean _eof;
    private boolean _hasData;


    public RawDataBlock(final InputStream stream)
            throws IOException {
        this(stream, POIFSConstants.SMALLER_BIG_BLOCK_SIZE);
    }


    public RawDataBlock(final InputStream stream, int blockSize)
            throws IOException {
        _data = new byte[blockSize];
        int count = IOUtils.readFully(stream, _data);
        _hasData = (count > 0);

        if (count == -1) {
            _eof = true;
        } else if (count != blockSize) {



            _eof = true;
            String type = " byte" + ((count == 1) ? ("")
                    : ("s"));

            log.log(POILogger.ERROR,
                    "Unable to read entire block; " + count
                            + type + " read before EOF; expected "
                            + blockSize + " bytes. Your document "
                            + "was either written by software that "
                            + "ignores the spec, or has been truncated!"
            );
        } else {
            _eof = false;
        }
    }


    public boolean eof() {
        return _eof;
    }


    public boolean hasData() {
        return _hasData;
    }

    public String toString() {
        return "RawDataBlock of size " + _data.length;
    }




    public byte[] getData()
            throws IOException {
        if (!hasData()) {
            throw new IOException("Cannot return empty data");
        }
        return _data;
    }


    public int getBigBlockSize() {
        return _data.length;
    }


}

