

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class UserSViewBegin extends StandardRecord {

    @Keep
    public final static short sid = 0x01AA;
    private byte[] _rawData;

    public UserSViewBegin(byte[] data) {
        _rawData = data;
    }


    public UserSViewBegin(RecordInputStream in) {
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


    public byte[] getGuid() {
        byte[] guid = new byte[16];
        System.arraycopy(_rawData, 0, guid, 0, guid.length);
        return guid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[").append("USERSVIEWBEGIN").append("] (0x");
        sb.append(Integer.toHexString(sid).toUpperCase() + ")\n");
        sb.append("  rawData=").append(HexDump.toHex(_rawData)).append("\n");
        sb.append("[/").append("USERSVIEWBEGIN").append("]\n");
        return sb.toString();
    }


    public Object clone() {
        return cloneViaReserialise();
    }

}
