

package com.document.render.office.fc.hssf.formula.atp;

import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.ss.util.DateUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;



final class YearFracCalculator {

    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
    private static final int MS_PER_HOUR = 60 * 60 * 1000;
    private static final int MS_PER_DAY = 24 * MS_PER_HOUR;
    private static final int DAYS_PER_NORMAL_YEAR = 365;
    private static final int DAYS_PER_LEAP_YEAR = DAYS_PER_NORMAL_YEAR + 1;


    private static final int LONG_MONTH_LEN = 31;

    private static final int SHORT_MONTH_LEN = 30;
    private static final int SHORT_FEB_LEN = 28;
    private static final int LONG_FEB_LEN = SHORT_FEB_LEN + 1;

    private YearFracCalculator() {

    }


    public static double calculate(double pStartDateVal, double pEndDateVal, int basis) throws EvaluationException {

        if (basis < 0 || basis >= 5) {

            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }




        int startDateVal = (int) Math.floor(pStartDateVal);
        int endDateVal = (int) Math.floor(pEndDateVal);
        if (startDateVal == endDateVal) {

            return 0;
        }

        if (startDateVal > endDateVal) {
            int temp = startDateVal;
            startDateVal = endDateVal;
            endDateVal = temp;
        }

        switch (basis) {
            case 0:
                return basis0(startDateVal, endDateVal);
            case 1:
                return basis1(startDateVal, endDateVal);
            case 2:
                return basis2(startDateVal, endDateVal);
            case 3:
                return basis3(startDateVal, endDateVal);
            case 4:
                return basis4(startDateVal, endDateVal);
        }
        throw new IllegalStateException("cannot happen");
    }



    public static double basis0(int startDateVal, int endDateVal) {
        SimpleDate startDate = createDate(startDateVal);
        SimpleDate endDate = createDate(endDateVal);
        int date1day = startDate.day;
        int date2day = endDate.day;


        if (date1day == LONG_MONTH_LEN && date2day == LONG_MONTH_LEN) {
            date1day = SHORT_MONTH_LEN;
            date2day = SHORT_MONTH_LEN;
        } else if (date1day == LONG_MONTH_LEN) {
            date1day = SHORT_MONTH_LEN;
        } else if (date1day == SHORT_MONTH_LEN && date2day == LONG_MONTH_LEN) {
            date2day = SHORT_MONTH_LEN;


        } else if (startDate.month == 2 && isLastDayOfMonth(startDate)) {

            date1day = SHORT_MONTH_LEN;
            if (endDate.month == 2 && isLastDayOfMonth(endDate)) {

                date2day = SHORT_MONTH_LEN;
            }
        }
        return calculateAdjusted(startDate, endDate, date1day, date2day);
    }


    public static double basis1(int startDateVal, int endDateVal) {
        SimpleDate startDate = createDate(startDateVal);
        SimpleDate endDate = createDate(endDateVal);
        double yearLength;
        if (isGreaterThanOneYear(startDate, endDate)) {
            yearLength = averageYearLength(startDate.year, endDate.year);
        } else if (shouldCountFeb29(startDate, endDate)) {
            yearLength = DAYS_PER_LEAP_YEAR;
        } else {
            yearLength = DAYS_PER_NORMAL_YEAR;
        }
        return dateDiff(startDate.tsMilliseconds, endDate.tsMilliseconds) / yearLength;
    }


    public static double basis2(int startDateVal, int endDateVal) {
        return (endDateVal - startDateVal) / 360.0;
    }


    public static double basis3(double startDateVal, double endDateVal) {
        return (endDateVal - startDateVal) / 365.0;
    }


    public static double basis4(int startDateVal, int endDateVal) {
        SimpleDate startDate = createDate(startDateVal);
        SimpleDate endDate = createDate(endDateVal);
        int date1day = startDate.day;
        int date2day = endDate.day;



        if (date1day == LONG_MONTH_LEN) {
            date1day = SHORT_MONTH_LEN;
        }
        if (date2day == LONG_MONTH_LEN) {
            date2day = SHORT_MONTH_LEN;
        }

        return calculateAdjusted(startDate, endDate, date1day, date2day);
    }


