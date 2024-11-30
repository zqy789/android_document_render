


package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class CodepageRecord
        extends StandardRecord {
    public final static short sid = 0x42;


    public final static short CODEPAGE = (short) 0x4b0;
    private short field_1_codepage;

    public CodepageRecord() {
    }

    public CodepageRecord(RecordInputStream in) {
        field_1_codepage = in.readShort();
    }



    public short getCodepage() {
        return field_1_codepage;
    }



    public void setCodepage(short cp) {
        field_1_codepage = cp;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CODEPAGE]\n");
        buffer.append("    .codepage        = ")
                .append(Integer.toHexString(getCodepage())).append("\n");
        buffer.append("[/CODEPAGE]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getCodepage());
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }
}
