


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class DateWindow1904Record
        extends StandardRecord {
    @Keep
    public final static short sid = 0x22;
    private short field_1_window;

    public DateWindow1904Record() {
    }

    public DateWindow1904Record(RecordInputStream in) {
        field_1_window = in.readShort();
    }



    public short getWindowing() {
        return field_1_window;
    }



    public void setWindowing(short window) {
        field_1_window = window;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[1904]\n");
        buffer.append("    .is1904          = ")
                .append(Integer.toHexString(getWindowing())).append("\n");
        buffer.append("[/1904]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getWindowing());
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }
}
