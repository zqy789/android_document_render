


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class PrintGridlinesRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x2b;
    private short field_1_print_gridlines;

    public PrintGridlinesRecord() {
    }

    public PrintGridlinesRecord(RecordInputStream in) {
        field_1_print_gridlines = in.readShort();
    }



    public boolean getPrintGridlines() {
        return (field_1_print_gridlines == 1);
    }



    public void setPrintGridlines(boolean pg) {
        if (pg == true) {
            field_1_print_gridlines = 1;
        } else {
            field_1_print_gridlines = 0;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PRINTGRIDLINES]\n");
        buffer.append("    .printgridlines = ").append(getPrintGridlines())
                .append("\n");
        buffer.append("[/PRINTGRIDLINES]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_print_gridlines);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        PrintGridlinesRecord rec = new PrintGridlinesRecord();
        rec.field_1_print_gridlines = field_1_print_gridlines;
        return rec;
    }
}
