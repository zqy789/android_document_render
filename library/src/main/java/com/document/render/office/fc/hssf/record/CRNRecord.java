

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.constant.fc.ConstantValueParser;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class CRNRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x005A;

    private int field_1_last_column_index;
    private int field_2_first_column_index;
    private int field_3_row_index;
    private Object[] field_4_constant_values;

    public CRNRecord() {
        throw new RuntimeException("incomplete code");
    }

    public CRNRecord(RecordInputStream in) {
        field_1_last_column_index = in.readUByte();
        field_2_first_column_index = in.readUByte();
        field_3_row_index = in.readShort();
        int nValues = field_1_last_column_index - field_2_first_column_index + 1;
        field_4_constant_values = ConstantValueParser.parse(in, nValues);
    }

    public int getNumberOfCRNs() {
        return field_1_last_column_index;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName()).append(" [CRN");
        sb.append(" rowIx=").append(field_3_row_index);
        sb.append(" firstColIx=").append(field_2_first_column_index);
        sb.append(" lastColIx=").append(field_1_last_column_index);
        sb.append("]");
        return sb.toString();
    }

    protected int getDataSize() {
        return 4 + ConstantValueParser.getEncodedSize(field_4_constant_values);
    }

    public void serialize(LittleEndianOutput out) {
        out.writeByte(field_1_last_column_index);
        out.writeByte(field_2_first_column_index);
        out.writeShort(field_3_row_index);
        ConstantValueParser.encode(out, field_4_constant_values);
    }


    public short getSid() {
        return sid;
    }
}
