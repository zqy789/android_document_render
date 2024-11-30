

package com.document.render.office.fc.hssf.record.common;

import com.document.render.office.fc.hssf.record.FeatRecord;
import com.document.render.office.fc.hssf.record.PasswordRecord;
import com.document.render.office.fc.hssf.record.PasswordRev4Record;
import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public final class FeatProtection implements SharedFeature {
    public static long NO_SELF_RELATIVE_SECURITY_FEATURE = 0;
    public static long HAS_SELF_RELATIVE_SECURITY_FEATURE = 1;

    private int fSD;


    private int passwordVerifier;

    private String title;
    private byte[] securityDescriptor;

    public FeatProtection() {
        securityDescriptor = new byte[0];
    }

    public FeatProtection(RecordInputStream in) {
        fSD = in.readInt();
        passwordVerifier = in.readInt();

        title = StringUtil.readUnicodeString(in);

        securityDescriptor = in.readRemainder();
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" [FEATURE PROTECTION]\n");
        buffer.append("   Self Relative = " + fSD);
        buffer.append("   Password Verifier = " + passwordVerifier);
        buffer.append("   Title = " + title);
        buffer.append("   Security Descriptor Size = " + securityDescriptor.length);
        buffer.append(" [/FEATURE PROTECTION]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(fSD);
        out.writeInt(passwordVerifier);
        StringUtil.writeUnicodeString(out, title);
        out.write(securityDescriptor);
    }

    public int getDataSize() {
        return 4 + 4 + StringUtil.getEncodedSize(title) + securityDescriptor.length;
    }

    public int getPasswordVerifier() {
        return passwordVerifier;
    }

    public void setPasswordVerifier(int passwordVerifier) {
        this.passwordVerifier = passwordVerifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFSD() {
        return fSD;
    }
}
