

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;


public final class HeaderRecord extends HeaderFooterBase {
    @Keep
    public final static short sid = 0x0014;

    public HeaderRecord(String text) {
        super(text);
    }

    public HeaderRecord(RecordInputStream in) {
        super(in);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[HEADER]\n");
        buffer.append("    .header = ").append(getText()).append("\n");
        buffer.append("[/HEADER]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return new HeaderRecord(getText());
    }
}
