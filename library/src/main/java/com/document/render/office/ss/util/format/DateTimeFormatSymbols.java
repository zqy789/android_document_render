
package com.document.render.office.ss.util.format;

import java.text.DateFormatSymbols;
import java.util.Locale;


public class DateTimeFormatSymbols {
    public final String[] stdWeekdays =
            {
                    "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
            };

    public final String[] stdShortWeekdays =
            {
                    "", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
            };

    public final String[] stdMonths =
            {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December", ""
            };

    public final String[] stdShortMonths =
            {
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "July", "Aug", "Sep", "Oct", "Nov", "Dec", ""
            };

    public final String[] stdShortestMonths =
            {
                    "J", "F", "M", "A", "M", "J",
                    "J", "A", "S", "O", "N", "D"
            };
    public DateFormatSymbols formatData;

    public DateTimeFormatSymbols(Locale locale) {
        formatData = new DateFormatSymbols(locale);
    }


    public void dispose() {
        formatData = null;
    }
}
