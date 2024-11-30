

package com.document.render.office.fc.hssf.formula.ptg;


import com.document.render.office.fc.ss.util.AreaReference;
import com.document.render.office.fc.util.LittleEndianInput;



public final class AreaPtg extends Area2DPtgBase {
    public final static short sid = 0x25;

    public AreaPtg(int firstRow, int lastRow, int firstColumn, int lastColumn, boolean firstRowRelative, boolean lastRowRelative, boolean firstColRelative, boolean lastColRelative) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
    }

    public AreaPtg(LittleEndianInput in) {
        super(in);
    }

    public AreaPtg(String arearef) {
        super(new AreaReference(arearef));
    }

    public AreaPtg(AreaReference areaRef) {
        super(areaRef);
    }

    protected byte getSid() {
        return sid;
    }
}
