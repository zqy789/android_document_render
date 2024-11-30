

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianInput;


public final class RefNPtg extends Ref2DPtgBase {
    public final static byte sid = 0x2C;

    public RefNPtg(LittleEndianInput in) {
        super(in);
    }

    protected byte getSid() {
        return sid;
    }
}
