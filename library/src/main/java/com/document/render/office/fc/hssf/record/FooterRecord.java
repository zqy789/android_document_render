

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;


public final class FooterRecord extends HeaderFooterBase {
    @Keep
    public final static short sid = 0x0015;

    public FooterRecord(String text) {
        super(text);
    }

    public FooterRecord(RecordInputStream in) {
        super(in);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[FOOTER]\n");
        buffer.append("    .footer = ").append(getText()).append("\n");
        buffer.append("[/FOOTER]\n");
        return buffer.toString();
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        return new FooterRecord(getText());
    }
}
