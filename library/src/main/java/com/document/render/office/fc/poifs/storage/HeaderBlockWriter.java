


package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;



public class HeaderBlockWriter implements HeaderBlockConstants, BlockWritable {
    private final HeaderBlock _header_block;


    public HeaderBlockWriter(POIFSBigBlockSize bigBlockSize) {
        _header_block = new HeaderBlock(bigBlockSize);
    }


    public HeaderBlockWriter(HeaderBlock headerBlock) {
        _header_block = headerBlock;
    }



    static int calculateXBATStorageRequirements(POIFSBigBlockSize bigBlockSize, final int blockCount) {
        return (blockCount > _max_bats_in_header)
                ? BATBlock.calculateXBATStorageRequirements(
                bigBlockSize, blockCount - _max_bats_in_header)
                : 0;
    }



    public BATBlock[] setBATBlocks(final int blockCount,
                                   final int startBlock) {
        BATBlock[] rvalue;
        POIFSBigBlockSize bigBlockSize = _header_block.getBigBlockSize();

        _header_block.setBATCount(blockCount);


        int limit = Math.min(blockCount, _max_bats_in_header);
        int[] bat_blocks = new int[limit];
        for (int j = 0; j < limit; j++) {
            bat_blocks[j] = startBlock + j;
        }
        _header_block.setBATArray(bat_blocks);


        if (blockCount > _max_bats_in_header) {
            int excess_blocks = blockCount - _max_bats_in_header;
            int[] excess_block_array = new int[excess_blocks];

            for (int j = 0; j < excess_blocks; j++) {
                excess_block_array[j] = startBlock + j
                        + _max_bats_in_header;
            }
            rvalue = BATBlock.createXBATBlocks(bigBlockSize, excess_block_array,
                    startBlock + blockCount);
            _header_block.setXBATStart(startBlock + blockCount);
        } else {
            rvalue = BATBlock.createXBATBlocks(bigBlockSize, new int[0], 0);
            _header_block.setXBATStart(POIFSConstants.END_OF_CHAIN);
        }
        _header_block.setXBATCount(rvalue.length);
        return rvalue;
    }


    public void setPropertyStart(final int startBlock) {
        _header_block.setPropertyStart(startBlock);
    }


    public void setSBATStart(final int startBlock) {
        _header_block.setSBATStart(startBlock);
    }


    public void setSBATBlockCount(final int count) {
        _header_block.setSBATBlockCount(count);
    }




    public void writeBlocks(final OutputStream stream)
            throws IOException {
        _header_block.writeData(stream);
    }


    public void writeBlock(ByteBuffer block)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                _header_block.getBigBlockSize().getBigBlockSize()
        );
        _header_block.writeData(baos);

        block.put(baos.toByteArray());
    }


}

