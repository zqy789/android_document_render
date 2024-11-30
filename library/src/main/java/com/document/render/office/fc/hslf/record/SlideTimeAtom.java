
package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.util.Hashtable;


public class SlideTimeAtom extends PositionDependentRecordAtom {
    private static long _type = 12011;
    private byte[] _header;

    private long fileTime;


    protected SlideTimeAtom(byte[] source, int start, int len) {

        if (len < 16) {
            len = 16;
        }


        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        fileTime = LittleEndian.getLong(source, start + 8);
    }


    public long getSlideCreateTime() {
        return fileTime;
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
