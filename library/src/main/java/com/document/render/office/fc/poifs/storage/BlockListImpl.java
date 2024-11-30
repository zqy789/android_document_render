

package com.document.render.office.fc.poifs.storage;

import java.io.IOException;


abstract class BlockListImpl implements BlockList {
    private ListManagedBlock[] _blocks;
    private BlockAllocationTableReader _bat;

    protected BlockListImpl() {
        _blocks = new ListManagedBlock[0];
        _bat = null;
    }


    protected void setBlocks(final ListManagedBlock[] blocks) {
        _blocks = blocks;
    }


    public void zap(final int index) {
        if ((index >= 0) && (index < _blocks.length)) {
            _blocks[index] = null;
        }
    }


    protected ListManagedBlock get(final int index) {
        return _blocks[index];
    }


    public ListManagedBlock remove(final int index) throws IOException {
        ListManagedBlock result = null;

        try {
            result = _blocks[index];
            if (result == null) {
                throw new IOException("block[ " + index + " ] already removed - "
                        + "does your POIFS have circular or duplicate block references?");
            }
            _blocks[index] = null;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            throw new IOException("Cannot remove block[ " + index + " ]; out of range[ 0 - "
                    + (_blocks.length - 1) + " ]");
        }
        return result;
    }


    public ListManagedBlock[] fetchBlocks(final int startBlock, final int headerPropertiesStartBlock)
            throws IOException {
        if (_bat == null) {
            throw new IOException("Improperly initialized list: no block allocation table provided");
        }
        return _bat.fetchBlocks(startBlock, headerPropertiesStartBlock, this);
    }


    public void setBAT(final BlockAllocationTableReader bat) throws IOException {
        if (_bat != null) {
            throw new IOException("Attempt to replace existing BlockAllocationTable");
        }
        _bat = bat;
    }


    public int blockCount() {
        return _blocks.length;
    }


    protected int remainingBlocks() {
        int c = 0;
        for (int i = 0; i < _blocks.length; i++) {
            if (_blocks[i] != null)
                c++;
        }
        return c;
    }
}
