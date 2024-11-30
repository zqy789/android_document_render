
package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.util.Hashtable;


public class TimeIterateDataAtom extends PositionDependentRecordAtom {
    private static long _type = 0xF140;
    private byte[] _header;

    private int iterateInterval;


    private int iterateType;


    private int iterateDirection;


    private int iterateIntervalType;

    private boolean fIterateDirectionPropertyUsed;
    private boolean fIterateTypePropertyUsed;
    private boolean fIterateIntervalPropertyUsed;
    private boolean fIterateIntervalTypePropertyUsed;

    private byte[] reserved;


    protected TimeIterateDataAtom(byte[] source, int start, int len) {
        if (len < 28) {
            len = 28;
        }

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        iterateInterval = LittleEndian.getInt(source, start + 8);
        iterateType = LittleEndian.getInt(source, start + 12);
        iterateDirection = LittleEndian.getInt(source, start + 16);
        iterateIntervalType = LittleEndian.getInt(source, start + 20);

    }


    public long getRecordType() {
        return _type;
    }


    public void updateOtherRecordReferences(Hashtable<Integer, Integer> oldToNewReferencesLookup) {

    }


    public void dispose() {
        _header = null;
    }
}
