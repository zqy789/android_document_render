


package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;




public class RawDataBlockList
        extends BlockListImpl {



    public RawDataBlockList(final InputStream stream, POIFSBigBlockSize bigBlockSize)
            throws IOException {
        List<RawDataBlock> blocks = new ArrayList<RawDataBlock>();

        while (true) {
            RawDataBlock block = new RawDataBlock(stream, bigBlockSize.getBigBlockSize());


            if (block.hasData()) {
                blocks.add(block);
            }


            if (block.eof()) {
                break;
            }
        }
        setBlocks(blocks.toArray(new RawDataBlock[blocks.size()]));
    }
}

