package com.document.render.office.fc.fs.filesystem;

import com.document.render.office.fc.fs.storage.LittleEndian;



public class BlockSize {
    private int bigBlockSize;
    private short headerValue;

    protected BlockSize(int bigBlockSize, short headerValue) {
        this.bigBlockSize = bigBlockSize;
        this.headerValue = headerValue;
    }

    public int getBigBlockSize() {
        return bigBlockSize;
    }


    public short getHeaderValue() {
        return headerValue;
    }

    public int getPropertiesPerBlock() {
        return bigBlockSize / CFBConstants.PROPERTY_SIZE;
    }

    public int getBATEntriesPerBlock() {
        return bigBlockSize / LittleEndian.INT_SIZE;
    }

    public int getXBATEntriesPerBlock() {
        return getBATEntriesPerBlock() - 1;
    }

    public int getNextXBATChainOffset() {
        return getXBATEntriesPerBlock() * LittleEndian.INT_SIZE;
    }
}
