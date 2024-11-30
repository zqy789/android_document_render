

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.filesystem.BATManaged;
import com.document.render.office.fc.util.IntList;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;



public final class BlockAllocationTableWriter implements BlockWritable, BATManaged {
    private IntList _entries;
    private BATBlock[] _blocks;
    private int _start_block;
    private POIFSBigBlockSize _bigBlockSize;


    public BlockAllocationTableWriter(POIFSBigBlockSize bigBlockSize) {
        _bigBlockSize = bigBlockSize;
        _start_block = POIFSConstants.END_OF_CHAIN;
        _entries = new IntList();
        _blocks = new BATBlock[0];
    }


    public static void writeBlock(final BATBlock bat, final ByteBuffer block)
            throws IOException {
        bat.writeData(block);
    }


    public int createBlocks() {
        int xbat_blocks = 0;
        int bat_blocks = 0;

        while (true) {
            int calculated_bat_blocks =
                    BATBlock.calculateStorageRequirements(_bigBlockSize,
                            bat_blocks
                                    + xbat_blocks
                                    + _entries.size());
            int calculated_xbat_blocks =
                    HeaderBlockWriter.calculateXBATStorageRequirements(
                            _bigBlockSize, calculated_bat_blocks);

            if ((bat_blocks == calculated_bat_blocks)
                    && (xbat_blocks == calculated_xbat_blocks)) {


                break;
            }
            bat_blocks = calculated_bat_blocks;
            xbat_blocks = calculated_xbat_blocks;
        }
        int startBlock = allocateSpace(bat_blocks);

        allocateSpace(xbat_blocks);
        simpleCreateBlocks();
        return startBlock;
    }


    public int allocateSpace(final int blockCount) {
        int startBlock = _entries.size();

        if (blockCount > 0) {
            int limit = blockCount - 1;
            int index = startBlock + 1;

            for (int k = 0; k < limit; k++) {
                _entries.add(index++);
            }
            _entries.add(POIFSConstants.END_OF_CHAIN);
        }
        return startBlock;
    }


    public int getStartBlock() {
        return _start_block;
    }


    public void setStartBlock(int start_block) {
        _start_block = start_block;
    }


    void simpleCreateBlocks() {
        _blocks = BATBlock.createBATBlocks(_bigBlockSize, _entries.toArray());
    }


    public void writeBlocks(final OutputStream stream)
            throws IOException {
        for (int j = 0; j < _blocks.length; j++) {
            _blocks[j].writeBlocks(stream);
        }
    }


    public int countBlocks() {
        return _blocks.length;
    }
}
