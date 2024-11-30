

package com.document.render.office.fc.hssf.util;

import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.SelectionRecord;


public class CellRangeAddress extends com.document.render.office.fc.ss.util.HSSFCellRangeAddress {

    public CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }

    public CellRangeAddress(RecordInputStream in) {
        super(in);
    }
}
