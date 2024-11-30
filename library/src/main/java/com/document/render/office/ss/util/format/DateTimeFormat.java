
package com.document.render.office.ss.util.format;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;








public class DateTimeFormat {

    public final static int FULL = 0;

    public final static int LONG = 1;

    public final static int MEDIUM = 2;

    public final static int SHORT = 3;

    public final static int ERA_FIELD = 0;

    public final static int YEAR_FIELD = 1;

    public final static int MONTH_FIELD = 2;

    public final static int DATE_FIELD = 3;

    public final static int HOUR_OF_DAY1_FIELD = 4;

    public final static int HOUR_OF_DAY0_FIELD = 5;

    public final static int MINUTE_FIELD = 6;

    public final static int SECOND_FIELD = 7;

    public final static int MILLISECOND_FIELD = 8;

    public final static int DAY_OF_YEAR_FIELD = 10;

    public final static int DAY_OF_WEEK_IN_MONTH_FIELD = 11;

    public final static int WEEK_OF_YEAR_FIELD = 12;




    public final static int WEEK_OF_MONTH_FIELD = 13;

    public final static int DAY_OF_WEEK_FIELD = 14;

    public final static int HOUR1_FIELD = 15;

    public final static int HOUR0_FIELD = 16;

    public final static int TIMEZONE_FIELD = 17;
    static final String patternChars = "GyMdkHmsSEDFwWahKzZ";
    private static final String datePatternChars = "GyMdEDFwWazZ";
    private static final String timePatternChars = "HhsSkK";
    protected Calendar calendar;






    protected NumberFormat numberFormat;
    private String pattern;
    private DateTimeFormatSymbols dateTimeFormatData;
    private boolean ampm = false;


    public DateTimeFormat(String pattern) {
        this(pattern, Locale.getDefault());
    }

    private DateTimeFormat(Locale locale) {
        numberFormat = NumberFormat.getInstance(locale);
        numberFormat.setParseIntegerOnly(true);
        numberFormat.setGroupingUsed(false);
        calendar = new GregorianCalendar(locale);
        calendar.add(Calendar.YEAR, -80);
    }

    public DateTimeFormat(String template, Locale locale) {
        this(locale);

        template = adjust(template);

        validatePattern(template);






        pattern = template;

        dateTimeFormatData = new DateTimeFormatSymbols(locale);
    }

    public static boolean isDateTimeFormat(String format) {


        format = format.replace("E+", "");

        int len = patternChars.length();
        boolean value = false;
        int index = 0;
        while (index < len) {
            if (format.indexOf(patternChars.charAt(index)) > -1) {
                value = true;
                break;
            }
            index++;
        }
        return value;
    }

    private String adjust(String template) {
        if (template.contains("AM/PM") || template.contains("上午/下午")) {
            template = template.replace("AM/PM", "").replace("上午/下午", "");
            ampm = true;
        }

        boolean date = isDate(template);
        boolean time = isTime(template);

        if (time && date) {


            int index = template.indexOf("mmm");
            char[] chars = null;
            while (index > -1) {
                chars = template.toCharArray();
                int first = index;
                int last = index + 3;
                while (template.charAt(last) == 'm') {
                    last++;
                }
                for (index = first; index < last; index++) {
                    chars[index] = 'M';
                }
                template = String.valueOf(chars);
                index = template.indexOf("mmm");
            }


            chars = template.toCharArray();
            List<Integer> indexList = new ArrayList<Integer>();
            index = template.indexOf('m');
            if (index > -1) {

            }
        } else if (date) {
            template = template.replace('m', 'M');
        } else {
            if (!ampm) {
                template = template.replace('h', 'k');
            }
        }

        return template;
    }




    private void validateFormat(char format) {
        int index = patternChars.indexOf(format);
        if (index == -1) {

            throw new IllegalArgumentException("invalidate char");
        }
    }



    private void validatePattern(String template) {
        boolean quote = false;
        int next, last = -1, count = 0;

        final int patternLength = template.length();
        for (int i = 0; i < patternLength; i++) {
            next = (template.charAt(i));
            if (next == '\'') {
                if (count > 0) {
                    validateFormat((char) last);
                    count = 0;
                }
                if (last == next) {
                    last = -1;
                } else {
                    last = next;
                }
                quote = !quote;
                continue;
            }
            if (!quote
                    && (last == next || (next >= 'a' && next <= 'z') || (next >= 'A' && next <= 'Z'))) {
                if (last == next) {
                    count++;
                } else {
                    if (count > 0) {
                        validateFormat((char) last);
                    }
                    last = next;
                    count = 1;
                }
            } else {
                if (count > 0) {
                    validateFormat((char) last);
                    count = 0;
                }
                last = -1;
            }
        }
        if (count > 0) {
            validateFormat((char) last);
        }

        if (quote) {

            throw new IllegalArgumentException("invalidate pattern");
        }

    }




















