

package com.document.render.office.fc.ddf;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.RecordFormatException;


public class EscherSplitMenuColorsRecord
        extends EscherRecord {
    @Keep
    public static final short RECORD_ID = (short) 0xF11E;
    public static final String RECORD_DESCRIPTION = "MsofbtSplitMenuColors";

    private int field_1_color1;
    private int field_2_color2;
    private int field_3_color3;
    private int field_4_color4;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        int size = 0;
        field_1_color1 = LittleEndian.getInt(data, pos + size);
        size += 4;
        field_2_color2 = LittleEndian.getInt(data, pos + size);
        size += 4;
        field_3_color3 = LittleEndian.getInt(data, pos + size);
        size += 4;
        field_4_color4 = LittleEndian.getInt(data, pos + size);
        size += 4;
        bytesRemaining -= size;
        if (bytesRemaining != 0)
            throw new RecordFormatException("Expecting no remaining data but got " + bytesRemaining + " byte(s).");
        return 8 + size + bytesRemaining;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {

        listener.beforeRecordSerialize(offset, getRecordId(), this);

        int pos = offset;
        LittleEndian.putShort(data, pos, getOptions());
        pos += 2;
        LittleEndian.putShort(data, pos, getRecordId());
        pos += 2;
        int remainingBytes = getRecordSize() - 8;

        LittleEndian.putInt(data, pos, remainingBytes);
        pos += 4;
        LittleEndian.putInt(data, pos, field_1_color1);
        pos += 4;
        LittleEndian.putInt(data, pos, field_2_color2);
        pos += 4;
        LittleEndian.putInt(data, pos, field_3_color3);
        pos += 4;
        LittleEndian.putInt(data, pos, field_4_color4);
        pos += 4;
        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        return getRecordSize();
    }

    public int getRecordSize() {
        return 8 + 4 * 4;
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "SplitMenuColors";
    }


    public String toString() {
        return getClass().getName() + ":" + '\n' +
                "  RecordId: 0x" + HexDump.toHex(RECORD_ID) + '\n' +
                "  Options: 0x" + HexDump.toHex(getOptions()) + '\n' +
                "  Color1: 0x" + HexDump.toHex(field_1_color1) + '\n' +
                "  Color2: 0x" + HexDump.toHex(field_2_color2) + '\n' +
                "  Color3: 0x" + HexDump.toHex(field_3_color3) + '\n' +
                "  Color4: 0x" + HexDump.toHex(field_4_color4) + '\n' +
                "";
    }

    public int getColor1() {
        return field_1_color1;
    }

    public void setColor1(int field_1_color1) {
        this.field_1_color1 = field_1_color1;
    }

    public int getColor2() {
        return field_2_color2;
    }

    public void setColor2(int field_2_color2) {
        this.field_2_color2 = field_2_color2;
    }

    public int getColor3() {
        return field_3_color3;
    }

    public void setColor3(int field_3_color3) {
        this.field_3_color3 = field_3_color3;
    }

    public int getColor4() {
        return field_4_color4;
    }

    public void setColor4(int field_4_color4) {
        this.field_4_color4 = field_4_color4;
    }


    public void dispose() {
    }

}