    private static double calculateAdjusted(SimpleDate startDate, SimpleDate endDate, int date1day,
                                            int date2day) {
        double dayCount
                = (endDate.year - startDate.year) * 360
                + (endDate.month - startDate.month) * SHORT_MONTH_LEN
                + (date2day - date1day) * 1;
        return dayCount / 360;
    }

    private static boolean isLastDayOfMonth(SimpleDate date) {
        if (date.day < SHORT_FEB_LEN) {
            return false;
        }
        return date.day == getLastDayOfMonth(date);
    }

    private static int getLastDayOfMonth(SimpleDate date) {
        switch (date.month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return LONG_MONTH_LEN;
            case 4:
            case 6:
            case 9:
            case 11:
                return SHORT_MONTH_LEN;
        }
        if (isLeapYear(date.year)) {
            return LONG_FEB_LEN;
        }
        return SHORT_FEB_LEN;
    }


    private static boolean shouldCountFeb29(SimpleDate start, SimpleDate end) {
        boolean startIsLeapYear = isLeapYear(start.year);
        if (startIsLeapYear && start.year == end.year) {

            return true;
        }

        boolean endIsLeapYear = isLeapYear(end.year);
        if (!startIsLeapYear && !endIsLeapYear) {
            return false;
        }
        if (startIsLeapYear) {
            switch (start.month) {
                case SimpleDate.JANUARY:
                case SimpleDate.FEBRUARY:
                    return true;
            }
            return false;
        }
        if (endIsLeapYear) {
            switch (end.month) {
                case SimpleDate.JANUARY:
                    return false;
                case SimpleDate.FEBRUARY:
                    break;
                default:
                    return true;
            }
            return end.day == LONG_FEB_LEN;
        }
        return false;
    }


    private static int dateDiff(long startDateMS, long endDateMS) {
        long msDiff = endDateMS - startDateMS;


        int remainderHours = (int) ((msDiff % MS_PER_DAY) / MS_PER_HOUR);
        switch (remainderHours) {
            case 0:
                break;
            case 1:
            case 23:

            default:
                throw new RuntimeException("Unexpected date diff between " + startDateMS + " and " + endDateMS);

        }
        return (int) (0.5 + ((double) msDiff / MS_PER_DAY));
    }

    private static double averageYearLength(int startYear, int endYear) {
        int dayCount = 0;
        for (int i = startYear; i <= endYear; i++) {
            dayCount += DAYS_PER_NORMAL_YEAR;
            if (isLeapYear(i)) {
                dayCount++;
            }
        }
        double numberOfYears = endYear - startYear + 1;
        return dayCount / numberOfYears;
    }

    private static boolean isLeapYear(int i) {

        if (i % 4 != 0) {
            return false;
        }

        if (i % 400 == 0) {
            return true;
        }

        if (i % 100 == 0) {
            return false;
        }
        return true;
    }

    private static boolean isGreaterThanOneYear(SimpleDate start, SimpleDate end) {
        if (start.year == end.year) {
            return false;
        }
        if (start.year + 1 != end.year) {
            return true;
        }

        if (start.month > end.month) {
            return false;
        }
        if (start.month < end.month) {
            return true;
        }

        return start.day < end.day;
    }

    private static SimpleDate createDate(int dayCount) {
        GregorianCalendar calendar = new GregorianCalendar(UTC_TIME_ZONE);
        DateUtil.setCalendar(calendar, dayCount, 0, false);
        return new SimpleDate(calendar);
    }

    private static final class SimpleDate {

        public static final int JANUARY = 1;
        public static final int FEBRUARY = 2;

        public final int year;

        public final int month;

        public final int day;

        public long tsMilliseconds;

        public SimpleDate(Calendar cal) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            tsMilliseconds = cal.getTimeInMillis();
        }
    }
}
