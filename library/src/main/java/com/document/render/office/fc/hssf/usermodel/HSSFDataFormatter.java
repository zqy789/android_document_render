

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ss.usermodel.DataFormatter;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;



public final class HSSFDataFormatter extends DataFormatter {


    public HSSFDataFormatter(Locale locale) {
        super(locale);
    }


    public HSSFDataFormatter() {
        this(Locale.getDefault());
    }

}
