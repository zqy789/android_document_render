


package com.document.render.office.fc.poifs.storage;


import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.filesystem.BATManaged;
import com.document.render.office.fc.poifs.filesystem.POIFSDocument;
import com.document.render.office.fc.poifs.property.RootProperty;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class SmallBlockTableWriter
        implements BlockWritable, BATManaged {
    private BlockAllocationTableWriter _sbat;
    private List _small_blocks;
    private int _big_block_count;
    private RootProperty _root;



    public SmallBlockTableWriter(final POIFSBigBlockSize bigBlockSize,
                                 final List documents,
                                 final RootProperty root) {
        _sbat = new BlockAllocationTableWriter(bigBlockSize);
        _small_blocks = new ArrayList();
        _root = root;
        Iterator iter = documents.iterator();

        while (iter.hasNext()) {
            POIFSDocument doc = (POIFSDocument) iter.next();
            BlockWritable[] blocks = doc.getSmallBlocks();

            if (blocks.length != 0) {
                doc.setStartBlock(_sbat.allocateSpace(blocks.length));
                for (int j = 0; j < blocks.length; j++) {
                    _small_blocks.add(blocks[j]);
                }
            } else {
                doc.setStartBlock(POIFSConstants.END_OF_CHAIN);
            }
        }
        _sbat.simpleCreateBlocks();
        _root.setSize(_small_blocks.size());
        _big_block_count = SmallDocumentBlock.fill(bigBlockSize, _small_blocks);
    }



    public int getSBATBlockCount() {
        return (_big_block_count + 15) / 16;
    }



    public BlockAllocationTableWriter getSBAT() {
        return _sbat;
    }





    public int countBlocks() {
        return _big_block_count;
    }



    public void setStartBlock(int start_block) {
        _root.setStartBlock(start_block);
    }






    public void writeBlocks(final OutputStream stream)
            throws IOException {
        Iterator iter = _small_blocks.iterator();

        while (iter.hasNext()) {
            ((BlockWritable) iter.next()).writeBlocks(stream);
        }
    }


}
