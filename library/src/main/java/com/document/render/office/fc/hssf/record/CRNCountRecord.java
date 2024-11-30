

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;


public final class CRNCountRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x59;

    private static final short DATA_SIZE = 4;


    private int field_1_number_crn_records;
    private int field_2_sheet_table_index;

    public CRNCountRecord() {
        throw new RuntimeException("incomplete code");
    }

    public CRNCountRecord(RecordInputStream in) {
        field_1_number_crn_records = in.readShort();
        if (field_1_number_crn_records < 0) {


            field_1_number_crn_records = (short) -field_1_number_crn_records;
        }
        field_2_sheet_table_index = in.readShort();
    }

    public int getNumberOfCRNs() {
        return field_1_number_crn_records;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName()).append(" [XCT");
        sb.append(" nCRNs=").append(field_1_number_crn_records);
        sb.append(" sheetIx=").append(field_2_sheet_table_index);
        sb.append("]");
        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort((short) field_1_number_crn_records);
        out.writeShort((short) field_2_sheet_table_index);
    }

    protected int getDataSize() {
        return DATA_SIZE;
    }


    public short getSid() {
        return sid;
    }
}
