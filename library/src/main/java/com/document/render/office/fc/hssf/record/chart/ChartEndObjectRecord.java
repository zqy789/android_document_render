

package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class ChartEndObjectRecord extends StandardRecord {
    public static final short sid = 0x0855;

    private short rt;
    private short grbitFrt;
    private short iObjectKind;
    private byte[] reserved;

    public ChartEndObjectRecord(RecordInputStream in) {
        rt = in.readShort();
        grbitFrt = in.readShort();
        iObjectKind = in.readShort();




        reserved = new byte[6];
        if (in.available() == 0) {

        } else {

            in.readFully(reserved);
        }
    }

    @Override
    protected int getDataSize() {
        return 2 + 2 + 2 + 6;
    }

    @Override
    public short getSid() {
        return sid;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(rt);
        out.writeShort(grbitFrt);
        out.writeShort(iObjectKind);

        out.write(reserved);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[ENDOBJECT]\n");
        buffer.append("    .rt         =").append(HexDump.shortToHex(rt)).append('\n');
        buffer.append("    .grbitFrt   =").append(HexDump.shortToHex(grbitFrt)).append('\n');
        buffer.append("    .iObjectKind=").append(HexDump.shortToHex(iObjectKind)).append('\n');
        buffer.append("    .reserved   =").append(HexDump.toHex(reserved)).append('\n');
        buffer.append("[/ENDOBJECT]\n");
        return buffer.toString();
    }
}