    public String format(Date date) {
        return formatImpl(date, new StringBuffer()).toString();
    }


    private StringBuffer formatImpl(Date date, StringBuffer buffer) {

        boolean quote = false;
        int next, last = -1, count = 0;
        calendar.setTime(date);

        final int patternLength = pattern.length();
        for (int i = 0; i < patternLength; i++) {
            next = (pattern.charAt(i));
            if (next == '\'') {
                if (count > 0) {
                    append(buffer, (char) last, count);
                    count = 0;
                }
                if (last == next) {
                    buffer.append('\'');
                    last = -1;
                } else {
                    last = next;
                }
                quote = !quote;
                continue;
            }
            if (!quote
                    && (last == next || (next >= 'a' && next <= 'z') || (next >= 'A' && next <= 'Z'))) {
                if (last == next) {
                    count++;
                } else {
                    if (count > 0) {
                        append(buffer, (char) last, count);
                    }
                    last = next;
                    count = 1;
                }
            } else {
                if (count > 0) {
                    append(buffer, (char) last, count);
                    count = 0;
                }
                last = -1;
                buffer.append((char) next);
            }
        }
        if (count > 0) {
            append(buffer, (char) last, count);
        }

        if (ampm) {
            String[] strAMPM = dateTimeFormatData.formatData.getAmPmStrings();
            buffer.append(strAMPM[calendar.get(Calendar.AM_PM)]);
        }

        return buffer;
    }

    private void append(StringBuffer buffer, char format, int count) {
        int field = -1;
        int index = patternChars.indexOf(format);
        if (index == -1) {

            throw new IllegalArgumentException("invalidate char");
        }

        switch (index) {
            case ERA_FIELD:
                String strERAS[] = dateTimeFormatData.formatData.getEras();
                buffer.append(strERAS[calendar.get(Calendar.ERA)]);
                break;
            case YEAR_FIELD:
                int year = calendar.get(Calendar.YEAR);





                if (count == 2) {
                    appendNumber(buffer, 2, year %= 100);
                } else {
                    appendNumber(buffer, count, year);
                }

                break;
            case MONTH_FIELD:
                int month = calendar.get(Calendar.MONTH);
                if (count <= 2) {
                    appendNumber(buffer, count, month + 1);
                } else if (count == 3) {
                    String[] strMonths = dateTimeFormatData.formatData.getShortMonths();
                    buffer.append(strMonths[month]);
                } else {
                    String[] strMonths = dateTimeFormatData.formatData.getMonths();
                    buffer.append(strMonths[month]);
                }
                break;
            case DATE_FIELD:
                int weekday = calendar.get(Calendar.DAY_OF_WEEK);
                if (weekday < dateTimeFormatData.stdShortWeekdays.length) {
                    if (count == 3) {
                        buffer.append(dateTimeFormatData.stdShortWeekdays[weekday]);
                    } else if (count > 3) {
                        buffer.append(dateTimeFormatData.stdWeekdays[weekday]);
                    } else {
                        field = Calendar.DATE;
                    }
                }


                break;
            case HOUR_OF_DAY1_FIELD:
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                appendNumber(buffer, count, hour == 0 ? 24 : hour);
                break;
            case HOUR_OF_DAY0_FIELD:
                if (ampm) {
                    hour = calendar.get(Calendar.HOUR);
                    appendNumber(buffer, count, hour == 0 ? 12 : hour);
                } else {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    appendNumber(buffer, count, hour);
                }

                break;
            case MINUTE_FIELD:
                if (count == 3 || count > 5) {
                    buffer.append(dateTimeFormatData.stdShortMonths[calendar.get(Calendar.MONTH)]);
                } else if (count == 4) {
                    buffer.append(dateTimeFormatData.stdMonths[calendar.get(Calendar.MONTH)]);
                } else if (count == 5) {
                    buffer.append(dateTimeFormatData.stdShortestMonths[calendar.get(Calendar.MONTH)]);
                } else {
                    field = Calendar.MINUTE;
                }
                break;
            case SECOND_FIELD:
                field = Calendar.SECOND;
                break;
            case MILLISECOND_FIELD:
                int value = calendar.get(Calendar.MILLISECOND);
                appendNumber(buffer, count, value);
                break;
            case DAY_OF_YEAR_FIELD:
                field = Calendar.DAY_OF_YEAR;
                break;
            case DAY_OF_WEEK_IN_MONTH_FIELD:
                field = Calendar.DAY_OF_WEEK_IN_MONTH;
                break;
            case WEEK_OF_YEAR_FIELD:
                field = Calendar.WEEK_OF_YEAR;
                break;
            case WEEK_OF_MONTH_FIELD:
                field = Calendar.WEEK_OF_MONTH;
                break;

            case DAY_OF_WEEK_FIELD:
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                if (count == 3) {
                    String[] strWeekdays = dateTimeFormatData.formatData.getShortWeekdays();
                    buffer.append(strWeekdays[day]);
                } else if (count > 3) {
                    String[] strWeekdays = dateTimeFormatData.formatData.getWeekdays();
                    buffer.append(strWeekdays[day]);
                }
                break;
            case HOUR1_FIELD:
                if (ampm) {
                    hour = calendar.get(Calendar.HOUR);
                    appendNumber(buffer, count, hour == 0 ? 12 : hour);
                } else {
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    appendNumber(buffer, count, hour);
                }

                break;
            case HOUR0_FIELD:
                field = Calendar.HOUR;
                break;
            case TIMEZONE_FIELD:
                appendTimeZone(buffer, count, true);
                break;

            case (TIMEZONE_FIELD + 1):
                appendNumericTimeZone(buffer, false);
                break;

        }
        if (field != -1) {
            appendNumber(buffer, count, calendar.get(field));
        }
    }


