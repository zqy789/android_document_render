
package com.document.render.office.fc.ss.usermodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public final class BuiltinFormats {

    public static final int FIRST_USER_DEFINED_FORMAT_INDEX = 164;

    private final static String[] _formats;


    static {
        List<String> m = new ArrayList<String>();
        putFormat(m, 0, "General");
        putFormat(m, 1, "0");
        putFormat(m, 2, "0.00");
        putFormat(m, 3, "#,##0");
        putFormat(m, 4, "#,##0.00");
        putFormat(m, 5, "\"$\"#,##0_);(\"$\"#,##0)");
        putFormat(m, 6, "\"$\"#,##0_);[Red](\"$\"#,##0)");
        putFormat(m, 7, "\"$\"#,##0.00_);(\"$\"#,##0.00)");
        putFormat(m, 8, "\"$\"#,##0.00_);[Red](\"$\"#,##0.00)");
        putFormat(m, 9, "0%");
        putFormat(m, 0xa, "0.00%");
        putFormat(m, 0xb, "0.00E+00");
        putFormat(m, 0xc, "# ?/?");
        putFormat(m, 0xd, "# ??/??");
        putFormat(m, 0xe, "m/d/yy");
        putFormat(m, 0xf, "d-mmm-yy");
        putFormat(m, 0x10, "d-mmm");
        putFormat(m, 0x11, "mmm-yy");
        putFormat(m, 0x12, "h:mm AM/PM");
        putFormat(m, 0x13, "h:mm:ss AM/PM");
        putFormat(m, 0x14, "h:mm");
        putFormat(m, 0x15, "h:mm:ss");
        putFormat(m, 0x16, "m/d/yy h:mm");


        for (int i = 0x17; i <= 0x24; i++) {

            putFormat(m, i, "reserved-0x" + Integer.toHexString(i));
        }

        putFormat(m, 0x25, "#,##0_);(#,##0)");
        putFormat(m, 0x26, "#,##0_);[Red](#,##0)");
        putFormat(m, 0x27, "#,##0.00_);(#,##0.00)");
        putFormat(m, 0x28, "#,##0.00_);[Red](#,##0.00)");
        putFormat(m, 0x29, "_(\"$\"* #,##0_);_(\"$\"* (#,##0);_(\"$\"* \"-\"_);_(@_)");
        putFormat(m, 0x2a, "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)");
        putFormat(m, 0x2b, "_(\"$\"* #,##0.00_);_(\"$\"* (#,##0.00);_(\"$\"* \"-\"??_);_(@_)");
        putFormat(m, 0x2c, "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)");
        putFormat(m, 0x2d, "mm:ss");
        putFormat(m, 0x2e, "[h]:mm:ss");
        putFormat(m, 0x2f, "mm:ss.0");
        putFormat(m, 0x30, "##0.0E+0");
        putFormat(m, 0x31, "@");


        for (int i = 0x32; i <= 0x38; i++) {

            putFormat(m, i, "General" + Integer.toHexString(i));
        }

        putFormat(m, 0x39, "yyyy\"年\"m\"月\"");

        String[] ss = new String[m.size()];
        m.toArray(ss);
        _formats = ss;
    }

    private static void putFormat(List<String> m, int index, String value) {
        if (m.size() != index) {
            throw new IllegalStateException("index " + index + " is wrong");
        }
        m.add(value);
    }



    public static Map<Integer, String> getBuiltinFormats() {
        Map<Integer, String> result = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < _formats.length; i++) {
            result.put(Integer.valueOf(i), _formats[i]);
        }
        return result;
    }


    public static String[] getAll() {
        return _formats.clone();
    }


    public static String getBuiltinFormat(int index) {
        if (index < 0 || index >= _formats.length) {
            return null;
        }
        return _formats[index];
    }


    public static int getBuiltinFormat(String pFmt) {
        String fmt;
        if (pFmt.equalsIgnoreCase("TEXT")) {
            fmt = "@";
        } else {
            fmt = pFmt;
        }

        for (int i = 0; i < _formats.length; i++) {
            if (fmt.equals(_formats[i])) {
                return i;
            }
        }
        return -1;
    }
}
