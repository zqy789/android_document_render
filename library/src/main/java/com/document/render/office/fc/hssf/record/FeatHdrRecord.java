

package com.document.render.office.fc.hssf.record;


import com.document.render.office.fc.hssf.record.common.FtrHeader;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FeatHdrRecord extends StandardRecord {

    public static final int SHAREDFEATURES_ISFPROTECTION = 0x02;

    public static final int SHAREDFEATURES_ISFFEC2 = 0x03;

    public static final int SHAREDFEATURES_ISFFACTOID = 0x04;

    public static final int SHAREDFEATURES_ISFLIST = 0x05;


    public final static short sid = 0x0867;

    private FtrHeader futureHeader;
    private int isf_sharedFeatureType;
    private byte reserved;

    private long cbHdrData;

    private byte[] rgbHdrData;

    public FeatHdrRecord() {
        futureHeader = new FtrHeader();
        futureHeader.setRecordType(sid);
    }

    public FeatHdrRecord(RecordInputStream in) {
        futureHeader = new FtrHeader(in);

        isf_sharedFeatureType = in.readShort();
        reserved = in.readByte();
        cbHdrData = in.readInt();

        rgbHdrData = in.readRemainder();
    }

    public short getSid() {
        return sid;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FEATURE HEADER]\n");



        buffer.append("[/FEATURE HEADER]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        futureHeader.serialize(out);

        out.writeShort(isf_sharedFeatureType);
        out.writeByte(reserved);
        out.writeInt((int) cbHdrData);
        out.write(rgbHdrData);
    }

    protected int getDataSize() {
        return 12 + 2 + 1 + 4 + rgbHdrData.length;
    }


    public Object clone() {
        return cloneViaReserialise();
    }


}
