

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class CommonObjectDataSubRecord extends SubRecord {
    public final static short sid = 0x0015;
    public final static short OBJECT_TYPE_GROUP = 0;
    public final static short OBJECT_TYPE_LINE = 1;
    public final static short OBJECT_TYPE_RECTANGLE = 2;
    public final static short OBJECT_TYPE_OVAL = 3;
    public final static short OBJECT_TYPE_ARC = 4;
    public final static short OBJECT_TYPE_CHART = 5;
    public final static short OBJECT_TYPE_TEXT = 6;
    public final static short OBJECT_TYPE_BUTTON = 7;
    public final static short OBJECT_TYPE_PICTURE = 8;
    public final static short OBJECT_TYPE_POLYGON = 9;
    public final static short OBJECT_TYPE_RESERVED1 = 10;
    public final static short OBJECT_TYPE_CHECKBOX = 11;
    public final static short OBJECT_TYPE_OPTION_BUTTON = 12;
    public final static short OBJECT_TYPE_EDIT_BOX = 13;
    public final static short OBJECT_TYPE_LABEL = 14;
    public final static short OBJECT_TYPE_DIALOG_BOX = 15;
    public final static short OBJECT_TYPE_SPINNER = 16;
    public final static short OBJECT_TYPE_SCROLL_BAR = 17;
    public final static short OBJECT_TYPE_LIST_BOX = 18;
    public final static short OBJECT_TYPE_GROUP_BOX = 19;
    public final static short OBJECT_TYPE_COMBO_BOX = 20;
    public final static short OBJECT_TYPE_RESERVED2 = 21;
    public final static short OBJECT_TYPE_RESERVED3 = 22;
    public final static short OBJECT_TYPE_RESERVED4 = 23;
    public final static short OBJECT_TYPE_RESERVED5 = 24;
    public final static short OBJECT_TYPE_COMMENT = 25;
    public final static short OBJECT_TYPE_RESERVED6 = 26;
    public final static short OBJECT_TYPE_RESERVED7 = 27;
    public final static short OBJECT_TYPE_RESERVED8 = 28;
    public final static short OBJECT_TYPE_RESERVED9 = 29;
    public final static short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    private static final BitField locked = BitFieldFactory.getInstance(0x0001);
    private static final BitField printable = BitFieldFactory.getInstance(0x0010);
    private static final BitField autofill = BitFieldFactory.getInstance(0x2000);
    private static final BitField autoline = BitFieldFactory.getInstance(0x4000);
    private short field_1_objectType;
    private int field_2_objectId;
    private short field_3_option;
    private int field_4_reserved1;
    private int field_5_reserved2;
    private int field_6_reserved3;


    public CommonObjectDataSubRecord() {

    }

    public CommonObjectDataSubRecord(LittleEndianInput in, int size) {
        if (size != 18) {
            throw new RecordFormatException("Expected size 18 but got (" + size + ")");
        }
        field_1_objectType = in.readShort();
        field_2_objectId = in.readUShort();
        field_3_option = in.readShort();
        field_4_reserved1 = in.readInt();
        field_5_reserved2 = in.readInt();
        field_6_reserved3 = in.readInt();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[ftCmo]\n");
        buffer.append("    .objectType           = ")
                .append("0x").append(HexDump.toHex(getObjectType()))
                .append(" (").append(getObjectType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .objectId             = ")
                .append("0x").append(HexDump.toHex(getObjectId()))
                .append(" (").append(getObjectId()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .option               = ")
                .append("0x").append(HexDump.toHex(getOption()))
                .append(" (").append(getOption()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .locked                   = ").append(isLocked()).append('\n');
        buffer.append("         .printable                = ").append(isPrintable()).append('\n');
        buffer.append("         .autofill                 = ").append(isAutofill()).append('\n');
        buffer.append("         .autoline                 = ").append(isAutoline()).append('\n');
        buffer.append("    .reserved1            = ")
                .append("0x").append(HexDump.toHex(getReserved1()))
                .append(" (").append(getReserved1()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .reserved2            = ")
                .append("0x").append(HexDump.toHex(getReserved2()))
                .append(" (").append(getReserved2()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .reserved3            = ")
                .append("0x").append(HexDump.toHex(getReserved3()))
                .append(" (").append(getReserved3()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/ftCmo]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {

        out.writeShort(sid);
        out.writeShort(getDataSize());

        out.writeShort(field_1_objectType);
        out.writeShort(field_2_objectId);
        out.writeShort(field_3_option);
        out.writeInt(field_4_reserved1);
        out.writeInt(field_5_reserved2);
        out.writeInt(field_6_reserved3);
    }

    protected int getDataSize() {
        return 2 + 2 + 2 + 4 + 4 + 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        CommonObjectDataSubRecord rec = new CommonObjectDataSubRecord();

        rec.field_1_objectType = field_1_objectType;
        rec.field_2_objectId = field_2_objectId;
        rec.field_3_option = field_3_option;
        rec.field_4_reserved1 = field_4_reserved1;
        rec.field_5_reserved2 = field_5_reserved2;
        rec.field_6_reserved3 = field_6_reserved3;
        return rec;
    }



    public short getObjectType() {
        return field_1_objectType;
    }


    public void setObjectType(short field_1_objectType) {
        this.field_1_objectType = field_1_objectType;
    }


    public int getObjectId() {
        return field_2_objectId;
    }


    public void setObjectId(int field_2_objectId) {
        this.field_2_objectId = field_2_objectId;
    }


    public short getOption() {
        return field_3_option;
    }


    public void setOption(short field_3_option) {
        this.field_3_option = field_3_option;
    }


    public int getReserved1() {
        return field_4_reserved1;
    }


    public void setReserved1(int field_4_reserved1) {
        this.field_4_reserved1 = field_4_reserved1;
    }


    public int getReserved2() {
        return field_5_reserved2;
    }


    public void setReserved2(int field_5_reserved2) {
        this.field_5_reserved2 = field_5_reserved2;
    }


    public int getReserved3() {
        return field_6_reserved3;
    }


    public void setReserved3(int field_6_reserved3) {
        this.field_6_reserved3 = field_6_reserved3;
    }


    public boolean isLocked() {
        return locked.isSet(field_3_option);
    }


    public void setLocked(boolean value) {
        field_3_option = locked.setShortBoolean(field_3_option, value);
    }


    public boolean isPrintable() {
        return printable.isSet(field_3_option);
    }


    public void setPrintable(boolean value) {
        field_3_option = printable.setShortBoolean(field_3_option, value);
    }


    public boolean isAutofill() {
        return autofill.isSet(field_3_option);
    }


    public void setAutofill(boolean value) {
        field_3_option = autofill.setShortBoolean(field_3_option, value);
    }


    public boolean isAutoline() {
        return autoline.isSet(field_3_option);
    }


    public void setAutoline(boolean value) {
        field_3_option = autoline.setShortBoolean(field_3_option, value);
    }
}
