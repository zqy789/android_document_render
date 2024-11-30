

package com.document.render.office.ss.util;

import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.usermodel.ICellStyle;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.style.CellStyle;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;



public class DateUtil {
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    private static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);
    private static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;
    private static final int BAD_DATE = -1;
    private static final Pattern TIME_SEPARATOR_PATTERN = Pattern.compile(":");

    private static final Pattern date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
    private static final Pattern date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
    private static final Pattern date_ptrn3 = Pattern
            .compile("^[\\[\\]yYmMdDhHsS\\-/,. :\"\\\\]+0*[ampAMP/]*$");

    private static final Pattern date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
    protected DateUtil() {

    }


    public static double getExcelDate(Date date) {
        return getExcelDate(date, false);
    }


    public static double getExcelDate(Date date, boolean use1904windowing) {
        Calendar calStart = new GregorianCalendar();
        calStart.setTime(date);
        return internalGetExcelDate(calStart, use1904windowing);
    }


    public static double getExcelDate(Calendar date, boolean use1904windowing) {

        return internalGetExcelDate((Calendar) date.clone(), use1904windowing);
    }

    private static double internalGetExcelDate(Calendar date, boolean use1904windowing) {
        if ((!use1904windowing && date.get(Calendar.YEAR) < 1900)
                || (use1904windowing && date.get(Calendar.YEAR) < 1904)) {
            return BAD_DATE;
        }







        double fraction = (((date.get(Calendar.HOUR_OF_DAY) * 60 + date.get(Calendar.MINUTE)) * 60 + date
                .get(Calendar.SECOND)) * 1000 + date.get(Calendar.MILLISECOND))
                / (double) DAY_MILLISECONDS;
        Calendar calStart = dayStart(date);

        double value = fraction + absoluteDay(calStart, use1904windowing);

        if (!use1904windowing && value >= 60) {
            value++;
        } else if (use1904windowing) {
            value--;
        }

        return value;
    }


    public static Date getJavaDate(double date) {
        return getJavaDate(date, false);
    }


    public static Date getJavaDate(double date, boolean use1904windowing) {
        if (!isValidExcelDate(date)) {
            return null;
        }
        int wholeDays = (int) Math.floor(date);
        int millisecondsInDay = (int) ((date - wholeDays) * DAY_MILLISECONDS + 0.5);
        Calendar calendar = new GregorianCalendar();
        setCalendar(calendar, wholeDays, millisecondsInDay, use1904windowing);
        return calendar.getTime();
    }

    public static void setCalendar(Calendar calendar, int wholeDays, int millisecondsInDay,
                                   boolean use1904windowing) {
        int startYear = 1900;
        int dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        } else if (wholeDays < 61) {


            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, millisecondsInDay);
    }


    public static boolean isADateFormat(int formatIndex, String formatString) {

        if (isInternalDateFormat(formatIndex)) {
            return true;
        }


        if (formatString == null || formatString.length() == 0) {
            return false;
        }

        String fs = formatString;
        if (false) {




            fs = fs.replaceAll("\\\\-", "-");

            fs = fs.replaceAll("\\\\,", ",");

            fs = fs.replaceAll("\\\\\\.", ".");

            fs = fs.replaceAll("\\\\ ", " ");



            fs = fs.replaceAll(";@", "");



        }
        StringBuilder sb = new StringBuilder(fs.length());
        for (int i = 0; i < fs.length(); i++) {
            char c = fs.charAt(i);
            if (i < fs.length() - 1) {
                char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    switch (nc) {
                        case '-':
                        case ',':
                        case '.':
                        case ' ':
                        case '\\':

                            continue;
                    }
                } else if (c == ';' && nc == '@') {
                    i++;

                    continue;
                }
            }
            sb.append(c);
        }
        fs = sb.toString();


        if (date_ptrn4.matcher(fs).matches()) {
            return true;
        }



        fs = date_ptrn1.matcher(fs).replaceAll("");


        fs = date_ptrn2.matcher(fs).replaceAll("");



        if (fs.indexOf(';') > 0 && fs.indexOf(';') < fs.length() - 1) {
            fs = fs.substring(0, fs.indexOf(';'));
        }




        return date_ptrn3.matcher(fs).matches();
    }


    public static boolean isInternalDateFormat(int format) {
        switch (format) {


            case 0x0e:
            case 0x0f:
            case 0x10:
            case 0x11:
            case 0x12:
            case 0x13:
            case 0x14:
            case 0x15:
            case 0x16:
            case 0x2d:
            case 0x2e:
            case 0x2f:
                return true;
        }
        return false;
    }


    public static boolean isCellDateFormatted(Cell cell) {
        if (cell == null)
            return false;
        boolean bDate = false;

        double d = cell.getNumberValue();
        if (DateUtil.isValidExcelDate(d)) {
            CellStyle style = cell.getCellStyle();
            if (style == null)
                return false;
            int i = style.getNumberFormatID();
            String f = style.getFormatCode();
            bDate = isADateFormat(i, f);
        }
        return bDate;
    }


    public static boolean isCellInternalDateFormatted(ICell cell) {
        if (cell == null)
            return false;
        boolean bDate = false;

        double d = cell.getNumericCellValue();
        if (DateUtil.isValidExcelDate(d)) {
            ICellStyle style = cell.getCellStyle();
            int i = style.getDataFormat();
            bDate = isInternalDateFormat(i);
        }
        return bDate;
    }



    public static boolean isValidExcelDate(double value) {
        return (value > -Double.MIN_VALUE);
    }


    protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
        return cal.get(Calendar.DAY_OF_YEAR)
                + daysInPriorYears(cal.get(Calendar.YEAR), use1904windowing);
    }



    private static int daysInPriorYears(int yr, boolean use1904windowing) {
        if ((!use1904windowing && yr < 1900) || (use1904windowing && yr < 1900)) {
            throw new IllegalArgumentException("'year' must be 1900 or greater");
        }

        int yr1 = yr - 1;
        int leapDays = yr1 / 4
                - yr1 / 100
                + yr1 / 400
                - 460;

        return 365 * (yr - (use1904windowing ? 1904 : 1900)) + leapDays;
    }


    private static Calendar dayStart(final Calendar cal) {
        cal.get(Calendar.HOUR_OF_DAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.get(Calendar.HOUR_OF_DAY);
        return cal;
    }


    public static double convertTime(String timeStr) {
        try {
            return convertTimeInternal(timeStr);
        } catch (FormatException e) {
            String msg = "Bad time format '" + timeStr + "' expected 'HH:MM' or 'HH:MM:SS' - "
                    + e.getMessage();
            throw new IllegalArgumentException(msg);
        }
    }

    private static double convertTimeInternal(String timeStr) throws FormatException {
        int len = timeStr.length();
        if (len < 4 || len > 8) {
            throw new FormatException("Bad length");
        }
        String[] parts = TIME_SEPARATOR_PATTERN.split(timeStr);

        String secStr;
        switch (parts.length) {
            case 2:
                secStr = "00";
                break;
            case 3:
                secStr = parts[2];
                break;
            default:
                throw new FormatException("Expected 2 or 3 fields but got (" + parts.length + ")");
        }
        String hourStr = parts[0];
        String minStr = parts[1];
        int hours = parseInt(hourStr, "hour", HOURS_PER_DAY);
        int minutes = parseInt(minStr, "minute", MINUTES_PER_HOUR);
        int seconds = parseInt(secStr, "second", SECONDS_PER_MINUTE);

        double totalSeconds = seconds + (minutes + (hours) * 60) * 60;
        return totalSeconds / (SECONDS_PER_DAY);
    }


    public static Date parseYYYYMMDDDate(String dateStr) {
        try {
            return parseYYYYMMDDDateInternal(dateStr);
        } catch (FormatException e) {
            String msg = "Bad time format " + dateStr + " expected 'YYYY/MM/DD' - "
                    + e.getMessage();
            throw new IllegalArgumentException(msg);
        }
    }

    private static Date parseYYYYMMDDDateInternal(String timeStr) throws FormatException {
        if (timeStr.length() != 10) {
            throw new FormatException("Bad length");
        }

        String yearStr = timeStr.substring(0, 4);
        String monthStr = timeStr.substring(5, 7);
        String dayStr = timeStr.substring(8, 10);
        int year = parseInt(yearStr, "year", Short.MIN_VALUE, Short.MAX_VALUE);
        int month = parseInt(monthStr, "month", 1, 12);
        int day = parseInt(dayStr, "day", 1, 31);

        Calendar cal = new GregorianCalendar(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static int parseInt(String strVal, String fieldName, int rangeMax)
            throws FormatException {
        return parseInt(strVal, fieldName, 0, rangeMax - 1);
    }

    private static int parseInt(String strVal, String fieldName, int lowerLimit, int upperLimit)
            throws FormatException {
        int result;
        try {
            result = Integer.parseInt(strVal);
        } catch (NumberFormatException e) {
            throw new FormatException("Bad int format '" + strVal + "' for " + fieldName + " field");
        }
        if (result < lowerLimit || result > upperLimit) {
            throw new FormatException(fieldName + " value (" + result
                    + ") is outside the allowable range(0.." + upperLimit + ")");
        }
        return result;
    }

    private static final class FormatException extends Exception {
        public FormatException(String msg) {
            super(msg);
        }
    }
}
