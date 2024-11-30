

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;



public final class BATBlock extends BigBlock {

    private int[] _values;


    private boolean _has_free_sectors;


    private int ourBlockIndex;


    private BATBlock(POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);

        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        _values = new int[_entries_per_block];
        _has_free_sectors = true;

        Arrays.fill(_values, POIFSConstants.UNUSED_BLOCK);
    }



    private BATBlock(POIFSBigBlockSize bigBlockSize, final int[] entries,
                     final int start_index, final int end_index) {
        this(bigBlockSize);
        for (int k = start_index; k < end_index; k++) {
            _values[k - start_index] = entries[k];
        }


        if (end_index - start_index == _values.length) {
            recomputeFree();
        }
    }


    public static BATBlock createBATBlock(final POIFSBigBlockSize bigBlockSize, ByteBuffer data) {

        BATBlock block = new BATBlock(bigBlockSize);


        byte[] buffer = new byte[LittleEndian.INT_SIZE];
        for (int i = 0; i < block._values.length; i++) {
            data.get(buffer);
            block._values[i] = LittleEndian.getInt(buffer);
        }
        block.recomputeFree();


        return block;
    }


    public static BATBlock createEmptyBATBlock(final POIFSBigBlockSize bigBlockSize, boolean isXBAT) {
        BATBlock block = new BATBlock(bigBlockSize);
        if (isXBAT) {
            block.setXBATChain(bigBlockSize, POIFSConstants.END_OF_CHAIN);
        }
        return block;
    }


    public static BATBlock[] createBATBlocks(final POIFSBigBlockSize bigBlockSize, final int[] entries) {
        int block_count = calculateStorageRequirements(bigBlockSize, entries.length);
        BATBlock[] blocks = new BATBlock[block_count];
        int index = 0;
        int remaining = entries.length;

        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        for (int j = 0; j < entries.length; j += _entries_per_block) {
            blocks[index++] = new BATBlock(bigBlockSize, entries, j,
                    (remaining > _entries_per_block)
                            ? j + _entries_per_block
                            : entries.length);
            remaining -= _entries_per_block;
        }
        return blocks;
    }



    public static BATBlock[] createXBATBlocks(final POIFSBigBlockSize bigBlockSize,
                                              final int[] entries,
                                              final int startBlock) {
        int block_count =
                calculateXBATStorageRequirements(bigBlockSize, entries.length);
        BATBlock[] blocks = new BATBlock[block_count];
        int index = 0;
        int remaining = entries.length;

        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        if (block_count != 0) {
            for (int j = 0; j < entries.length; j += _entries_per_xbat_block) {
                blocks[index++] =
                        new BATBlock(bigBlockSize, entries, j,
                                (remaining > _entries_per_xbat_block)
                                        ? j + _entries_per_xbat_block
                                        : entries.length);
                remaining -= _entries_per_xbat_block;
            }
            for (index = 0; index < blocks.length - 1; index++) {
                blocks[index].setXBATChain(bigBlockSize, startBlock + index + 1);
            }
            blocks[index].setXBATChain(bigBlockSize, POIFSConstants.END_OF_CHAIN);
        }
        return blocks;
    }


    public static int calculateStorageRequirements(final POIFSBigBlockSize bigBlockSize, final int entryCount) {
        int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        return (entryCount + _entries_per_block - 1) / _entries_per_block;
    }


    public static int calculateXBATStorageRequirements(final POIFSBigBlockSize bigBlockSize, final int entryCount) {
        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        return (entryCount + _entries_per_xbat_block - 1)
                / _entries_per_xbat_block;
    }


    public static int calculateMaximumSize(final POIFSBigBlockSize bigBlockSize,
                                           final int numBATs) {
        int size = 1;




        size += (numBATs * bigBlockSize.getBATEntriesPerBlock());


        return size * bigBlockSize.getBigBlockSize();
    }

    public static int calculateMaximumSize(final HeaderBlock header) {
        return calculateMaximumSize(header.getBigBlockSize(), header.getBATCount());
    }


    public static BATBlockAndIndex getBATBlockAndIndex(final int offset,
                                                       final HeaderBlock header, final List<BATBlock> bats) {
        POIFSBigBlockSize bigBlockSize = header.getBigBlockSize();

        int whichBAT = (int) Math.floor(offset / bigBlockSize.getBATEntriesPerBlock());
        int index = offset % bigBlockSize.getBATEntriesPerBlock();
        return new BATBlockAndIndex(index, bats.get(whichBAT));
    }


    public static BATBlockAndIndex getSBATBlockAndIndex(final int offset,
                                                        final HeaderBlock header, final List<BATBlock> sbats) {
        POIFSBigBlockSize bigBlockSize = header.getBigBlockSize();


        int whichSBAT = (int) Math.floor(offset / bigBlockSize.getBATEntriesPerBlock());
        int index = offset % bigBlockSize.getBATEntriesPerBlock();
        return new BATBlockAndIndex(index, sbats.get(whichSBAT));
    }

    private void recomputeFree() {
        boolean hasFree = false;
        for (int k = 0; k < _values.length; k++) {
            if (_values[k] == POIFSConstants.UNUSED_BLOCK) {
                hasFree = true;
                break;
            }
        }
        _has_free_sectors = hasFree;
    }

    private void setXBATChain(final POIFSBigBlockSize bigBlockSize, int chainIndex) {
        int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        _values[_entries_per_xbat_block] = chainIndex;
    }


    public boolean hasFreeSectors() {
        return _has_free_sectors;
    }

    public int getValueAt(int relativeOffset) {
        if (relativeOffset >= _values.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "Unable to fetch offset " + relativeOffset + " as the " +
                            "BAT only contains " + _values.length + " entries"
            );
        }
        return _values[relativeOffset];
    }

    public void setValueAt(int relativeOffset, int value) {
        int oldValue = _values[relativeOffset];
        _values[relativeOffset] = value;


        if (value == POIFSConstants.UNUSED_BLOCK) {
            _has_free_sectors = true;
            return;
        }
        if (oldValue == POIFSConstants.UNUSED_BLOCK) {
            recomputeFree();
        }
    }


    public int getOurBlockIndex() {
        return ourBlockIndex;
    }


    public void setOurBlockIndex(int index) {
        this.ourBlockIndex = index;
    }





    void writeData(final OutputStream stream)
            throws IOException {

        stream.write(serialize());
    }

    void writeData(final ByteBuffer block)
            throws IOException {

        block.put(serialize());
    }

    private byte[] serialize() {

        byte[] data = new byte[bigBlockSize.getBigBlockSize()];


        int offset = 0;
        for (int i = 0; i < _values.length; i++) {
            LittleEndian.putInt(data, offset, _values[i]);
            offset += LittleEndian.INT_SIZE;
        }


        return data;
    }




    public static class BATBlockAndIndex {
        private final int index;
        private final BATBlock block;

        private BATBlockAndIndex(int index, BATBlock block) {
            this.index = index;
            this.block = block;
        }

        public int getIndex() {
            return index;
        }

        public BATBlock getBlock() {
            return block;
        }
    }
}