    private void appendTimeZone(StringBuffer buffer, int count, boolean generalTimeZone) {

        if (generalTimeZone) {
            TimeZone tz = calendar.getTimeZone();
            boolean daylight = (calendar.get(Calendar.DST_OFFSET) != 0);
            int style = count < 4 ? TimeZone.SHORT : TimeZone.LONG;

            {
                buffer.append(tz.getDisplayName(daylight, style, Locale.getDefault()));
                return;
            }
            // We can't call TimeZone.getDisplayName() because it would not use
            // the custom DateFormatSymbols of this SimpleDateFormat.
///////////// String custom = Resources.lookupDisplayTimeZone(formatData.zoneStrings, tz.getID(), daylight, style);
/*            if (custom != null)
            {
                buffer.append(custom);
                return;
            }*/////////////////////////////////////////////////////////////////////////////////////
        }
        // We didn't find what we were looking for, so default to a numeric time zone.
        appendNumericTimeZone(buffer, generalTimeZone);
        // END android-changed
    }



    private void appendNumericTimeZone(StringBuffer buffer, boolean generalTimeZone) {
        int offset = calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET);
        char sign = '+';
        if (offset < 0) {
            sign = '-';
            offset = -offset;
        }
        if (generalTimeZone) {
            buffer.append("GMT");
        }
        buffer.append(sign);
        appendNumber(buffer, 2, offset / 3600000);
        if (generalTimeZone) {
            buffer.append(':');
        }
        appendNumber(buffer, 2, (offset % 3600000) / 60000);
    }

    private void appendNumber(StringBuffer buffer, int count, int value) {
        int minimumIntegerDigits = numberFormat.getMinimumIntegerDigits();
        numberFormat.setMinimumIntegerDigits(count);
        numberFormat.format(Integer.valueOf(value), buffer, new FieldPosition(0));
        numberFormat.setMinimumIntegerDigits(minimumIntegerDigits);
    }


    private boolean isDate(String formatString) {
        String subString = formatString.replace("AM", "");
        subString = formatString.replace("PM", "");

        int len = datePatternChars.length();
        boolean value = false;
        int index = 0;
        while (index < len) {
            if (subString.indexOf(datePatternChars.charAt(index)) > -1) {
                value = true;
                break;
            }
            index++;
        }
        return value;
    }


    private boolean isTime(String formatString) {
        int len = timePatternChars.length();
        boolean value = false;
        int index = 0;
        while (index < len) {
            if (formatString.indexOf(timePatternChars.charAt(index)) > -1) {
                value = true;
                break;
            }
            index++;
        }
        return value;
    }


    public void dispose() {
        calendar = null;
        numberFormat = null;
        dateTimeFormatData.dispose();
        dateTimeFormatData = null;
    }
}
