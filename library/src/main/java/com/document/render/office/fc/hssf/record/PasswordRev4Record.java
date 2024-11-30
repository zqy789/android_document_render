

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class PasswordRev4Record extends StandardRecord {
    @Keep
    public final static short sid = 0x01BC;
    private int field_1_password;

    public PasswordRev4Record(int pw) {
        field_1_password = pw;
    }

    public PasswordRev4Record(RecordInputStream in) {
        field_1_password = in.readShort();
    }


    public void setPassword(short pw) {
        field_1_password = pw;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PROT4REVPASSWORD]\n");
        buffer.append("    .password = ").append(HexDump.shortToHex(field_1_password)).append("\n");
        buffer.append("[/PROT4REVPASSWORD]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_1_password);
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }
}
