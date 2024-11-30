

package com.document.render.office.fc.ddf;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;


public class EscherBlipRecord extends EscherRecord {
    public static final short RECORD_ID_START = (short) 0xF018;
    public static final short RECORD_ID_END = (short) 0xF117;
    public static final String RECORD_DESCRIPTION = "msofbtBlip";

    private static final int HEADER_SIZE = 8;

    protected byte[] field_pictureData;

    private String tempFilePath;

    public EscherBlipRecord() {
    }

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesAfterHeader = readHeader(data, offset);
        int pos = offset + HEADER_SIZE;

        field_pictureData = new byte[bytesAfterHeader];
        System.arraycopy(data, pos, field_pictureData, 0, bytesAfterHeader);

        return bytesAfterHeader + 8;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);

        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());

        System.arraycopy(field_pictureData, 0, data, offset + 4, field_pictureData.length);

        listener.afterRecordSerialize(offset + 4 + field_pictureData.length, getRecordId(), field_pictureData.length + 4, this);
        return field_pictureData.length + 4;
    }

    public int getRecordSize() {
        return field_pictureData.length + HEADER_SIZE;
    }

    public String getRecordName() {
        return "Blip";
    }

    public byte[] getPicturedata() {
        return field_pictureData;
    }

    public void setPictureData(byte[] pictureData) {
        field_pictureData = pictureData;
    }

    public String toString() {
        String extraData = HexDump.toHex(field_pictureData, 32);
        return getClass().getName() + ":" + '\n' +
                "  RecordId: 0x" + HexDump.toHex(getRecordId()) + '\n' +
                "  Options: 0x" + HexDump.toHex(getOptions()) + '\n' +
                "  Extra Data:" + '\n' + extraData;
    }


    public void dispose() {
        field_pictureData = null;
    }


    public String getTempFilePath() {
        return tempFilePath;
    }


    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }
}
