

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class TabIdRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x013D;
    private static final short[] EMPTY_SHORT_ARRAY = {};

    public short[] _tabids;

    public TabIdRecord() {
        _tabids = EMPTY_SHORT_ARRAY;
    }

    public TabIdRecord(RecordInputStream in) {
        int nTabs = in.remaining() / 2;
        _tabids = new short[nTabs];
        for (int i = 0; i < _tabids.length; i++) {
            _tabids[i] = in.readShort();
        }
    }


    public void setTabIdArray(short[] array) {
        _tabids = array;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[TABID]\n");
        buffer.append("    .elements        = ").append(_tabids.length).append("\n");
        for (int i = 0; i < _tabids.length; i++) {
            buffer.append("    .element_").append(i).append(" = ").append(_tabids[i]).append("\n");
        }
        buffer.append("[/TABID]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        short[] tabids = _tabids;

        for (int i = 0; i < tabids.length; i++) {
            out.writeShort(tabids[i]);
        }
    }

    protected int getDataSize() {
        return _tabids.length * 2;
    }

    public short getSid() {
        return sid;
    }
}
