

package com.document.render.office.fc.hssf.record.pivottable;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public final class DataItemRecord extends StandardRecord {
    public static final short sid = 0x00C5;

    private int isxvdData;
    private int iiftab;
    private int df;
    private int isxvd;
    private int isxvi;
    private int ifmt;
    private String name;

    public DataItemRecord(RecordInputStream in) {
        isxvdData = in.readUShort();
        iiftab = in.readUShort();
        df = in.readUShort();
        isxvd = in.readUShort();
        isxvi = in.readUShort();
        ifmt = in.readUShort();

        name = in.readString();
    }

    @Override
    protected void serialize(LittleEndianOutput out) {

        out.writeShort(isxvdData);
        out.writeShort(iiftab);
        out.writeShort(df);
        out.writeShort(isxvd);
        out.writeShort(isxvi);
        out.writeShort(ifmt);

        StringUtil.writeUnicodeString(out, name);
    }

    @Override
    protected int getDataSize() {
        return 2 + 2 + 2 + 2 + 2 + 2 + StringUtil.getEncodedSize(name);
    }

    @Override
    public short getSid() {
        return sid;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[SXDI]\n");
        buffer.append("  .isxvdData = ").append(HexDump.shortToHex(isxvdData)).append("\n");
        buffer.append("  .iiftab = ").append(HexDump.shortToHex(iiftab)).append("\n");
        buffer.append("  .df = ").append(HexDump.shortToHex(df)).append("\n");
        buffer.append("  .isxvd = ").append(HexDump.shortToHex(isxvd)).append("\n");
        buffer.append("  .isxvi = ").append(HexDump.shortToHex(isxvi)).append("\n");
        buffer.append("  .ifmt = ").append(HexDump.shortToHex(ifmt)).append("\n");
        buffer.append("[/SXDI]\n");
        return buffer.toString();
    }
}
