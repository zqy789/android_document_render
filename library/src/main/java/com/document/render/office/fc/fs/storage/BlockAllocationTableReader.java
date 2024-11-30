package com.document.render.office.fc.fs.storage;

import com.document.render.office.fc.fs.filesystem.BlockSize;
import com.document.render.office.fc.fs.filesystem.CFBConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class BlockAllocationTableReader {


    private final IntList _entries;
    private BlockSize bigBlockSize;


    public BlockAllocationTableReader(BlockSize bigBlockSize, int block_count,
                                      int[] block_array, int xbat_count, int xbat_index, BlockList raw_block_list)
            throws IOException {
        this.bigBlockSize = bigBlockSize;
        _entries = new IntList();
        int limit = Math.min(block_count, block_array.length);
        int block_index;


        RawDataBlock blocks[] = new RawDataBlock[block_count];

        for (block_index = 0; block_index < limit; block_index++) {

            int nextOffset = block_array[block_index];
            if (nextOffset > raw_block_list.blockCount()) {
                throw new IOException("Your file contains " + raw_block_list.blockCount()
                        + " sectors, but the initial DIFAT array at index " + block_index
                        + " referenced block # " + nextOffset + ". This isn't allowed and "
                        + " your file is corrupt");
            }

            blocks[block_index] = (RawDataBlock) raw_block_list.remove(nextOffset);
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
                limit = Math.min(block_count - block_index, max_entries_per_block);
                byte[] data = raw_block_list.remove(chain_index).getData();
                int offset = 0;

                for (int k = 0; k < limit; k++) {
                    blocks[block_index++] = (RawDataBlock) raw_block_list.remove(LittleEndian.getInt(data, offset));
                    offset += LittleEndian.INT_SIZE;
                }
                chain_index = LittleEndian.getInt(data, chain_index_offset);
                if (chain_index == CFBConstants.END_OF_CHAIN) {
                    break;
                }
            }
        }
        if (block_index != block_count) {
            throw new IOException("Could not find all blocks");
        }



        setEntries(blocks, raw_block_list);
    }


    public BlockAllocationTableReader(BlockSize bigBlockSize, RawDataBlock[] blocks,
                                      BlockList raw_block_list) throws IOException {
        this.bigBlockSize = bigBlockSize;
        _entries = new IntList();
        setEntries(blocks, raw_block_list);
    }


    public RawDataBlock[] fetchBlocks(int startBlock, int headerPropertiesStartBlock,
                                      BlockList blockList) throws IOException {
        List<RawDataBlock> blocks = new ArrayList<RawDataBlock>();
        int currentBlock = startBlock;
        boolean firstPass = true;
        RawDataBlock dataBlock = null;





        while (currentBlock != CFBConstants.END_OF_CHAIN) {
            try {

                dataBlock = blockList.remove(currentBlock);
                blocks.add(dataBlock);

                currentBlock = _entries.get(currentBlock);
                firstPass = false;
            } catch (IOException e) {
                if (currentBlock == headerPropertiesStartBlock) {
                    currentBlock = CFBConstants.END_OF_CHAIN;
                } else if (currentBlock == 0 && firstPass) {
                    currentBlock = CFBConstants.END_OF_CHAIN;
                } else {

                    throw e;
                }
            }
        }

        return blocks.toArray(new RawDataBlock[blocks.size()]);
    }


    private void setEntries(RawDataBlock[] blocks, BlockList raw_blocks) throws IOException {
        int limit = bigBlockSize.getBATEntriesPerBlock();

        for (int block_index = 0; block_index < blocks.length; block_index++) {
            byte[] data = blocks[block_index].getData();
            int offset = 0;
            for (int k = 0; k < limit; k++) {
                int entry = LittleEndian.getInt(data, offset);

                if (entry == CFBConstants.UNUSED_BLOCK) {
                    raw_blocks.zap(_entries.size());
                }
                _entries.add(entry);
                offset += LittleEndian.INT_SIZE;
            }


            blocks[block_index] = null;
        }
        raw_blocks.setBAT(this);
    }
}
