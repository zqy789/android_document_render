

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public final class FileSharingRecord extends StandardRecord {

    @Keep
    public final static short sid = 0x005B;
    private short field_1_readonly;
    private short field_2_password;
    private byte field_3_username_unicode_options;
    private String field_3_username_value;

    public FileSharingRecord() {
    }

    public FileSharingRecord(RecordInputStream in) {
        field_1_readonly = in.readShort();
        field_2_password = in.readShort();

        int nameLen = in.readShort();

        if (nameLen > 0) {
            
            field_3_username_unicode_options = in.readByte();
            field_3_username_value = in.readCompressedUnicode(nameLen);
        } else {
            field_3_username_value = "";
        }
    }

    
    
    public static short hashPassword(String password) {
        byte[] passwordCharacters = password.getBytes();
        int hash = 0;
        if (passwordCharacters.length > 0) {
            int charIndex = passwordCharacters.length;
            while (charIndex-- > 0) {
                hash = ((hash >> 14) & 0x01) | ((hash << 1) & 0x7fff);
                hash ^= passwordCharacters[charIndex];
            }
            
            hash = ((hash >> 14) & 0x01) | ((hash << 1) & 0x7fff);
            hash ^= passwordCharacters.length;
            hash ^= (0x8000 | ('N' << 8) | 'K');
        }
        return (short) hash;
    }

    
    public short getReadOnly() {
        return field_1_readonly;
    }

    
    public void setReadOnly(short readonly) {
        field_1_readonly = readonly;
    }

    
    public short getPassword() {
        return field_2_password;
    }

    
    public void setPassword(short password) {
        field_2_password = password;
    }

    
    public String getUsername() {
        return field_3_username_value;
    }

    
    public void setUsername(String username) {
        field_3_username_value = username;
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[FILESHARING]\n");
        buffer.append("    .readonly       = ")
                .append(getReadOnly() == 1 ? "true" : "false").append("\n");
        buffer.append("    .password       = ")
                .append(Integer.toHexString(getPassword())).append("\n");
        buffer.append("    .username       = ")
                .append(getUsername()).append("\n");
        buffer.append("[/FILESHARING]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        
        out.writeShort(getReadOnly());
        out.writeShort(getPassword());
        out.writeShort(field_3_username_value.length());
        if (field_3_username_value.length() > 0) {
            out.writeByte(field_3_username_unicode_options);
            StringUtil.putCompressedUnicode(getUsername(), out);
        }
    }

    protected int getDataSize() {
        int nameLen = field_3_username_value.length();
        if (nameLen < 1) {
            return 6;
        }
        return 7 + nameLen;
    }

    public short getSid() {
        return sid;
    }

    
    public Object clone() {
        FileSharingRecord clone = new FileSharingRecord();
        clone.setReadOnly(field_1_readonly);
        clone.setPassword(field_2_password);
        clone.setUsername(field_3_username_value);
        return clone;
    }
}
