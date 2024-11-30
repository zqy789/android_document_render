
package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.util.Hashtable;


public class TimeSequenceDataAtom extends PositionDependentRecordAtom {
    private static long _type = 0xF141;
    private byte[] _header;

    private int concurrency;


    private int nextAction;


    private int previousAction;

    private int reserved1;

    private boolean fConcurrencyPropertyUsed;
    private boolean fNextActionPropertyUsed;
    private boolean fPreviousActionPropertyUsed;

    private byte[] reserved2;


    protected TimeSequenceDataAtom(byte[] source, int start, int len) {
        if (len < 28) {
            len = 28;
        }

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        concurrency = LittleEndian.getInt(source, start + 8);
        nextAction = LittleEndian.getInt(source, start + 12);
        previousAction = LittleEndian.getInt(source, start + 16);
        reserved1 = LittleEndian.getInt(source, start + 20);

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
