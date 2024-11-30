

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class DatRecord extends StandardRecord {
    public final static short sid = 0x1063;

    private static final BitField horizontalBorder = BitFieldFactory.getInstance(0x1);
    private static final BitField verticalBorder = BitFieldFactory.getInstance(0x2);
    private static final BitField border = BitFieldFactory.getInstance(0x4);
    private static final BitField showSeriesKey = BitFieldFactory.getInstance(0x8);

    private short field_1_options;


    public DatRecord() {

    }

    public DatRecord(RecordInputStream in) {
        field_1_options = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DAT]\n");
        buffer.append("    .options              = ")
                .append("0x").append(HexDump.toHex(getOptions()))
                .append(" (").append(getOptions()).append(" )");
        buffer.append(System.getProperty("line.separator"));
        buffer.append("         .horizontalBorder         = ").append(isHorizontalBorder()).append('\n');
        buffer.append("         .verticalBorder           = ").append(isVerticalBorder()).append('\n');
        buffer.append("         .border                   = ").append(isBorder()).append('\n');
        buffer.append("         .showSeriesKey            = ").append(isShowSeriesKey()).append('\n');

        buffer.append("[/DAT]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_options);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        DatRecord rec = new DatRecord();

        rec.field_1_options = field_1_options;
        return rec;
    }



    public short getOptions() {
        return field_1_options;
    }


    public void setOptions(short field_1_options) {
        this.field_1_options = field_1_options;
    }


    public boolean isHorizontalBorder() {
        return horizontalBorder.isSet(field_1_options);
    }


    public void setHorizontalBorder(boolean value) {
        field_1_options = horizontalBorder.setShortBoolean(field_1_options, value);
    }


    public boolean isVerticalBorder() {
        return verticalBorder.isSet(field_1_options);
    }


    public void setVerticalBorder(boolean value) {
        field_1_options = verticalBorder.setShortBoolean(field_1_options, value);
    }


    public boolean isBorder() {
        return border.isSet(field_1_options);
    }


    public void setBorder(boolean value) {
        field_1_options = border.setShortBoolean(field_1_options, value);
    }


    public boolean isShowSeriesKey() {
        return showSeriesKey.isSet(field_1_options);
    }


    public void setShowSeriesKey(boolean value) {
        field_1_options = showSeriesKey.setShortBoolean(field_1_options, value);
    }
}
