
package com.document.render.office.fc.ss.format;


import com.document.render.office.fc.ss.format.CellFormatPart.PartHandler;

import java.util.regex.Matcher;


public class CellTextFormatter extends CellFormatter {
    static final CellFormatter SIMPLE_TEXT = new CellTextFormatter("@");
    private final int[] textPos;
    private final String desc;

    public CellTextFormatter(String format) {
        super(format);

        final int[] numPlaces = new int[1];

        desc = CellFormatPart.parseFormat(format, CellFormatType.TEXT,
                new PartHandler() {
                    public String handlePart(Matcher m, String part,
                                             CellFormatType type, StringBuffer desc) {
                        if (part.equals("@")) {
                            numPlaces[0]++;
                            return "\u0000";
                        }
                        return null;
                    }
                }).toString();


        textPos = new int[numPlaces[0]];
        int pos = desc.length() - 1;
        for (int i = 0; i < textPos.length; i++) {
            textPos[i] = desc.lastIndexOf("\u0000", pos);
            pos = textPos[i] - 1;
        }
    }


    public void formatValue(StringBuffer toAppendTo, Object obj) {
        int start = toAppendTo.length();
        String text = obj.toString();
        toAppendTo.append(desc);
        for (int i = 0; i < textPos.length; i++) {
            int pos = start + textPos[i];
            toAppendTo.replace(pos, pos + 1, text);
        }
    }


    public void simpleValue(StringBuffer toAppendTo, Object value) {
        SIMPLE_TEXT.formatValue(toAppendTo, value);
    }
}
