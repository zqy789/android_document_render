
package com.document.render.office.fc.ss.format;

import java.util.Locale;
import java.util.logging.Logger;


public abstract class CellFormatter {

    public static final Locale LOCALE = Locale.US;

    static final Logger logger = Logger.getLogger(
            CellFormatter.class.getName());

    protected final String format;


    public CellFormatter(String format) {
        this.format = format;
    }


    static String quote(String str) {
        return '"' + str + '"';
    }


    public abstract void formatValue(StringBuffer toAppendTo, Object value);


    public abstract void simpleValue(StringBuffer toAppendTo, Object value);


    public String format(Object value) {
        StringBuffer sb = new StringBuffer();
        formatValue(sb, value);
        return sb.toString();
    }


    public String simpleFormat(Object value) {
        StringBuffer sb = new StringBuffer();
        simpleValue(sb, value);
        return sb.toString();
    }
}
