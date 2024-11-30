

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.util.CellRangeAddress8Bit;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class SelectionRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x001D;
    private byte field_1_pane;
    private int field_2_row_active_cell;
    private int field_3_col_active_cell;
    private int field_4_active_cell_ref_index;
    private CellRangeAddress8Bit[] field_6_refs;


    public SelectionRecord(int activeCellRow, int activeCellCol) {
        field_1_pane = 3;
        field_2_row_active_cell = activeCellRow;
        field_3_col_active_cell = activeCellCol;
        field_4_active_cell_ref_index = 0;
        field_6_refs = new CellRangeAddress8Bit[]{
                new CellRangeAddress8Bit(activeCellRow, activeCellRow, activeCellCol, activeCellCol),
        };
    }

    public SelectionRecord(RecordInputStream in) {
        field_1_pane = in.readByte();
        field_2_row_active_cell = in.readUShort();
        field_3_col_active_cell = in.readShort();
        field_4_active_cell_ref_index = in.readShort();
        int field_5_num_refs = in.readUShort();

        field_6_refs = new CellRangeAddress8Bit[field_5_num_refs];
        for (int i = 0; i < field_6_refs.length; i++) {
            field_6_refs[i] = new CellRangeAddress8Bit(in);
        }
    }


    public byte getPane() {
        return field_1_pane;
    }


    public void setPane(byte pane) {
        field_1_pane = pane;
    }


    public int getActiveCellRow() {
        return field_2_row_active_cell;
    }


    public void setActiveCellRow(int row) {
        field_2_row_active_cell = row;
    }


    public int getActiveCellCol() {
        return field_3_col_active_cell;
    }


    public void setActiveCellCol(short col) {
        field_3_col_active_cell = col;
    }


    public int getActiveCellRef() {
        return field_4_active_cell_ref_index;
    }


    public void setActiveCellRef(short ref) {
        field_4_active_cell_ref_index = ref;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[SELECTION]\n");
        sb.append("    .pane            = ").append(HexDump.byteToHex(getPane())).append("\n");
        sb.append("    .activecellrow   = ").append(HexDump.shortToHex(getActiveCellRow())).append("\n");
        sb.append("    .activecellcol   = ").append(HexDump.shortToHex(getActiveCellCol())).append("\n");
        sb.append("    .activecellref   = ").append(HexDump.shortToHex(getActiveCellRef())).append("\n");
        sb.append("    .numrefs         = ").append(HexDump.shortToHex(field_6_refs.length)).append("\n");
        sb.append("[/SELECTION]\n");
        return sb.toString();
    }

    protected int getDataSize() {
        return 9
                + CellRangeAddress8Bit.getEncodedSize(field_6_refs.length);
    }

    public void serialize(LittleEndianOutput out) {
        out.writeByte(getPane());
        out.writeShort(getActiveCellRow());
        out.writeShort(getActiveCellCol());
        out.writeShort(getActiveCellRef());
        int nRefs = field_6_refs.length;
        out.writeShort(nRefs);
        for (int i = 0; i < field_6_refs.length; i++) {
            field_6_refs[i].serialize(out);
        }
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        SelectionRecord rec = new SelectionRecord(field_2_row_active_cell, field_3_col_active_cell);
        rec.field_1_pane = field_1_pane;
        rec.field_4_active_cell_ref_index = field_4_active_cell_ref_index;
        rec.field_6_refs = field_6_refs;
        return rec;
    }
}
