

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class LabelSSTRecord extends CellRecord {
    @Keep
    public final static short sid = 0xfd;
    private int field_4_sst_index;

    public LabelSSTRecord() {

    }

    public LabelSSTRecord(RecordInputStream in) {
        super(in);
        field_4_sst_index = in.readInt();
    }


    public int getSSTIndex() {
        return field_4_sst_index;
    }


    public void setSSTIndex(int index) {
        field_4_sst_index = index;
    }

    @Override
    protected String getRecordName() {
        return "LABELSST";
    }

    @Override
    protected void appendValueText(StringBuilder sb) {
        sb.append("  .sstIndex = ");
        sb.append(HexDump.shortToHex(getXFIndex()));
    }

    @Override
    protected void serializeValue(LittleEndianOutput out) {
        out.writeInt(getSSTIndex());
    }

    @Override
    protected int getValueDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        LabelSSTRecord rec = new LabelSSTRecord();
        copyBaseFields(rec);
        rec.field_4_sst_index = field_4_sst_index;
        return rec;
    }
}
