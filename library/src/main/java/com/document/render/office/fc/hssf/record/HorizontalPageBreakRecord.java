

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import java.util.Iterator;


public final class HorizontalPageBreakRecord extends PageBreakRecord {

    @Keep
    public static final short sid = 0x001B;


    public HorizontalPageBreakRecord() {

    }


    public HorizontalPageBreakRecord(RecordInputStream in) {
        super(in);
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        PageBreakRecord result = new HorizontalPageBreakRecord();
        Iterator iterator = getBreaksIterator();
        while (iterator.hasNext()) {
            Break original = (Break) iterator.next();
            result.addBreak(original.main, original.subFrom, original.subTo);
        }
        return result;
    }
}
