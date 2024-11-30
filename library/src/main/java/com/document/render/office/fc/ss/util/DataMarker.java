

package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.ss.usermodel.Sheet;


public class DataMarker {

    private Sheet sheet;
    private HSSFCellRangeAddress range;


    public DataMarker(Sheet sheet, HSSFCellRangeAddress range) {
        this.sheet = sheet;
        this.range = range;
    }


    public Sheet getSheet() {
        return sheet;
    }


    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }


    public HSSFCellRangeAddress getRange() {
        return range;
    }


    public void setRange(HSSFCellRangeAddress range) {
        this.range = range;
    }


    public String formatAsString() {






        return null;
    }
}
