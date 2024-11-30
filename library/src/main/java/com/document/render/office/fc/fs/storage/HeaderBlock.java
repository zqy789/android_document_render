package com.document.render.office.fc.fs.storage;

import com.document.render.office.fc.fs.filesystem.BlockSize;
import com.document.render.office.fc.fs.filesystem.CFBConstants;
import com.document.render.office.fc.util.HexDump;

import java.io.IOException;
import java.io.InputStream;


public final class HeaderBlock {

    public static final long _signature = 0xE11AB1A1E011CFD0L;
    public static final int _bat_array_offset = 0x4c;
    public static final int _max_bats_in_header = (CFBConstants.SMALLER_BIG_BLOCK_SIZE - _bat_array_offset) / 4;


    public static final int _signature_offset = 0;
    public static final int _bat_count_offset = 0x2C;
    public static final int _property_start_offset = 0x30;
    public static final int _sbat_start_offset = 0x3C;
    public static final int _sbat_block_count_offset = 0x40;
    public static final int _xbat_start_offset = 0x44;
    public static final int _xbat_count_offset = 0x48;


    private BlockSize bigBlockSize;


    private int _bat_count;


    private int _property_start;


    private int _sbat_start;

    private int _sbat_count;


    private int _xbat_start;

    private int _xbat_count;


    private byte[] _data;


    public HeaderBlock(InputStream stream) throws IOException {

        _data = new byte[512];
        stream.read(_data);

        long signature = LittleEndian.getLong(_data, _signature_offset);

        if (signature != _signature) {




            throw new IOException("Invalid header signature; read " + longToHex(signature)
                    + ", expected " + longToHex(_signature));
        }

        if (_data[30] == 12) {
            this.bigBlockSize = CFBConstants.LARGER_BIG_BLOCK_SIZE_DETAILS;
        } else if (_data[30] == 9) {
            this.bigBlockSize = CFBConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
        } else {
            throw new IOException("Unsupported blocksize  (2^" + _data[30]
                    + "). Expected 2^9 or 2^12.");
        }


        _bat_count = LittleEndian.getInt(_data, _bat_count_offset);
        _property_start = LittleEndian.getInt(_data, _property_start_offset);
        _sbat_start = LittleEndian.getInt(_data, _sbat_start_offset);
        _sbat_count = LittleEndian.getInt(_data, _sbat_block_count_offset);
        _xbat_start = LittleEndian.getInt(_data, _xbat_start_offset);
        _xbat_count = LittleEndian.getInt(_data, _xbat_count_offset);
    }


    private String longToHex(long value) {
        return new String(HexDump.longToHex(value));
    }


    public int getPropertyStart() {
        return _property_start;
    }


    public void setPropertyStart(final int startBlock) {
        _property_start = startBlock;
    }


    public int getSBATStart() {
        return _sbat_start;
    }


    public void setSBATStart(final int startBlock) {
        _sbat_start = startBlock;
    }

    public int getSBATCount() {
        return _sbat_count;
    }


    public void setSBATBlockCount(final int count) {
        _sbat_count = count;
    }


    public int getBATCount() {
        return _bat_count;
    }


    public void setBATCount(final int count) {
        _bat_count = count;
    }


    public int[] getBATArray() {

        int[] result = new int[Math.min(_bat_count, _max_bats_in_header)];
        int offset = _bat_array_offset;
        for (int j = 0; j < result.length; j++) {
            result[j] = LittleEndian.getInt(_data, offset);
            offset += LittleEndian.INT_SIZE;
        }
        return result;
    }


    public int getXBATCount() {
        return _xbat_count;
    }


    public int getXBATIndex() {
        return _xbat_start;
    }


    public BlockSize getBigBlockSize() {
        return bigBlockSize;
    }


    public void dispose() {
        _data = null;
        bigBlockSize = null;
    }
}
