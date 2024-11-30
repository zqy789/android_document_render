




package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.ss.util.DateUtil;

import java.util.Calendar;




public class HSSFDateUtil extends DateUtil {
    protected static int absoluteDay(Calendar cal, boolean use1904windowing) {
        return DateUtil.absoluteDay(cal, use1904windowing);
    }
}
