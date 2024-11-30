

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FrameRecord extends StandardRecord {
    public final static short sid = 0x1032;
    public final static short BORDER_TYPE_REGULAR = 0;
    public final static short BORDER_TYPE_SHADOW = 1;
    private static final BitField autoSize = BitFieldFactory.getInstance(0x1);
    private static final BitField autoPosition = BitFieldFactory.getInstance(0x2);
    private short field_1_borderType;
    private short field_2_options;


    public FrameRecord() {

    }

    public FrameRecord(RecordInputStream in) {
        field_1_borderType = in.readShort();
        field_2_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[FRAME]\n");
        buffer.append("    .borderType           = ")
                .append("0x").append(HexDump.toHex(getBorderType()))
                .append(" (").append(getBorderType()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("    .options              = ")
                .append("0x").append(HexDump.toHex(getOptions()))
                .append(" (").append(getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .autoSize                 = ").append(isAutoSize()).append('\n');
        buffer.append("         .autoPosition             = ").append(isAutoPosition()).append('\n');

        buffer.append("[/FRAME]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_borderType);
        out.writeShort(field_2_options);
    }

    protected int getDataSize() {
        return 2 + 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        FrameRecord rec = new FrameRecord();

        rec.field_1_borderType = field_1_borderType;
        rec.field_2_options = field_2_options;
        return rec;
    }



    public short getBorderType() {
        return field_1_borderType;
    }


    public void setBorderType(short field_1_borderType) {
        this.field_1_borderType = field_1_borderType;
    }


    public short getOptions() {
        return field_2_options;
    }


    public void setOptions(short field_2_options) {
        this.field_2_options = field_2_options;
    }


    public boolean isAutoSize() {
        return autoSize.isSet(field_2_options);
    }


    public void setAutoSize(boolean value) {
        field_2_options = autoSize.setShortBoolean(field_2_options, value);
    }


    public boolean isAutoPosition() {
        return autoPosition.isSet(field_2_options);
    }


    public void setAutoPosition(boolean value) {
        field_2_options = autoPosition.setShortBoolean(field_2_options, value);
    }
}
