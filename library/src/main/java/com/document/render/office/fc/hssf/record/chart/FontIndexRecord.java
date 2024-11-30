

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FontIndexRecord extends StandardRecord {
    public final static short sid = 0x1026;
    private short field_1_fontIndex;


    public FontIndexRecord() {

    }

    public FontIndexRecord(RecordInputStream in) {
        field_1_fontIndex = in.readShort();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[FONTX]\n");
        buffer.append("    .fontIndex            = ")
                .append("0x").append(HexDump.toHex(getFontIndex()))
                .append(" (").append(getFontIndex()).append(" )");
        buffer.append(System.getProperty("line.separator"));

        buffer.append("[/FONTX]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_fontIndex);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        FontIndexRecord rec = new FontIndexRecord();

        rec.field_1_fontIndex = field_1_fontIndex;
        return rec;
    }



    public short getFontIndex() {
        return field_1_fontIndex;
    }


    public void setFontIndex(short field_1_fontIndex) {
        this.field_1_fontIndex = field_1_fontIndex;
    }
}
