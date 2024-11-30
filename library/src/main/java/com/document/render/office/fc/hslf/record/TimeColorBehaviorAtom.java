
package com.document.render.office.fc.hslf.record;

import java.util.Hashtable;


public class TimeColorBehaviorAtom extends PositionDependentRecordAtom {
    private static long _type = 0xF135;
    private byte[] _header;

    private int flag;


    protected TimeColorBehaviorAtom(byte[] source, int start, int len) {
        if (len < 60) {
            len = 60;
        }

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

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
