


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class BookBoolRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0xDA;
    private short field_1_save_link_values;

    public BookBoolRecord() {
    }

    public BookBoolRecord(RecordInputStream in) {
        field_1_save_link_values = in.readShort();
    }



    public short getSaveLinkValues() {
        return field_1_save_link_values;
    }



    public void setSaveLinkValues(short flag) {
        field_1_save_link_values = flag;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[BOOKBOOL]\n");
        buffer.append("    .savelinkvalues  = ")
                .append(Integer.toHexString(getSaveLinkValues())).append("\n");
        buffer.append("[/BOOKBOOL]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_save_link_values);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }
}
