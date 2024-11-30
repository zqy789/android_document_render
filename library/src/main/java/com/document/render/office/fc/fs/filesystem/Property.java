package com.document.render.office.fc.fs.filesystem;

import com.document.render.office.fc.fs.storage.LittleEndian;
import com.document.render.office.fc.fs.storage.RawDataBlock;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class Property {
    public static final int PROPERTY_TYPE_OFFSET = 0x42;

    public static final byte DIRECTORY_TYPE = 1;
    public static final byte DOCUMENT_TYPE = 2;
    public static final byte ROOT_TYPE = 5;

    protected static final int _NO_INDEX = -1;

    static final protected byte _NODE_BLACK = 1;
    static final protected byte _NODE_RED = 0;
    private static final int NAME_SIZE_OFFSET = 0x40;

    private static final int PREVIOUS_PROPERTY_OFFSET = 0x44;

    private static final int NEXT_PROPERTY_OFFSET = 0x48;

    private static final int CHILD_PROPERTY_OFFSET = 0x4c;
    private static final int START_BLOCK_OFFSET = 0x74;
    private static final int SIZE_OFFSET = 0x78;

    private static int _big_block_minimum_bytes = CFBConstants.BIG_BLOCK_MINIMUM_DOCUMENT_SIZE;

    protected Map<String, Property> properties = new HashMap<String, Property>();
    private String _name;
    private short _name_size;
    private byte _property_type;
    private int _start_block;
    private int _size;
    private int _chlid_property;
    private int _next_property;
    private int _previous_property;

    private byte[] documentRawData;

    private RawDataBlock[] blocks;

    private int blockSize;


    public Property(int index, byte[] array, int offset) {



        _name_size = LittleEndian.getShort(array, NAME_SIZE_OFFSET + offset);

        _previous_property = LittleEndian.getShort(array, PREVIOUS_PROPERTY_OFFSET + offset);

        _next_property = LittleEndian.getShort(array, NEXT_PROPERTY_OFFSET + offset);

        _chlid_property = LittleEndian.getShort(array, CHILD_PROPERTY_OFFSET + offset);

        _start_block = LittleEndian.getInt(array, START_BLOCK_OFFSET + offset);

        _size = LittleEndian.getInt(array, SIZE_OFFSET + offset);

        _property_type = array[PROPERTY_TYPE_OFFSET + offset];
        int name_length = (_name_size / LittleEndian.SHORT_SIZE) - 1;

        if (name_length < 1) {
            if (_property_type == ROOT_TYPE) {
                _name = "Root Entry";
            } else {
                _name = "aaa";
            }
        } else {
            char[] char_array = new char[name_length];
            int name_offset = 0;

            for (int j = 0; j < name_length; j++) {
                char_array[j] = (char) LittleEndian.getShort(array, name_offset + offset);
                name_offset += LittleEndian.SHORT_SIZE;
            }
            _name = new String(char_array, 0, name_length);
        }
    }


    public byte[] getDocumentRawData() {
        return this.documentRawData;
    }


    public void setDocumentRawData(byte[] rawData) {
        documentRawData = rawData;
    }


    public int getStartBlock() {
        return _start_block;
    }


    public int getSize() {
        return _size;
    }


    public int getPreviousPropertyIndex() {
        return _previous_property;
    }


    public int getNextPropertyIndex() {
        return _next_property;
    }


    public int getChildPropertyIndex() {
        return _chlid_property;
    }


    public boolean shouldUseSmallBlocks() {
        return getSize() < _big_block_minimum_bytes;
    }


    public String getName() {
        return _name;
    }


    public long getPropertyRawDataSize() {
        if (blocks != null) {
            return blocks[0].getData().length * blocks.length;
        }
        return documentRawData.length;
    }


    public RawDataBlock[] getBlocks() {
        return blocks;
    }


    public void setBlocks(RawDataBlock[] blocks) {
        this.blocks = blocks;
        blockSize = blocks[0].getData().length;
    }


    public boolean isDocument() {
        return _property_type == DOCUMENT_TYPE;
    }


    public boolean isDirectory() {
        return _property_type == DIRECTORY_TYPE;
    }


    public boolean isRoot() {
        return _property_type == ROOT_TYPE;
    }


    public int getUShort(int offset) {
        int b0 = getByteForOffset(offset);
        int b1 = getByteForOffset(offset + 1);

        return (b1 << 8) + (b0 << 0);
    }

    public long getUInt(int offset) {
        long retNum = getInt(offset);
        return retNum & 0x00FFFFFFFFl;
    }


    public int getInt(int offset) {
        int b0 = getByteForOffset(offset);
        int b1 = getByteForOffset(offset + 1);
        int b2 = getByteForOffset(offset + 2);
        int b3 = getByteForOffset(offset + 3);


        return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
    }


    public long getLong(int offset) {
        long result = 0;
        for (int j = offset + 8 - 1; j >= offset; j--) {
            result <<= 8;
            result |= 0xff & getByteForOffset(j);
        }
        return result;
    }


    public void writeByte(OutputStream out, int offset, int len) throws IOException {

        final int BLOCKNUMBER = 16;

        int length = Math.min(len, blockSize * BLOCKNUMBER);
        byte[] data = new byte[length];
        int index = getBlockIndexForOffset(offset);


        int off = offset - blockSize * index;
        int writeLen = Math.min(len, blockSize - off);
        System.arraycopy(blocks[index].getData(), off, data, 0, writeLen);
        int blockCnt = 1;
        while (writeLen <= len && index < blocks.length) {
            if (blockCnt < BLOCKNUMBER) {
                index++;
                blockCnt++;
                if (writeLen + blockSize > len) {

                    if (len > writeLen && index < blocks.length) {
                        System.arraycopy(blocks[index].getData(), 0, data, writeLen, len - writeLen);
                    }
                    out.write(data, 0, len);
                    break;
                }
                System.arraycopy(blocks[index].getData(), 0, data, writeLen, blockSize);
                writeLen += blockSize;
            } else {

                out.write(data, 0, writeLen);


                len -= writeLen;
                blockCnt = 0;
                writeLen = 0;
            }
        }

        data = null;
    }


    private int getBlockIndexForOffset(int offset) {
        return offset / blockSize;
    }


    private int getByteForOffset(int offset) {
        int index = offset / blockSize;
        int off = offset - blockSize * index;
        return blocks[index].getData()[off] & 0xFF;
    }

    public byte[] getRecordData(int usrOffset) {

        int rlen = (int) getUInt(usrOffset + 4) + 8;


        if (rlen < 0) {
            rlen = 0;
        }

        if (documentRawData == null || documentRawData.length < rlen) {
            documentRawData = new byte[Math.max(rlen, blockSize)];
        }


        int startIndex = usrOffset / blockSize;
        int endIndex = (usrOffset + rlen) / blockSize;

        if (endIndex > startIndex) {

            int off = usrOffset % blockSize;

            System.arraycopy(blocks[startIndex].getData(), off, documentRawData, 0, blockSize - off);

            off = blockSize - off;

            if (startIndex + 1 < endIndex) {
                for (int i = startIndex + 1; i < endIndex; i++) {
                    System.arraycopy(blocks[i].getData(), 0, documentRawData, off, blockSize);
                    off = off + blockSize;
                }
            }


            if (endIndex < blocks.length) {
                System.arraycopy(blocks[endIndex].getData(), 0, documentRawData, off, (usrOffset + rlen) % blockSize);
            }
        } else {

            int off = usrOffset % blockSize;
            System.arraycopy(blocks[startIndex].getData(), off, documentRawData, 0, rlen);
        }
        return documentRawData;
    }


    public void addChildProperty(Property property) {
        properties.put(property.getName(), property);
    }


    public Property getChlidProperty(String name) {
        return properties.get(name);
    }


    public void dispose() {

        documentRawData = null;
        _name = null;
        blocks = null;
        if (properties != null) {
            Set<String> set = properties.keySet();
            for (String key : set) {
                properties.get(key).dispose();
            }
            properties.clear();
            properties = null;
        }
    }
}
