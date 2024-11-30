
package com.document.render.office.fc.hslf.record;

import androidx.annotation.Keep;


public class TimeCommandBehaviorContainer extends PositionDependentRecordContainer {
    @Keep
    public static long RECORD_ID = 0xF132;
    private byte[] _header;


    protected TimeCommandBehaviorContainer(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);
    }


    public long getRecordType() {
        return RECORD_ID;
    }

}
