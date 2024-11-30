

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ss.usermodel.AutoFilter;


public final class HSSFAutoFilter implements AutoFilter {
    private HSSFSheet _sheet;

    HSSFAutoFilter(HSSFSheet sheet) {
        _sheet = sheet;
    }
}
