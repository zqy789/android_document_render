

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianInput;


public final class AreaNPtg extends Area2DPtgBase {
    public final static short sid = 0x2D;

    public AreaNPtg(LittleEndianInput in) {
        super(in);
    }

    protected byte getSid() {
        return sid;
    }
}
