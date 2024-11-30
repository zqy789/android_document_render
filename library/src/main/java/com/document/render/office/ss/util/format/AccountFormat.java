
package com.document.render.office.ss.util.format;

import java.text.DecimalFormat;
import java.text.Format;


public class AccountFormat {

    private static final double ZERO = 0.000001;
    private static AccountFormat af = new AccountFormat();


    private AccountFormat() {
    }


    public static AccountFormat instance() {
        return af;
    }


    public String format(String pattern, double value) {
        String[] subFormat = pattern.split(";");
        String contents = "";
        switch (subFormat.length) {
            case 1:
                contents = parse(subFormat[0], value, false);
                break;

            case 2:
                contents = parse(subFormat[0] + ";" + subFormat[1], value, false);
                break;

            case 3:
            case 4:
                if (Math.abs(value) > ZERO) {
                    contents = parse(subFormat[0] + ";" + subFormat[1], value, false);
                } else {
                    contents = parse(subFormat[2], 0, true);
                }
                break;
        }
        return contents;
    }


    private String parse(String pattern, double value, boolean isZero) {
        String contents = "";
        String[] subFormat = pattern.split(";");
        int index = pattern.indexOf("*");
        if (Math.abs(value) < ZERO && subFormat.length == 1) {
            String header = pattern.substring(0, index + 1);
            index = pattern.indexOf('-');
            pattern = pattern.replace("#", "");
            pattern = pattern.replace("?", " ");
            contents = header + pattern.substring(index - 1, pattern.length());
        } else {
            pattern = pattern.replace("*", "");
            Format format = new DecimalFormat(pattern);
            contents = format.format(value);

            if (value > 0) {
                value += 0.000000001d;
            } else if (value < 0) {
                value -= 0.000000001d;
            }
            contents = format.format(value);
            contents = contents.substring(0, index) + "*" + contents.substring(index, contents.length());
        }
        return contents;
    }
}
