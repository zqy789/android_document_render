

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;

import java.util.Arrays;



public final class WriteAccessRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x005C;

    private static final byte PAD_CHAR = (byte) ' ';
    private static final int DATA_SIZE = 112;

    private static final byte[] PADDING = new byte[DATA_SIZE];

    static {
        Arrays.fill(PADDING, PAD_CHAR);
    }

    private String field_1_username;

    public WriteAccessRecord() {
        setUsername("");
    }

    public WriteAccessRecord(RecordInputStream in) {







        int nChars = in.readUShort();
        int is16BitFlag = in.readUByte();
        if (nChars > DATA_SIZE || (is16BitFlag & 0xFE) != 0) {



            byte[] data = new byte[3 + in.remaining()];
            LittleEndian.putUShort(data, 0, nChars);
            LittleEndian.putByte(data, 2, is16BitFlag);
            in.readFully(data, 3, data.length - 3);
            String rawValue = new String(data);
            setUsername(rawValue.trim());
            return;
        }

        String rawText;
        if ((is16BitFlag & 0x01) == 0x00) {
            rawText = StringUtil.readCompressedUnicode(in, nChars);
        } else {
            rawText = StringUtil.readUnicodeLE(in, nChars);
        }
        field_1_username = rawText.trim();


        int padSize = in.remaining();
        while (padSize > 0) {

            in.readUByte();
            padSize--;
        }
    }


    public String getUsername() {
        return field_1_username;
    }


    public void setUsername(String username) {
        boolean is16bit = StringUtil.hasMultibyte(username);
        int encodedByteCount = 3 + username.length() * (is16bit ? 2 : 1);
        int paddingSize = DATA_SIZE - encodedByteCount;
        if (paddingSize < 0) {
            throw new IllegalArgumentException("Name is too long: " + username);
        }

        field_1_username = username;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[WRITEACCESS]\n");
        buffer.append("    .name = ").append(field_1_username.toString()).append("\n");
        buffer.append("[/WRITEACCESS]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        String username = getUsername();
        boolean is16bit = StringUtil.hasMultibyte(username);

        out.writeShort(username.length());
        out.writeByte(is16bit ? 0x01 : 0x00);
        if (is16bit) {
            StringUtil.putUnicodeLE(username, out);
        } else {
            StringUtil.putCompressedUnicode(username, out);
        }
        int encodedByteCount = 3 + username.length() * (is16bit ? 2 : 1);
        int paddingSize = DATA_SIZE - encodedByteCount;
        out.write(PADDING, 0, paddingSize);
    }

    protected int getDataSize() {
        return DATA_SIZE;
    }

    public short getSid() {
        return sid;
    }
}
