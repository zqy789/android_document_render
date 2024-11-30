

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.IntList;
import com.document.render.office.fc.util.LittleEndianOutput;


public class IndexRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x020B;
    private int field_2_first_row;
    private int field_3_last_row_add1;
    private int field_4_zero;
    private IntList field_5_dbcells;

    public IndexRecord() {
    }

    public IndexRecord(RecordInputStream in) {
        int field_1_zero = in.readInt();
        if (field_1_zero != 0) {
            throw new RecordFormatException("Expected zero for field 1 but got " + field_1_zero);
        }
        field_2_first_row = in.readInt();
        field_3_last_row_add1 = in.readInt();
        field_4_zero = in.readInt();

        int nCells = in.remaining() / 4;
        field_5_dbcells = new IntList(nCells);
        for (int i = 0; i < nCells; i++) {
            field_5_dbcells.add(in.readInt());
        }
    }


    public static int getRecordSizeForBlockCount(int blockCount) {
        return 20 + 4 * blockCount;
    }

    public void addDbcell(int cell) {
        if (field_5_dbcells == null) {
            field_5_dbcells = new IntList();
        }
        field_5_dbcells.add(cell);
    }

    public void setDbcell(int cell, int value) {
        field_5_dbcells.set(cell, value);
    }

    public int getFirstRow() {
        return field_2_first_row;
    }

    public void setFirstRow(int row) {
        field_2_first_row = row;
    }

    public int getLastRowAdd1() {
        return field_3_last_row_add1;
    }

    public void setLastRowAdd1(int row) {
        field_3_last_row_add1 = row;
    }

    public int getNumDbcells() {
        if (field_5_dbcells == null) {
            return 0;
        }
        return field_5_dbcells.size();
    }

    public int getDbcellAt(int cellnum) {
        return field_5_dbcells.get(cellnum);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[INDEX]\n");
        buffer.append("    .firstrow       = ")
                .append(Integer.toHexString(getFirstRow())).append("\n");
        buffer.append("    .lastrowadd1    = ")
                .append(Integer.toHexString(getLastRowAdd1())).append("\n");
        for (int k = 0; k < getNumDbcells(); k++) {
            buffer.append("    .dbcell_").append(k).append(" = ")
                    .append(Integer.toHexString(getDbcellAt(k))).append("\n");
        }
        buffer.append("[/INDEX]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {

        out.writeInt(0);
        out.writeInt(getFirstRow());
        out.writeInt(getLastRowAdd1());
        out.writeInt(field_4_zero);
        for (int k = 0; k < getNumDbcells(); k++) {
            out.writeInt(getDbcellAt(k));
        }
    }

    protected int getDataSize() {
        return 16
                + getNumDbcells() * 4;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        IndexRecord rec = new IndexRecord();
        rec.field_2_first_row = field_2_first_row;
        rec.field_3_last_row_add1 = field_3_last_row_add1;
        rec.field_4_zero = field_4_zero;
        rec.field_5_dbcells = new IntList();
        rec.field_5_dbcells.addAll(field_5_dbcells);
        return rec;
    }
}
