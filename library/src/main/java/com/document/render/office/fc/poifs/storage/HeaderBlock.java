

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.IOUtils;
import com.document.render.office.fc.util.IntegerField;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianConsts;
import com.document.render.office.fc.util.LongField;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;
import com.document.render.office.fc.util.ShortField;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;



public final class HeaderBlock implements HeaderBlockConstants {
    private static final POILogger _logger = POILogFactory.getLogger(HeaderBlock.class);
    private static final byte _default_value = (byte) 0xFF;

    private final POIFSBigBlockSize bigBlockSize;

    private final byte[] _data;

    private int _bat_count;

    private int _property_start;

    private int _sbat_start;

    private int _sbat_count;

    private int _xbat_start;

    private int _xbat_count;


    public HeaderBlock(InputStream stream) throws IOException {



        this(readFirst512(stream));


        if (bigBlockSize.getBigBlockSize() != 512) {
            int rest = bigBlockSize.getBigBlockSize() - 512;
            byte[] tmp = new byte[rest];
            IOUtils.readFully(stream, tmp);
        }
    }

    public HeaderBlock(ByteBuffer buffer) throws IOException {
        this(IOUtils.toByteArray(buffer, POIFSConstants.SMALLER_BIG_BLOCK_SIZE));
    }

    private HeaderBlock(byte[] data) throws IOException {
        this._data = data;


        long signature = LittleEndian.getLong(_data, _signature_offset);

        if (signature != _signature) {




            throw new IOException("Invalid header signature; read " + longToHex(signature)
                    + ", expected " + longToHex(_signature));
        }


        if (_data[30] == 12) {
            this.bigBlockSize = POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS;
        } else if (_data[30] == 9) {
            this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
        } else {
            throw new IOException("Unsupported blocksize  (2^" + _data[30]
                    + "). Expected 2^9 or 2^12.");
        }


        _bat_count = new IntegerField(_bat_count_offset, data).get();
        _property_start = new IntegerField(_property_start_offset, _data).get();
        _sbat_start = new IntegerField(_sbat_start_offset, _data).get();
        _sbat_count = new IntegerField(_sbat_block_count_offset, _data).get();
        _xbat_start = new IntegerField(_xbat_start_offset, _data).get();
        _xbat_count = new IntegerField(_xbat_count_offset, _data).get();
    }


    public HeaderBlock(POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;


        _data = new byte[POIFSConstants.SMALLER_BIG_BLOCK_SIZE];
        Arrays.fill(_data, _default_value);


        new LongField(_signature_offset, _signature, _data);
        new IntegerField(0x08, 0, _data);
        new IntegerField(0x0c, 0, _data);
        new IntegerField(0x10, 0, _data);
        new IntegerField(0x14, 0, _data);
        new ShortField(0x18, (short) 0x3b, _data);
        new ShortField(0x1a, (short) 0x3, _data);
        new ShortField(0x1c, (short) -2, _data);

        new ShortField(0x1e, bigBlockSize.getHeaderValue(), _data);
        new IntegerField(0x20, 0x6, _data);
        new IntegerField(0x24, 0, _data);
        new IntegerField(0x28, 0, _data);
        new IntegerField(0x34, 0, _data);
        new IntegerField(0x38, 0x1000, _data);


        _bat_count = 0;
        _sbat_count = 0;
        _xbat_count = 0;
        _property_start = POIFSConstants.END_OF_CHAIN;
        _sbat_start = POIFSConstants.END_OF_CHAIN;
        _xbat_start = POIFSConstants.END_OF_CHAIN;
    }

    private static byte[] readFirst512(InputStream stream) throws IOException {


        byte[] data = new byte[512];
        int bsCount = IOUtils.readFully(stream, data);
        if (bsCount != 512) {
            throw alertShortRead(bsCount, 512);
        }
        return data;
    }

    private static String longToHex(long value) {
        return new String(HexDump.longToHex(value));
    }

    private static IOException alertShortRead(int pRead, int expectedReadSize) {
        int read;
        if (pRead < 0) {

            read = 0;
        } else {
            read = pRead;
        }
        String type = " byte" + (read == 1 ? ("") : ("s"));

        return new IOException("Unable to read entire header; " + read + type + " read; expected "
                + expectedReadSize + " bytes");
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
            offset += LittleEndianConsts.INT_SIZE;
        }
        return result;
    }


    public void setBATArray(int[] bat_array) {
        int count = Math.min(bat_array.length, _max_bats_in_header);
        int blank = _max_bats_in_header - count;

        int offset = _bat_array_offset;
        for (int i = 0; i < count; i++) {
            LittleEndian.putInt(_data, offset, bat_array[i]);
            offset += LittleEndianConsts.INT_SIZE;
        }
        for (int i = 0; i < blank; i++) {
            LittleEndian.putInt(_data, offset, POIFSConstants.UNUSED_BLOCK);
            offset += LittleEndianConsts.INT_SIZE;
        }
    }


    public int getXBATCount() {
        return _xbat_count;
    }


    public void setXBATCount(final int count) {
        _xbat_count = count;
    }


    public int getXBATIndex() {
        return _xbat_start;
    }


    public void setXBATStart(final int startBlock) {
        _xbat_start = startBlock;
    }


    public POIFSBigBlockSize getBigBlockSize() {
        return bigBlockSize;
    }


    void writeData(final OutputStream stream) throws IOException {

        new IntegerField(_bat_count_offset, _bat_count, _data);
        new IntegerField(_property_start_offset, _property_start, _data);
        new IntegerField(_sbat_start_offset, _sbat_start, _data);
        new IntegerField(_sbat_block_count_offset, _sbat_count, _data);
        new IntegerField(_xbat_start_offset, _xbat_start, _data);
        new IntegerField(_xbat_count_offset, _xbat_count, _data);


        stream.write(_data, 0, 512);


        for (int i = POIFSConstants.SMALLER_BIG_BLOCK_SIZE; i < bigBlockSize.getBigBlockSize(); i++) {
            stream.write(0);
        }
    }
}
