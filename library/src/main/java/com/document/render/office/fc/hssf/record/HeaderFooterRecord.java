

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;

import java.util.Arrays;


public final class HeaderFooterRecord extends StandardRecord {

    @Keep
    public final static short sid = 0x089C;
    private static final byte[] BLANK_GUID = new byte[16];
    private byte[] _rawData;

    public HeaderFooterRecord(byte[] data) {
        _rawData = data;
    }


    public HeaderFooterRecord(RecordInputStream in) {
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
        System.arraycopy(_rawData, 12, guid, 0, Math.min(guid.length, _rawData.length - 12));
        return guid;
    }


    public boolean isCurrentSheet() {
        return Arrays.equals(getGuid(), BLANK_GUID);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[").append("HEADERFOOTER").append("] (0x");
        sb.append(Integer.toHexString(sid).toUpperCase() + ")\n");
        sb.append("  rawData=").append(HexDump.toHex(_rawData)).append("\n");
        sb.append("[/").append("HEADERFOOTER").append("]\n");
        return sb.toString();
    }


    public Object clone() {
        return cloneViaReserialise();
    }


}
