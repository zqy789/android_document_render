

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class UserSViewEnd extends StandardRecord {

    @Keep
    public final static short sid = 0x01AB;
    private byte[] _rawData;

    public UserSViewEnd(byte[] data) {
        _rawData = data;
    }


    public UserSViewEnd(RecordInputStream in) {
        _rawData = in.readRemainder();
    }


    public void serialize(LittleEndianOutput out) {
        out.write(_rawData);
    }

    protected int getDataSize() {
        return _rawData.length;
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[").append("USERSVIEWEND").append("] (0x");
        sb.append(Integer.toHexString(sid).toUpperCase() + ")\n");
        sb.append("  rawData=").append(HexDump.toHex(_rawData)).append("\n");
        sb.append("[/").append("USERSVIEWEND").append("]\n");
        return sb.toString();
    }


    public Object clone() {
        return cloneViaReserialise();
    }


}
