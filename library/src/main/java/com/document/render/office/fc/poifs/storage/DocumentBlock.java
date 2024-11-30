

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;



public final class DocumentBlock extends BigBlock {
    private static final byte _default_value = (byte) 0xFF;
    private byte[] _data;
    private int _bytes_read;



    public DocumentBlock(final RawDataBlock block)
            throws IOException {
        super(
                block.getBigBlockSize() == POIFSConstants.SMALLER_BIG_BLOCK_SIZE ?
                        POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS :
                        POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS
        );
        _data = block.getData();
        _bytes_read = _data.length;
    }



    public DocumentBlock(final InputStream stream, POIFSBigBlockSize bigBlockSize)
            throws IOException {
        this(bigBlockSize);
        int count = IOUtils.readFully(stream, _data);

        _bytes_read = (count == -1) ? 0
                : count;
    }



    private DocumentBlock(POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);
        _data = new byte[bigBlockSize.getBigBlockSize()];
        Arrays.fill(_data, _default_value);
    }



    public static byte getFillByte() {
        return _default_value;
    }



    public static DocumentBlock[] convert(final POIFSBigBlockSize bigBlockSize,
                                          final byte[] array,
                                          final int size) {
        DocumentBlock[] rval =
                new DocumentBlock[(size + bigBlockSize.getBigBlockSize() - 1) / bigBlockSize.getBigBlockSize()];
        int offset = 0;

        for (int k = 0; k < rval.length; k++) {
            rval[k] = new DocumentBlock(bigBlockSize);
            if (offset < array.length) {
                int length = Math.min(bigBlockSize.getBigBlockSize(),
                        array.length - offset);

                System.arraycopy(array, offset, rval[k]._data, 0, length);
                if (length != bigBlockSize.getBigBlockSize()) {
                    Arrays.fill(rval[k]._data, length,
                            bigBlockSize.getBigBlockSize(),
                            _default_value);
                }
            } else {
                Arrays.fill(rval[k]._data, _default_value);
            }
            offset += bigBlockSize.getBigBlockSize();
        }
        return rval;
    }

    public static DataInputBlock getDataInputBlock(DocumentBlock[] blocks, int offset) {
        if (blocks == null || blocks.length == 0) {
            return null;
        }


        POIFSBigBlockSize bigBlockSize = blocks[0].bigBlockSize;
        int BLOCK_SHIFT = bigBlockSize.getHeaderValue();
        int BLOCK_SIZE = bigBlockSize.getBigBlockSize();
        int BLOCK_MASK = BLOCK_SIZE - 1;


        int firstBlockIndex = offset >> BLOCK_SHIFT;
        int firstBlockOffset = offset & BLOCK_MASK;
        return new DataInputBlock(blocks[firstBlockIndex]._data, firstBlockOffset);
    }



    public int size() {
        return _bytes_read;
    }



    public boolean partiallyRead() {
        return _bytes_read != bigBlockSize.getBigBlockSize();
    }





    void writeData(final OutputStream stream)
            throws IOException {
        doWriteData(stream, _data);
    }


}

