

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.IntList;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianConsts;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public final class BlockAllocationTableReader {
    private static final POILogger _logger = POILogFactory.getLogger(BlockAllocationTableReader.class);


    private static final int MAX_BLOCK_COUNT = 65535;
    private final IntList _entries;
    private POIFSBigBlockSize bigBlockSize;


    public BlockAllocationTableReader(POIFSBigBlockSize bigBlockSize, int block_count, int[] block_array,
                                      int xbat_count, int xbat_index, BlockList raw_block_list) throws IOException {
        this(bigBlockSize);

        sanityCheckBlockCount(block_count);







        int limit = Math.min(block_count, block_array.length);
        int block_index;


        RawDataBlock blocks[] = new RawDataBlock[block_count];


        for (block_index = 0; block_index < limit; block_index++) {

            int nextOffset = block_array[block_index];
            if (nextOffset > raw_block_list.blockCount()) {
                throw new IOException("Your file contains " + raw_block_list.blockCount() +
                        " sectors, but the initial DIFAT array at index " + block_index +
                        " referenced block # " + nextOffset + ". This isn't allowed and " +
                        " your file is corrupt");
            }

            blocks[block_index] =
                    (RawDataBlock) raw_block_list.remove(nextOffset);
        }


        if (block_index < block_count) {


            if (xbat_index < 0) {
                throw new IOException(
                        "BAT count exceeds limit, yet XBAT index indicates no valid entries");
            }
            int chain_index = xbat_index;
            int max_entries_per_block = bigBlockSize.getXBATEntriesPerBlock();
            int chain_index_offset = bigBlockSize.getNextXBATChainOffset();




            for (int j = 0; j < xbat_count; j++) {
                limit = Math.min(block_count - block_index,
                        max_entries_per_block);
                byte[] data = raw_block_list.remove(chain_index).getData();
                int offset = 0;

                for (int k = 0; k < limit; k++) {
                    blocks[block_index++] =
                            (RawDataBlock) raw_block_list
                                    .remove(LittleEndian.getInt(data, offset));
                    offset += LittleEndianConsts.INT_SIZE;
                }
                chain_index = LittleEndian.getInt(data, chain_index_offset);
                if (chain_index == POIFSConstants.END_OF_CHAIN) {
                    break;
                }
            }
        }
        if (block_index != block_count) {
            throw new IOException("Could not find all blocks");
        }



        setEntries(blocks, raw_block_list);
    }


    BlockAllocationTableReader(POIFSBigBlockSize bigBlockSize, ListManagedBlock[] blocks, BlockList raw_block_list)
            throws IOException {
        this(bigBlockSize);
        setEntries(blocks, raw_block_list);
    }

    BlockAllocationTableReader(POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
        _entries = new IntList();
    }

    public static void sanityCheckBlockCount(int block_count) throws IOException {
        if (block_count <= 0) {
            throw new IOException(
                    "Illegal block count; minimum count is 1, got " +
                            block_count + " instead"
            );
        }
        if (block_count > MAX_BLOCK_COUNT) {
            throw new IOException(
                    "Block count " + block_count +
                            " is too high. POI maximum is " + MAX_BLOCK_COUNT + "."
            );
        }
    }


    ListManagedBlock[] fetchBlocks(int startBlock, int headerPropertiesStartBlock,
                                   BlockList blockList) throws IOException {
        List<ListManagedBlock> blocks = new ArrayList<ListManagedBlock>();
        int currentBlock = startBlock;
        boolean firstPass = true;
        ListManagedBlock dataBlock = null;





        while (currentBlock != POIFSConstants.END_OF_CHAIN) {
            try {

                dataBlock = blockList.remove(currentBlock);
                blocks.add(dataBlock);

                currentBlock = _entries.get(currentBlock);
                firstPass = false;
            } catch (IOException e) {
                if (currentBlock == headerPropertiesStartBlock) {

                    _logger.log(POILogger.WARN, "Warning, header block comes after data blocks in POIFS block listing");
                    currentBlock = POIFSConstants.END_OF_CHAIN;
                } else if (currentBlock == 0 && firstPass) {


                    _logger.log(POILogger.WARN, "Warning, incorrectly terminated empty data blocks in POIFS block listing (should end at -2, ended at 0)");
                    currentBlock = POIFSConstants.END_OF_CHAIN;
                } else {

                    throw e;
                }
            }
        }

        return blocks.toArray(new ListManagedBlock[blocks.size()]);
    }




    boolean isUsed(int index) {

        try {
            return _entries.get(index) != -1;
        } catch (IndexOutOfBoundsException e) {

            return false;
        }
    }


    int getNextBlockIndex(int index) throws IOException {
        if (isUsed(index)) {
            return _entries.get(index);
        }
        throw new IOException("index " + index + " is unused");
    }


    private void setEntries(ListManagedBlock[] blocks, BlockList raw_blocks) throws IOException {
        int limit = bigBlockSize.getBATEntriesPerBlock();

        for (int block_index = 0; block_index < blocks.length; block_index++) {
            byte[] data = blocks[block_index].getData();
            int offset = 0;

            for (int k = 0; k < limit; k++) {
                int entry = LittleEndian.getInt(data, offset);

                if (entry == POIFSConstants.UNUSED_BLOCK) {
                    raw_blocks.zap(_entries.size());
                }
                _entries.add(entry);
                offset += LittleEndianConsts.INT_SIZE;
            }


            blocks[block_index] = null;
        }
        raw_blocks.setBAT(this);
    }
}
