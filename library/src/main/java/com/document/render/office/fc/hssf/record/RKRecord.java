

package com.document.render.office.fc.hssf.record;


import com.document.render.office.fc.hssf.util.RKUtil;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class RKRecord extends CellRecord {
    public final static short sid = 0x027E;
    public final static short RK_IEEE_NUMBER = 0;
    public final static short RK_IEEE_NUMBER_TIMES_100 = 1;
    public final static short RK_INTEGER = 2;
    public final static short RK_INTEGER_TIMES_100 = 3;
    private int field_4_rk_number;

    private RKRecord() {

    }

    public RKRecord(RecordInputStream in) {
        super(in);
        field_4_rk_number = in.readInt();
    }


    public double getRKNumber() {
        return RKUtil.decodeNumber(field_4_rk_number);
    }

    @Override
    protected String getRecordName() {
        return "RK";
    }

    @Override
    protected void appendValueText(StringBuilder sb) {
        sb.append("  .value= ").append(getRKNumber());
    }

    @Override
    protected void serializeValue(LittleEndianOutput out) {
        out.writeInt(field_4_rk_number);
    }

    @Override
    protected int getValueDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        RKRecord rec = new RKRecord();
        copyBaseFields(rec);
        rec.field_4_rk_number = field_4_rk_number;
        return rec;
    }
}
