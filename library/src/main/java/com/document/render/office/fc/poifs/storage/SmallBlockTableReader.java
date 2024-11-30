

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.property.RootProperty;

import java.io.IOException;



public final class SmallBlockTableReader {


    public static BlockList getSmallDocumentBlocks(
            final POIFSBigBlockSize bigBlockSize,
            final RawDataBlockList blockList, final RootProperty root,
            final int sbatStart)
            throws IOException {

        ListManagedBlock[] smallBlockBlocks =
                blockList.fetchBlocks(root.getStartBlock(), -1);


        BlockList list = new SmallDocumentBlockList(
                SmallDocumentBlock.extract(bigBlockSize, smallBlockBlocks));


        new BlockAllocationTableReader(bigBlockSize,
                blockList.fetchBlocks(sbatStart, -1),
                list);
        return list;
    }
}
