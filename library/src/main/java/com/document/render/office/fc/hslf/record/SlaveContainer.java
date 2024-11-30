
package com.document.render.office.fc.hslf.record;


public class SlaveContainer extends PositionDependentRecordContainer {
    private static long _type = 0xF145;
    private byte[] _header;


    protected SlaveContainer(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);
    }


    public long getRecordType() {
        return _type;
    }
}

