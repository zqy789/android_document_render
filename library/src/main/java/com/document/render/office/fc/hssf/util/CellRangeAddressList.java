

package com.document.render.office.fc.hssf.util;

import com.document.render.office.fc.hssf.record.RecordInputStream;


public class CellRangeAddressList extends com.document.render.office.fc.ss.util.CellRangeAddressList {
    public CellRangeAddressList(int firstRow, int lastRow, int firstCol, int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }

    public CellRangeAddressList() {
        super();
    }


    public CellRangeAddressList(RecordInputStream in) {
        super();
        int nItems = in.readUShort();

        for (int k = 0; k < nItems; k++) {
            _list.add(new CellRangeAddress(in));
        }
    }
}
