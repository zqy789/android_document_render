
package com.document.render.office.fc.hslf.record;

import androidx.annotation.Keep;


public class TimeColorBehaviorContainer extends PositionDependentRecordContainer {
    @Keep
    public static long RECORD_ID = 0xF12C;
    private byte[] _header;

    private TimeColorBehaviorAtom colorBehaviorAtom;


    protected TimeColorBehaviorContainer(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        colorBehaviorAtom = new TimeColorBehaviorAtom(source, start + 8, 60);

        _children = Record.findChildRecords(source, start + 8, len - 8);
    }


    public long getRecordType() {
        return RECORD_ID;
    }

}
