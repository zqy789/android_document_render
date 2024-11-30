

package com.document.render.office.fc.hssf.record.common;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class FtrHeader {

    private short recordType;

    private short grbitFrt;

    private byte[] reserved;

    public FtrHeader() {
        reserved = new byte[8];
    }

    public FtrHeader(RecordInputStream in) {
        recordType = in.readShort();
        grbitFrt = in.readShort();

        reserved = new byte[8];
        in.read(reserved, 0, 8);
    }

    public static int getDataSize() {
        return 12;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" [FUTURE HEADER]\n");
        buffer.append("   Type " + recordType);
        buffer.append("   Flags " + grbitFrt);
        buffer.append(" [/FUTURE HEADER]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(recordType);
        out.writeShort(grbitFrt);
        out.write(reserved);
    }

    public short getRecordType() {
        return recordType;
    }

    public void setRecordType(short recordType) {
        this.recordType = recordType;
    }

    public short getGrbitFrt() {
        return grbitFrt;
    }

    public void setGrbitFrt(short grbitFrt) {
        this.grbitFrt = grbitFrt;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public void setReserved(byte[] reserved) {
        this.reserved = reserved;
    }
}
