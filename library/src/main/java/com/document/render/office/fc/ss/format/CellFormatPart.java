

package com.document.render.office.fc.ss.format;

import static com.document.render.office.fc.ss.format.CellFormatter.logger;
import static com.document.render.office.fc.ss.format.CellFormatter.quote;

import android.graphics.Color;

import com.document.render.office.fc.hssf.util.HSSFColor;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CellFormatPart {

    public static final Pattern COLOR_PAT;

    public static final Pattern CONDITION_PAT;

    public static final Pattern SPECIFICATION_PAT;

    public static final Pattern FORMAT_PAT;

    public static final int COLOR_GROUP;

    public static final int CONDITION_OPERATOR_GROUP;

    public static final int CONDITION_VALUE_GROUP;

    public static final int SPECIFICATION_GROUP;
    private static final Map<String, Integer> NAMED_COLORS;

    static {
        NAMED_COLORS = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);

        Map<Integer, HSSFColor> colors = HSSFColor.getIndexHash();
        for (HSSFColor color : colors.values()) {
            Class<? extends HSSFColor> type = color.getClass();
            String name = type.getSimpleName();
            if (name.equals(name.toUpperCase())) {
                short[] rgb = color.getTriplet();
                int c = Color.rgb(rgb[0], rgb[1], rgb[2]);
                NAMED_COLORS.put(name, c);
                if (name.indexOf('_') > 0)
                    NAMED_COLORS.put(name.replace('_', ' '), c);
                if (name.indexOf("_PERCENT") > 0)
                    NAMED_COLORS.put(name.replace("_PERCENT", "%").replace('_', ' '), c);
            }
        }
    }

    static {

        String condition = "([<>=]=?|!=|<>)    # The operator\n"
                + "  \\s*([0-9]+(?:\\.[0-9]*)?)\\s*  # The constant to test against\n";

        String color = "\\[(black|blue|cyan|green|magenta|red|white|yellow|color [0-9]+)\\]";





        String part = "\\\\.                 # Quoted single character\n"
                + "|\"([^\\\\\"]|\\\\.)*\"         # Quoted string of characters (handles escaped quotes like \\\") \n"
                + "|_.                             # Space as wide as a given character\n"
                + "|\\*.                           # Repeating fill character\n"
                + "|@                              # Text: cell text\n"
                + "|([0?\\#](?:[0?\\#,]*))         # Number: digit + other digits and commas\n"
                + "|e[-+]                          # Number: Scientific: Exponent\n"
                + "|m{1,5}                         # Date: month or minute spec\n"
                + "|d{1,4}                         # Date: day/date spec\n"
                + "|y{2,4}                         # Date: year spec\n"
                + "|h{1,2}                         # Date: hour spec\n"
                + "|s{1,2}                         # Date: second spec\n"
                + "|am?/pm?                        # Date: am/pm spec\n"
                + "|\\[h{1,2}\\]                   # Elapsed time: hour spec\n"
                + "|\\[m{1,2}\\]                   # Elapsed time: minute spec\n"
                + "|\\[s{1,2}\\]                   # Elapsed time: second spec\n"
                + "|[^;]                           # A character\n" + "";

        String format = "(?:" + color + ")?                  # Text color\n" + "(?:\\[" + condition
                + "\\])?                # Condition\n" + "((?:" + part
                + ")+)                        # Format spec\n";

        int flags = Pattern.COMMENTS | Pattern.CASE_INSENSITIVE;
        COLOR_PAT = Pattern.compile(color, flags);
        CONDITION_PAT = Pattern.compile(condition, flags);
        SPECIFICATION_PAT = Pattern.compile(part, flags);
        FORMAT_PAT = Pattern.compile(format, flags);





        COLOR_GROUP = findGroup(FORMAT_PAT, "[Blue]@", "Blue");
        CONDITION_OPERATOR_GROUP = findGroup(FORMAT_PAT, "[>=1]@", ">=");
        CONDITION_VALUE_GROUP = findGroup(FORMAT_PAT, "[>=1]@", "1");
        SPECIFICATION_GROUP = findGroup(FORMAT_PAT, "[Blue][>1]\\a ?", "\\a ?");
    }

    private final int color;
    private final CellFormatter format;
    private CellFormatCondition condition;


    public CellFormatPart(String desc) {
        Matcher m = FORMAT_PAT.matcher(desc);
        if (!m.matches()) {
            throw new IllegalArgumentException("Unrecognized format: " + quote(desc));
        }
        color = getColor(m);
        condition = getCondition(m);
        format = getFormatter(m);
    }


    private static int findGroup(Pattern pat, String str, String marker) {
        Matcher m = pat.matcher(str);
        if (!m.find())
            throw new IllegalArgumentException("Pattern \"" + pat.pattern() + "\" doesn't match \""
                    + str + "\"");
        for (int i = 1; i <= m.groupCount(); i++) {
            String grp = m.group(i);
            if (grp != null && grp.equals(marker))
                return i;
        }
        throw new IllegalArgumentException("\"" + marker + "\" not found in \"" + pat.pattern()
                + "\"");
    }


    private static int getColor(Matcher m) {
        String cdesc = m.group(COLOR_GROUP);
        if (cdesc == null || cdesc.length() == 0)
            return -1;
        Integer c = NAMED_COLORS.get(cdesc);
        if (c == null)
            logger.warning("Unknown color: " + quote(cdesc));
        return c;
    }


    static String quoteSpecial(String repl, CellFormatType type) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repl.length(); i++) {
            char ch = repl.charAt(i);
            if (ch == '\'' && type.isSpecial('\'')) {
                sb.append('\u0000');
                continue;
            }

            boolean special = type.isSpecial(ch);
            if (special)
                sb.append("'");
            sb.append(ch);
            if (special)
                sb.append("'");
        }
        return sb.toString();
    }



    public static StringBuffer parseFormat(String fdesc, CellFormatType type,
                                           PartHandler partHandler) {
















        Matcher m = SPECIFICATION_PAT.matcher(fdesc);
        StringBuffer fmt = new StringBuffer();
        while (m.find()) {
            String part = group(m, 0);
            if (part.length() > 0) {
                String repl = partHandler.handlePart(m, part, type, fmt);
                if (repl == null) {
                    switch (part.charAt(0)) {
                        case '\"':
                            repl = quoteSpecial(part.substring(1, part.length() - 1), type);
                            break;
                        case '\\':
                            repl = quoteSpecial(part.substring(1), type);
                            break;
                        case '_':
                            repl = " ";
                            break;
                        case '*':
                            repl = expandChar(part);
                            break;
                        default:
                            repl = part;
                            break;
                    }
                }
                m.appendReplacement(fmt, Matcher.quoteReplacement(repl));
            }
        }
        m.appendTail(fmt);

        if (type.isSpecial('\'')) {

            int pos = 0;
            while ((pos = fmt.indexOf("''", pos)) >= 0) {
                fmt.delete(pos, pos + 2);
            }


            pos = 0;
            while ((pos = fmt.indexOf("\u0000", pos)) >= 0) {
                fmt.replace(pos, pos + 1, "''");
            }
        }

        return fmt;
    }


    static String expandChar(String part) {
        String repl;
        char ch = part.charAt(1);
        repl = "" + ch + ch + ch;
        return repl;
    }


    public static String group(Matcher m, int g) {
        String str = m.group(g);
        return (str == null ? "" : str);
    }


    public boolean applies(Object valueObject) {
        if (condition == null || !(valueObject instanceof Number)) {
            if (valueObject == null)
                throw new NullPointerException("valueObject");
            return true;
        } else {
            Number num = (Number) valueObject;
            return condition.pass(num.doubleValue());
        }
    }


    private CellFormatCondition getCondition(Matcher m) {
        String mdesc = m.group(CONDITION_OPERATOR_GROUP);
        if (mdesc == null || mdesc.length() == 0)
            return null;
        return CellFormatCondition.getInstance(m.group(CONDITION_OPERATOR_GROUP),
                m.group(CONDITION_VALUE_GROUP));
    }


    private CellFormatter getFormatter(Matcher matcher) {
        String fdesc = matcher.group(SPECIFICATION_GROUP);
        CellFormatType type = formatType(fdesc);
        return type.formatter(fdesc);
    }


    private CellFormatType formatType(String fdesc) {
        fdesc = fdesc.trim();
        if (fdesc.equals("") || fdesc.equalsIgnoreCase("General"))
            return CellFormatType.GENERAL;

        Matcher m = SPECIFICATION_PAT.matcher(fdesc);
        boolean couldBeDate = false;
        boolean seenZero = false;
        while (m.find()) {
            String repl = m.group(0);
            if (repl.length() > 0) {
                switch (repl.charAt(0)) {
                    case '@':
                        return CellFormatType.TEXT;
                    case 'd':
                    case 'D':
                    case 'y':
                    case 'Y':
                        return CellFormatType.DATE;
                    case 'h':
                    case 'H':
                    case 'm':
                    case 'M':
                    case 's':
                    case 'S':

                        couldBeDate = true;
                        break;
                    case '0':

                        seenZero = true;
                        break;
                    case '[':
                        return CellFormatType.ELAPSED;
                    case '#':
                    case '?':
                        return CellFormatType.NUMBER;
                }
            }
        }


        if (couldBeDate)
            return CellFormatType.DATE;
        if (seenZero)
            return CellFormatType.NUMBER;
        return CellFormatType.TEXT;
    }


    public CellFormatResult apply(Object value) {
        boolean applies = applies(value);
        String text;
        int textColor;
        if (applies) {
            text = format.format(value);
            textColor = color;
        } else {
            text = format.simpleFormat(value);
            textColor = -1;
        }
        return new CellFormatResult(applies, text, textColor);
    }

    interface PartHandler {
        String handlePart(Matcher m, String part, CellFormatType type, StringBuffer desc);
    }
}
