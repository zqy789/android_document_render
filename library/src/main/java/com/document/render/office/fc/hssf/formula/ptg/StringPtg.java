

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;


public final class StringPtg extends ScalarConstantPtg {
    public final static byte sid = 0x17;

    private static final char FORMULA_DELIMITER = '"';

    private final boolean _is16bitUnicode;

    private final String field_3_string;


    public StringPtg(LittleEndianInput in) {
        int nChars = in.readUByte();
        _is16bitUnicode = (in.readByte() & 0x01) != 0;
        if (_is16bitUnicode) {
            field_3_string = StringUtil.readUnicodeLE(in, nChars);
        } else {
            field_3_string = StringUtil.readCompressedUnicode(in, nChars);
        }
    }


    public StringPtg(String value) {
        if (value.length() > 255) {
            throw new IllegalArgumentException(
                    "String literals in formulas can't be bigger than 255 characters ASCII");
        }
        _is16bitUnicode = StringUtil.hasMultibyte(value);
        field_3_string = value;
    }

    public String getValue() {
        return field_3_string;
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeByte(field_3_string.length());
        out.writeByte(_is16bitUnicode ? 0x01 : 0x00);
        if (_is16bitUnicode) {
            StringUtil.putUnicodeLE(field_3_string, out);
        } else {
            StringUtil.putCompressedUnicode(field_3_string, out);
        }
    }

    public int getSize() {
        return 3 + field_3_string.length() * (_is16bitUnicode ? 2 : 1);
    }

    public String toFormulaString() {
        String value = field_3_string;
        int len = value.length();
        StringBuffer sb = new StringBuffer(len + 4);
        sb.append(FORMULA_DELIMITER);

        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c == FORMULA_DELIMITER) {
                sb.append(FORMULA_DELIMITER);
            }
            sb.append(c);
        }

        sb.append(FORMULA_DELIMITER);
        return sb.toString();
    }
}
