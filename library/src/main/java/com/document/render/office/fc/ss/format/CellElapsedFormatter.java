
package com.document.render.office.fc.ss.format;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CellElapsedFormatter extends CellFormatter {
    private static final Pattern PERCENTS = Pattern.compile("%");
    private static final double HOUR__FACTOR = 1.0 / 24.0;
    private static final double MIN__FACTOR = HOUR__FACTOR / 60.0;
    private static final double SEC__FACTOR = MIN__FACTOR / 60.0;
    private final List<TimeSpec> specs;
    private final String printfFmt;
    private TimeSpec topmost;


    public CellElapsedFormatter(String pattern) {
        super(pattern);

        specs = new ArrayList<TimeSpec>();

        StringBuffer desc = CellFormatPart.parseFormat(pattern,
                CellFormatType.ELAPSED, new ElapsedPartHandler());

        ListIterator<TimeSpec> it = specs.listIterator(specs.size());
        while (it.hasPrevious()) {
            TimeSpec spec = it.previous();
            desc.replace(spec.pos, spec.pos + spec.len, "%0" + spec.len + "d");
            if (spec.type != topmost.type) {
                spec.modBy = modFor(spec.type, spec.len);
            }
        }

        printfFmt = desc.toString();
    }

    private static double factorFor(char type, int len) {
        switch (type) {
            case 'h':
                return HOUR__FACTOR;
            case 'm':
                return MIN__FACTOR;
            case 's':
                return SEC__FACTOR;
            case '0':
                return SEC__FACTOR / Math.pow(10, len);
            default:
                throw new IllegalArgumentException(
                        "Uknown elapsed time spec: " + type);
        }
    }

    private static double modFor(char type, int len) {
        switch (type) {
            case 'h':
                return 24;
            case 'm':
                return 60;
            case 's':
                return 60;
            case '0':
                return Math.pow(10, len);
            default:
                throw new IllegalArgumentException(
                        "Uknown elapsed time spec: " + type);
        }
    }

    private TimeSpec assignSpec(char type, int pos, int len) {
        TimeSpec spec = new TimeSpec(type, pos, len, factorFor(type, len));
        specs.add(spec);
        return spec;
    }


    public void formatValue(StringBuffer toAppendTo, Object value) {
        double elapsed = ((Number) value).doubleValue();

        if (elapsed < 0) {
            toAppendTo.append('-');
            elapsed = -elapsed;
        }

        Object[] parts = new Long[specs.size()];
        for (int i = 0; i < specs.size(); i++) {
            parts[i] = specs.get(i).valueFor(elapsed);
        }

        Formatter formatter = new Formatter(toAppendTo);
        formatter.format(printfFmt, parts);
    }


    public void simpleValue(StringBuffer toAppendTo, Object value) {
        formatValue(toAppendTo, value);
    }

    private static class TimeSpec {
        final char type;
        final int pos;
        final int len;
        final double factor;
        double modBy;

        public TimeSpec(char type, int pos, int len, double factor) {
            this.type = type;
            this.pos = pos;
            this.len = len;
            this.factor = factor;
            modBy = 0;
        }

        public long valueFor(double elapsed) {
            double val;
            if (modBy == 0)
                val = elapsed / factor;
            else
                val = elapsed / factor % modBy;
            if (type == '0')
                return Math.round(val);
            else
                return (long) val;
        }
    }

    private class ElapsedPartHandler implements CellFormatPart.PartHandler {





        public String handlePart(Matcher m, String part, CellFormatType type,
                                 StringBuffer desc) {

            int pos = desc.length();
            char firstCh = part.charAt(0);
            switch (firstCh) {
                case '[':
                    if (part.length() < 3)
                        break;
                    if (topmost != null)
                        throw new IllegalArgumentException(
                                "Duplicate '[' times in format");
                    part = part.toLowerCase();
                    int specLen = part.length() - 2;
                    topmost = assignSpec(part.charAt(1), pos, specLen);
                    return part.substring(1, 1 + specLen);

                case 'h':
                case 'm':
                case 's':
                case '0':
                    part = part.toLowerCase();
                    assignSpec(part.charAt(0), pos, part.length());
                    return part;

                case '\n':
                    return "%n";

                case '\"':
                    part = part.substring(1, part.length() - 1);
                    break;

                case '\\':
                    part = part.substring(1);
                    break;

                case '*':
                    if (part.length() > 1)
                        part = CellFormatPart.expandChar(part);
                    break;


                case '_':
                    return null;
            }

            return PERCENTS.matcher(part).replaceAll("%%");
        }
    }
}
