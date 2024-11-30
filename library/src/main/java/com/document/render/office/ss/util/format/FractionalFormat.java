

package com.document.render.office.ss.util.format;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;


@SuppressWarnings("serial")
public class FractionalFormat extends Format {

    private short ONE_DIGIT = 1;
    private short TWO_DIGIT = 2;
    private short THREE_DIGIT = 3;
    private short UNITS = 4;
    private int units = 1;
    private short mode = -1;


    public FractionalFormat(String formatStr) {
        if ("# ?/?".equals(formatStr)) {
            mode = ONE_DIGIT;
        } else if ("# ??/??".equals(formatStr)) {
            mode = TWO_DIGIT;
        } else if ("# ???/???".equals(formatStr)) {
            mode = THREE_DIGIT;
        } else if ("# ?/2".equals(formatStr)) {
            mode = UNITS;
            units = 2;
        } else if ("# ?/4".equals(formatStr)) {
            mode = UNITS;
            units = 4;
        } else if ("# ?/8".equals(formatStr)) {
            mode = UNITS;
            units = 8;
        } else if ("# ??/16".equals(formatStr)) {
            mode = UNITS;
            units = 16;
        } else if ("# ?/10".equals(formatStr)) {
            mode = UNITS;
            units = 10;
        } else if ("# ??/100".equals(formatStr)) {
            mode = UNITS;
            units = 100;
        }
    }


    private String format(final double f, final int maxDen) {
        long whole = (long) f;
        int sign = 1;
        if (f < 0) {
            sign = -1;
        }
        double precision = 0.00001;
        double allowedError = precision;
        double d = Math.abs(f);
        d -= whole;
        double frac = d;
        double diff = frac;
        long num = 1;
        long den = 0;
        long a = 0;
        long b = 0;
        long i = 0;
        if (frac > precision) {
            while (true) {
                d = 1.0 / d;
                i = (long) (d + precision);
                d -= i;
                if (a > 0) {
                    num = i * num + b;
                }
                den = (long) (num / frac + 0.5);
                diff = Math.abs((double) num / den - frac);
                if (den > maxDen) {
                    if (a > 0) {
                        num = a;
                        den = (long) (num / frac + 0.5);
                        diff = Math.abs((double) num / den - frac);
                    } else {
                        den = maxDen;
                        num = 1;
                        diff = Math.abs((double) num / den - frac);
                        if (diff > frac) {
                            num = 0;
                            den = 1;

                            diff = frac;
                        }
                    }
                    break;
                }
                if ((diff <= allowedError) || (d < precision)) {
                    break;
                }
                precision = allowedError / diff;



                b = a;
                a = num;
            }
        }
        if (num == den) {
            whole++;
            num = 0;
            den = 0;
        } else if (den == 0) {
            num = 0;
        }
        if (sign < 0) {
            if (whole == 0) {
                num = -num;
            } else {
                whole = -whole;
            }
        }
        String value = "";

        if (whole != 0) {
            value = value.concat(String.valueOf(whole));
        }
        if (num != 0 && den != 0) {
            value = value.concat(" " + num + "/" + den);
        }

        return value;
    }


    private String formatUnit(double f, int units) {
        long whole = (long) f;
        f -= whole;
        long num = Math.round(f * units);

        String value = "";
        if (whole != 0) {
            value = value.concat(String.valueOf(whole));
        }
        if (num != 0) {
            value = value.concat(" " + num + "/" + units);
        }

        return value;
    }


    public final String format(double val) {
        if (mode == ONE_DIGIT) {
            return format(val, 9);
        } else if (mode == TWO_DIGIT) {
            return format(val, 99);
        } else if (mode == THREE_DIGIT) {
            return format(val, 999);
        } else if (mode == UNITS) {
            return formatUnit(val, units);
        }
        throw new RuntimeException("Unexpected Case");
    }


    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Number) {
            toAppendTo.append(format(((Number) obj).doubleValue()));
            return toAppendTo;
        }
        throw new IllegalArgumentException("Can only handle Numbers");
    }


    public Object parseObject(String source, ParsePosition status) {
        return null;
    }


    public Object parseObject(String source) throws ParseException {
        return null;
    }


    public Object clone() {
        return null;
    }
}
