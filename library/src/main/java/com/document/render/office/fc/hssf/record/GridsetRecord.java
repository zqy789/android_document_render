


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class GridsetRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x82;
    public short field_1_gridset_flag;

    public GridsetRecord() {
    }

    public GridsetRecord(RecordInputStream in) {
        field_1_gridset_flag = in.readShort();
    }

    

    public boolean getGridset() {
        return (field_1_gridset_flag == 1);
    }

    

    public void setGridset(boolean gridset) {
        if (gridset == true) {
            field_1_gridset_flag = 1;
        } else {
            field_1_gridset_flag = 0;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[GRIDSET]\n");
        buffer.append("    .gridset        = ").append(getGridset())
                .append("\n");
        buffer.append("[/GRIDSET]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_gridset_flag);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        GridsetRecord rec = new GridsetRecord();
        rec.field_1_gridset_flag = field_1_gridset_flag;
        return rec;
    }
}
