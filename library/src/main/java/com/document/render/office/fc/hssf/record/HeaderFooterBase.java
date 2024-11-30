

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public abstract class HeaderFooterBase extends StandardRecord {
    private boolean field_2_hasMultibyte;
    private String field_3_text;

    protected HeaderFooterBase(String text) {
        setText(text);
    }

    protected HeaderFooterBase(RecordInputStream in) {
        if (in.remaining() > 0) {
            int field_1_footer_len = in.readShort();
            field_2_hasMultibyte = in.readByte() != 0x00;

            if (field_2_hasMultibyte) {
                field_3_text = in.readUnicodeLEString(field_1_footer_len);
            } else {
                field_3_text = in.readCompressedUnicode(field_1_footer_len);
            }
        } else {
            
            
            field_3_text = "";
        }
    }

    
    private int getTextLength() {
        return field_3_text.length();
    }

    public final String getText() {
        return field_3_text;
    }

    
    public final void setText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        field_2_hasMultibyte = StringUtil.hasMultibyte(text);
        field_3_text = text;

        
        if (getDataSize() > RecordInputStream.MAX_RECORD_DATA_SIZE) {
            throw new IllegalArgumentException("Header/Footer string too long (limit is "
                    + RecordInputStream.MAX_RECORD_DATA_SIZE + " bytes)");
        }
    }

    public final void serialize(LittleEndianOutput out) {
        if (getTextLength() > 0) {
            out.writeShort(getTextLength());
            out.writeByte(field_2_hasMultibyte ? 0x01 : 0x00);
            if (field_2_hasMultibyte) {
                StringUtil.putUnicodeLE(field_3_text, out);
            } else {
                StringUtil.putCompressedUnicode(field_3_text, out);
            }
        }
    }

    protected final int getDataSize() {
        if (getTextLength() < 1) {
            return 0;
        }
        return 3 + getTextLength() * (field_2_hasMultibyte ? 2 : 1);
    }
}
