

package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.ss.util.DateUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class DataFormatter {


    private static final Pattern numPattern = Pattern.compile("[0#]+");


    private static final Pattern daysAsText = Pattern
            .compile("([d]{3,})", Pattern.CASE_INSENSITIVE);


    private static final Pattern amPmPattern = Pattern.compile("((A|P)[M/P]*)",
            Pattern.CASE_INSENSITIVE);


    private static final Pattern specialPatternGroup = Pattern
            .compile("(\\[\\$[^-\\]]*-[0-9A-Z]+\\])");


    private static final Pattern colorPattern = Pattern.compile(
            "(\\[BLACK\\])|(\\[BLUE\\])|(\\[CYAN\\])|(\\[GREEN\\])|"
                    + "(\\[MAGENTA\\])|(\\[RED\\])|(\\[WHITE\\])|(\\[YELLOW\\])|"
                    + "(\\[COLOR\\s*\\d\\])|(\\[COLOR\\s*[0-5]\\d\\])", Pattern.CASE_INSENSITIVE);


    private static final String invalidDateTimeString;

    static {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 255; i++)
            buf.append('#');
        invalidDateTimeString = buf.toString();
    }


    private final DecimalFormatSymbols decimalSymbols;


    private final DateFormatSymbols dateSymbols;


    private final Format generalWholeNumFormat;


    private final Format generalDecimalNumFormat;

    private final Map<String, Format> formats;

    private Format defaultNumFormat;
    private boolean emulateCsv = false;


    public DataFormatter() {
        this(false);
    }


    public DataFormatter(boolean emulateCsv) {
        this(Locale.getDefault());
        this.emulateCsv = emulateCsv;
    }


    public DataFormatter(Locale locale, boolean emulateCsv) {
        this(locale);
        this.emulateCsv = emulateCsv;
    }


    public DataFormatter(Locale locale) {
        dateSymbols = new DateFormatSymbols(locale);
        decimalSymbols = new DecimalFormatSymbols(locale);
        generalWholeNumFormat = new DecimalFormat("#", decimalSymbols);
        generalDecimalNumFormat = new DecimalFormat("#.##########", decimalSymbols);

        formats = new HashMap<String, Format>();



        Format zipFormat = ZipPlusFourFormat.instance;
        addFormat("00000\\-0000", zipFormat);
        addFormat("00000-0000", zipFormat);

        Format phoneFormat = PhoneFormat.instance;

        addFormat("[<=9999999]###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        addFormat("[<=9999999]###-####;(###) ###-####", phoneFormat);
        addFormat("###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        addFormat("###-####;(###) ###-####", phoneFormat);

        Format ssnFormat = SSNFormat.instance;
        addFormat("000\\-00\\-0000", ssnFormat);
        addFormat("000-00-0000", ssnFormat);
    }


    private static boolean isWholeNumber(double d) {
        return d == Math.floor(d);
    }



    static DecimalFormat createIntegerOnlyFormat(String fmt) {
        DecimalFormat result = new DecimalFormat(fmt);
        result.setParseIntegerOnly(true);
        return result;
    }


    public static void setExcelStyleRoundingMode(DecimalFormat format) {
        setExcelStyleRoundingMode(format, RoundingMode.HALF_UP);
    }


    public static void setExcelStyleRoundingMode(DecimalFormat format, RoundingMode roundingMode) {
        try {
            Method srm = format.getClass().getMethod("setRoundingMode", RoundingMode.class);
            srm.invoke(format, roundingMode);
        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException iae) {

            throw new RuntimeException("Unable to set rounding mode", iae);
        } catch (InvocationTargetException ite) {

            throw new RuntimeException("Unable to set rounding mode", ite);
        } catch (SecurityException se) {

        }
    }


    private Format getFormat(ICell cell) {
        if (cell.getCellStyle() == null) {
            return null;
        }

        int formatIndex = cell.getCellStyle().getDataFormat();
        String formatStr = cell.getCellStyle().getDataFormatString();
        if (formatStr == null || formatStr.trim().length() == 0) {
            return null;
        }
        return getFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }

    private Format getFormat(double cellValue, int formatIndex, String formatStrIn) {
        String formatStr = formatStrIn;


        final int firstAt = formatStr.indexOf(';');
        final int lastAt = formatStr.lastIndexOf(';');

        if (firstAt != -1 && firstAt != lastAt) {
            final int secondAt = formatStr.indexOf(';', firstAt + 1);
            if (secondAt == lastAt) {
                if (cellValue == 0.0) {
                    formatStr = formatStr.substring(lastAt + 1);
                } else {
                    formatStr = formatStr.substring(0, lastAt);
                }
            } else {
                if (cellValue == 0.0) {
                    formatStr = formatStr.substring(secondAt + 1, lastAt);
                } else {
                    formatStr = formatStr.substring(0, secondAt);
                }
            }
        }


        if (emulateCsv && cellValue == 0.0 && formatStr.contains("#") && !formatStr.contains("0")) {
            formatStr = formatStr.replaceAll("#", "");
        }


        Format format = formats.get(formatStr);
        if (format != null) {
            return format;
        }
        if ("General".equalsIgnoreCase(formatStr) || "@".equals(formatStr)) {
            if (isWholeNumber(cellValue)) {
                return generalWholeNumFormat;
            }
            return generalDecimalNumFormat;
        }
        format = createFormat(cellValue, formatIndex, formatStr);
        formats.put(formatStr, format);
        return format;
    }


    public Format createFormat(ICell cell) {

        int formatIndex = cell.getCellStyle().getDataFormat();
        String formatStr = cell.getCellStyle().getDataFormatString();
        return createFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }

    private Format createFormat(double cellValue, int formatIndex, String sFormat) {
        String formatStr = sFormat;


        Matcher colourM = colorPattern.matcher(formatStr);
        while (colourM.find()) {
            String colour = colourM.group();


            int at = formatStr.indexOf(colour);
            if (at == -1)
                break;
            String nFormatStr = formatStr.substring(0, at)
                    + formatStr.substring(at + colour.length());
            if (nFormatStr.equals(formatStr))
                break;


            formatStr = nFormatStr;
            colourM = colorPattern.matcher(formatStr);
        }


        Matcher m = specialPatternGroup.matcher(formatStr);
        while (m.find()) {
            String match = m.group();
            String symbol = match.substring(match.indexOf('$') + 1, match.indexOf('-'));
            if (symbol.indexOf('$') > -1) {
                StringBuffer sb = new StringBuffer();
                sb.append(symbol.substring(0, symbol.indexOf('$')));
                sb.append('\\');
                sb.append(symbol.substring(symbol.indexOf('$'), symbol.length()));
                symbol = sb.toString();
            }
            formatStr = m.replaceAll(symbol);
            m = specialPatternGroup.matcher(formatStr);
        }

        if (formatStr == null || formatStr.trim().length() == 0) {
            return getDefaultFormat(cellValue);
        }

        if (DateUtil.isADateFormat(formatIndex, formatStr) && DateUtil.isValidExcelDate(cellValue)) {
            return createDateFormat(formatStr, cellValue);
        }
        if (numPattern.matcher(formatStr).find()) {
            return createNumberFormat(formatStr, cellValue);
        }

        if (emulateCsv) {
            return new ConstantStringFormat(cleanFormatForNumber(formatStr));
        }

        return null;
    }

    private Format createDateFormat(String pFormatStr, double cellValue) {
        String formatStr = pFormatStr;
        formatStr = formatStr.replaceAll("\\\\-", "-");
        formatStr = formatStr.replaceAll("\\\\,", ",");
        formatStr = formatStr.replaceAll("\\\\\\.", ".");
        formatStr = formatStr.replaceAll("\\\\ ", " ");
        formatStr = formatStr.replaceAll("\\\\/", "/");
        formatStr = formatStr.replaceAll(";@", "");
        formatStr = formatStr.replaceAll("\"/\"", "/");

        boolean hasAmPm = false;
        Matcher amPmMatcher = amPmPattern.matcher(formatStr);
        while (amPmMatcher.find()) {
            formatStr = amPmMatcher.replaceAll("@");
            hasAmPm = true;
            amPmMatcher = amPmPattern.matcher(formatStr);
        }
        formatStr = formatStr.replaceAll("@", "a");

        Matcher dateMatcher = daysAsText.matcher(formatStr);
        if (dateMatcher.find()) {
            String match = dateMatcher.group(0);
            formatStr = dateMatcher.replaceAll(match.toUpperCase().replaceAll("D", "E"));
        }






        StringBuffer sb = new StringBuffer();
        char[] chars = formatStr.toCharArray();
        boolean mIsMonth = true;
        List<Integer> ms = new ArrayList<Integer>();
        boolean isElapsed = false;
        for (int j = 0; j < chars.length; j++) {
            char c = chars[j];
            if (c == '[' && !isElapsed) {
                isElapsed = true;
                mIsMonth = false;
                sb.append(c);
            } else if (c == ']' && isElapsed) {
                isElapsed = false;
                sb.append(c);
            } else if (isElapsed) {
                if (c == 'h' || c == 'H') {
                    sb.append('H');
                } else if (c == 'm' || c == 'M') {
                    sb.append('m');
                } else if (c == 's' || c == 'S') {
                    sb.append('s');
                } else {
                    sb.append(c);
                }
            } else if (c == 'h' || c == 'H') {
                mIsMonth = false;
                if (hasAmPm) {
                    sb.append('h');
                } else {
                    sb.append('H');
                }
            } else if (c == 'm' || c == 'M') {
                if (mIsMonth) {
                    sb.append('M');
                    ms.add(Integer.valueOf(sb.length() - 1));
                } else {
                    sb.append('m');
                }
            } else if (c == 's' || c == 'S') {
                sb.append('s');

                for (int i = 0; i < ms.size(); i++) {
                    int index = ms.get(i).intValue();
                    if (sb.charAt(index) == 'M') {
                        sb.replace(index, index + 1, "m");
                    }
                }
                mIsMonth = true;
                ms.clear();
            } else if (Character.isLetter(c)) {
                mIsMonth = true;
                ms.clear();
                if (c == 'y' || c == 'Y') {
                    sb.append('y');
                } else if (c == 'd' || c == 'D') {
                    sb.append('d');
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        formatStr = sb.toString();

        try {
            return new ExcelStyleDateFormatter(formatStr, dateSymbols);
        } catch (IllegalArgumentException iae) {



            return getDefaultFormat(cellValue);
        }

    }

    private String cleanFormatForNumber(String formatStr) {
        StringBuffer sb = new StringBuffer(formatStr);

        if (emulateCsv) {




            for (int i = 0; i < sb.length(); i++) {
                char c = sb.charAt(i);
                if (c == '_' || c == '*' || c == '?') {
                    if (i > 0 && sb.charAt((i - 1)) == '\\') {

                        continue;
                    }
                    if (c == '?') {
                        sb.setCharAt(i, ' ');
                    } else if (i < sb.length() - 1) {



                        if (c == '_') {
                            sb.setCharAt(i + 1, ' ');
                        } else {
                            sb.deleteCharAt(i + 1);
                        }

                        sb.deleteCharAt(i);
                    }
                }
            }
        } else {




            for (int i = 0; i < sb.length(); i++) {
                char c = sb.charAt(i);
                if (c == '_' || c == '*') {
                    if (i > 0 && sb.charAt((i - 1)) == '\\') {

                        continue;
                    }
                    if (i < sb.length() - 1) {



                        sb.deleteCharAt(i + 1);
                    }

                    sb.deleteCharAt(i);
                }
            }
        }



        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);

            if (c == '\\' || c == '"') {
                sb.deleteCharAt(i);
                i--;


            } else if (c == '+' && i > 0 && sb.charAt(i - 1) == 'E') {
                sb.deleteCharAt(i);
                i--;
            }
        }

        return sb.toString();
    }

    private Format createNumberFormat(String formatStr, double cellValue) {
        final String format = cleanFormatForNumber(formatStr);

        try {
            DecimalFormat df = new DecimalFormat(format, decimalSymbols);
            setExcelStyleRoundingMode(df);
            return df;
        } catch (IllegalArgumentException iae) {



            return getDefaultFormat(cellValue);
        }
    }


    public Format getDefaultFormat(ICell cell) {
        return getDefaultFormat(cell.getNumericCellValue());
    }

    private Format getDefaultFormat(double cellValue) {

        if (defaultNumFormat != null) {
            return defaultNumFormat;


        }
        if (isWholeNumber(cellValue)) {
            return generalWholeNumFormat;
        }
        return generalDecimalNumFormat;
    }


    private String performDateFormatting(Date d, Format dateFormat) {
        if (dateFormat != null) {
            return dateFormat.format(d);
        }
        return d.toString();
    }


    private String getFormattedDateString(ICell cell) {
        Format dateFormat = getFormat(cell);
        if (dateFormat instanceof ExcelStyleDateFormatter) {

            ((ExcelStyleDateFormatter) dateFormat).setDateToBeFormatted(cell.getNumericCellValue());
        }
        Date d = cell.getDateCellValue();
        return performDateFormatting(d, dateFormat);
    }


    private String getFormattedNumberString(ICell cell) {

        Format numberFormat = getFormat(cell);
        double d = cell.getNumericCellValue();
        if (numberFormat == null) {
            return String.valueOf(d);
        }
        return numberFormat.format(new Double(d));
    }


    public String formatRawCellContents(double value, int formatIndex, String formatString) {
        return formatRawCellContents(value, formatIndex, formatString, false);
    }


    public String formatRawCellContents(double value, int formatIndex, String formatString,
                                        boolean use1904Windowing) {

        if (DateUtil.isADateFormat(formatIndex, formatString)) {
            if (DateUtil.isValidExcelDate(value)) {
                Format dateFormat = getFormat(value, formatIndex, formatString);
                if (dateFormat instanceof ExcelStyleDateFormatter) {

                    ((ExcelStyleDateFormatter) dateFormat).setDateToBeFormatted(value);
                }
                Date d = DateUtil.getJavaDate(value, use1904Windowing);
                return performDateFormatting(d, dateFormat);
            }

            if (emulateCsv) {
                return invalidDateTimeString;
            }
        }

        Format numberFormat = getFormat(value, formatIndex, formatString);
        if (numberFormat == null) {
            return String.valueOf(value);
        }

        String result = numberFormat.format(new Double(value));
        if (result.contains("E") && !result.contains("E-")) {
            result = result.replaceFirst("E", "E+");
        }
        return result;
    }


    public String formatCellValue(ICell cell) {
        return formatCellValue(cell, null);
    }




    public String formatCellValue(ICell cell, FormulaEvaluator evaluator) {

        if (cell == null) {
            return "";
        }

        int cellType = cell.getCellType();
        if (cellType == ICell.CELL_TYPE_FORMULA) {
            if (evaluator == null) {
                return cell.getCellFormula();
            }
            cellType = evaluator.evaluateFormulaCell(cell);
        }
        switch (cellType) {
            case ICell.CELL_TYPE_NUMERIC:


                return getFormattedNumberString(cell);

            case ICell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString();

            case ICell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case ICell.CELL_TYPE_BLANK:
                return "";
        }
        throw new RuntimeException("Unexpected celltype (" + cellType + ")");
    }


    public void setDefaultNumberFormat(Format format) {
        Iterator<Map.Entry<String, Format>> itr = formats.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Format> entry = itr.next();
            if (entry.getValue() == generalDecimalNumFormat
                    || entry.getValue() == generalWholeNumFormat) {
                entry.setValue(format);
            }
        }
        defaultNumFormat = format;
    }


    public void addFormat(String excelFormatStr, Format format) {
        formats.put(excelFormatStr, format);
    }


    @SuppressWarnings("serial")
    private static final class SSNFormat extends Format {
        public static final Format instance = new SSNFormat();
        private static final DecimalFormat df = createIntegerOnlyFormat("000000000");

        private SSNFormat() {

        }


        public static String format(Number num) {
            String result = df.format(num);
            StringBuffer sb = new StringBuffer();
            sb.append(result.substring(0, 3)).append('-');
            sb.append(result.substring(3, 5)).append('-');
            sb.append(result.substring(5, 9));
            return sb.toString();
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(format((Number) obj));
        }

        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }


    @SuppressWarnings("serial")
    private static final class ZipPlusFourFormat extends Format {
        public static final Format instance = new ZipPlusFourFormat();
        private static final DecimalFormat df = createIntegerOnlyFormat("000000000");

        private ZipPlusFourFormat() {

        }


        public static String format(Number num) {
            String result = df.format(num);
            StringBuffer sb = new StringBuffer();
            sb.append(result.substring(0, 5)).append('-');
            sb.append(result.substring(5, 9));
            return sb.toString();
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(format((Number) obj));
        }

        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }


    @SuppressWarnings("serial")
    private static final class PhoneFormat extends Format {
        public static final Format instance = new PhoneFormat();
        private static final DecimalFormat df = createIntegerOnlyFormat("##########");

        private PhoneFormat() {

        }


        public static String format(Number num) {
            String result = df.format(num);
            StringBuffer sb = new StringBuffer();
            String seg1, seg2, seg3;
            int len = result.length();
            if (len <= 4) {
                return result;
            }

            seg3 = result.substring(len - 4, len);
            seg2 = result.substring(Math.max(0, len - 7), len - 4);
            seg1 = result.substring(Math.max(0, len - 10), Math.max(0, len - 7));

            if (seg1 != null && seg1.trim().length() > 0) {
                sb.append('(').append(seg1).append(") ");
            }
            if (seg2 != null && seg2.trim().length() > 0) {
                sb.append(seg2).append('-');
            }
            sb.append(seg3);
            return sb.toString();
        }

        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(format((Number) obj));
        }

        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }


    @SuppressWarnings("serial")
    private static final class ConstantStringFormat extends Format {
        private static final DecimalFormat df = createIntegerOnlyFormat("##########");
        private final String str;

        public ConstantStringFormat(String s) {
            str = s;
        }

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            return toAppendTo.append(str);
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return df.parseObject(source, pos);
        }
    }
}
