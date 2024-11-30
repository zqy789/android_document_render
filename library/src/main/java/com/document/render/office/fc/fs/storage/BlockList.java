package com.document.render.office.fc.fs.storage;

import com.document.render.office.fc.fs.filesystem.BlockSize;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class BlockList {


    private RawDataBlock[] _blocks;

    private BlockAllocationTableReader _bat;


    public BlockList(InputStream stream, BlockSize bigBlockSize) throws IOException {
        List<RawDataBlock> blocks = new ArrayList<RawDataBlock>();
        int size = bigBlockSize.getBigBlockSize();
        while (true) {
            byte[] b = new byte[size];
            int count = stream.read(b);
            if (count <= 0) {
                break;
            }
            RawDataBlock block = new RawDataBlock(b);
            blocks.add(block);
            if (count != size) {
                break;
            }
        }
        _blocks = blocks.toArray(new RawDataBlock[blocks.size()]);
    }


    public BlockList(RawDataBlock[] _blocks) {
        this._blocks = _blocks;
    }


    public void zap(final int index) {
        if ((index >= 0) && (index < _blocks.length)) {
            _blocks[index] = null;
        }
    }


    protected RawDataBlock get(final int index) {
        return _blocks[index];
    }


    public RawDataBlock remove(int index) throws IOException {
        if (index < 0 || index >= _blocks.length) {
            return null;
        }
        RawDataBlock result = _blocks[index];
        _blocks[index] = null;
        return result;
    }


    public RawDataBlock[] fetchBlocks(int startBlock, int headerPropertiesStartBlock)
            throws IOException {
        if (_bat == null) {
            throw new IOException("Improperly initialized list: no block allocation table provided");
        }
        return _bat.fetchBlocks(startBlock, headerPropertiesStartBlock, this);
    }


    public void setBAT(BlockAllocationTableReader bat) throws IOException {
        _bat = bat;
    }


    public int blockCount() {
        return _blocks.length;
    }
}
