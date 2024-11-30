

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public final class SmallDocumentBlock implements BlockWritable, ListManagedBlock {
    private static final int BLOCK_SHIFT = 6;
    private static final byte _default_fill = (byte) 0xff;
    private static final int _block_size = 1 << BLOCK_SHIFT;
    private static final int BLOCK_MASK = _block_size - 1;
    private final int _blocks_per_big_block;
    private final POIFSBigBlockSize _bigBlockSize;
    private byte[] _data;

    private SmallDocumentBlock(final POIFSBigBlockSize bigBlockSize, final byte[] data, final int index) {
        this(bigBlockSize);
        System.arraycopy(data, index * _block_size, _data, 0, _block_size);
    }

    private SmallDocumentBlock(final POIFSBigBlockSize bigBlockSize) {
        _bigBlockSize = bigBlockSize;
        _blocks_per_big_block = getBlocksPerBigBlock(bigBlockSize);
        _data = new byte[_block_size];
    }

    private static int getBlocksPerBigBlock(final POIFSBigBlockSize bigBlockSize) {
        return bigBlockSize.getBigBlockSize() / _block_size;
    }


    public static SmallDocumentBlock[] convert(POIFSBigBlockSize bigBlockSize,
                                               byte[] array,
                                               int size) {
        SmallDocumentBlock[] rval =
                new SmallDocumentBlock[(size + _block_size - 1) / _block_size];
        int offset = 0;

        for (int k = 0; k < rval.length; k++) {
            rval[k] = new SmallDocumentBlock(bigBlockSize);
            if (offset < array.length) {
                int length = Math.min(_block_size, array.length - offset);

                System.arraycopy(array, offset, rval[k]._data, 0, length);
                if (length != _block_size) {
                    Arrays.fill(rval[k]._data, length, _block_size,
                            _default_fill);
                }
            } else {
                Arrays.fill(rval[k]._data, _default_fill);
            }
            offset += _block_size;
        }
        return rval;
    }


    public static int fill(POIFSBigBlockSize bigBlockSize, List blocks) {
        int _blocks_per_big_block = getBlocksPerBigBlock(bigBlockSize);

        int count = blocks.size();
        int big_block_count = (count + _blocks_per_big_block - 1)
                / _blocks_per_big_block;
        int full_count = big_block_count * _blocks_per_big_block;

        for (; count < full_count; count++) {
            blocks.add(makeEmptySmallDocumentBlock(bigBlockSize));
        }
        return big_block_count;
    }


    public static SmallDocumentBlock[] convert(POIFSBigBlockSize bigBlockSize,
                                               BlockWritable[] store,
                                               int size)
            throws IOException, ArrayIndexOutOfBoundsException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        for (int j = 0; j < store.length; j++) {
            store[j].writeBlocks(stream);
        }
        byte[] data = stream.toByteArray();
        SmallDocumentBlock[] rval =
                new SmallDocumentBlock[convertToBlockCount(size)];

        for (int index = 0; index < rval.length; index++) {
            rval[index] = new SmallDocumentBlock(bigBlockSize, data, index);
        }
        return rval;
    }


    public static List extract(POIFSBigBlockSize bigBlockSize, ListManagedBlock[] blocks)
            throws IOException {
        int _blocks_per_big_block = getBlocksPerBigBlock(bigBlockSize);

        List sdbs = new ArrayList();

        for (int j = 0; j < blocks.length; j++) {
            byte[] data = blocks[j].getData();

            for (int k = 0; k < _blocks_per_big_block; k++) {
                sdbs.add(new SmallDocumentBlock(bigBlockSize, data, k));
            }
        }
        return sdbs;
    }

    public static DataInputBlock getDataInputBlock(SmallDocumentBlock[] blocks, int offset) {
        int firstBlockIndex = offset >> BLOCK_SHIFT;
        int firstBlockOffset = offset & BLOCK_MASK;
        return new DataInputBlock(blocks[firstBlockIndex]._data, firstBlockOffset);
    }


    public static int calcSize(int size) {
        return size * _block_size;
    }

    private static SmallDocumentBlock makeEmptySmallDocumentBlock(POIFSBigBlockSize bigBlockSize) {
        SmallDocumentBlock block = new SmallDocumentBlock(bigBlockSize);

        Arrays.fill(block._data, _default_fill);
        return block;
    }

    private static int convertToBlockCount(int size) {
        return (size + _block_size - 1) / _block_size;
    }


    public void writeBlocks(OutputStream stream)
            throws IOException {
        stream.write(_data);
    }


    public byte[] getData() {
        return _data;
    }

    public POIFSBigBlockSize getBigBlockSize() {
        return _bigBlockSize;
    }
}
