

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class DBCellRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x00D7;
    public final static int BLOCK_SIZE = 32;

    private final int field_1_row_offset;
    private final short[] field_2_cell_offsets;
    DBCellRecord(int rowOffset, short[] cellOffsets) {
        field_1_row_offset = rowOffset;
        field_2_cell_offsets = cellOffsets;
    }

    public DBCellRecord(RecordInputStream in) {
        field_1_row_offset = in.readUShort();
        int size = in.remaining();
        field_2_cell_offsets = new short[size / 2];

        for (int i = 0; i < field_2_cell_offsets.length; i++) {
            field_2_cell_offsets[i] = in.readShort();
        }
    }


    public static int calculateSizeOfRecords(int nBlocks, int nRows) {



        return nBlocks * 8 + nRows * 2;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[DBCELL]\n");
        buffer.append("    .rowoffset = ").append(HexDump.intToHex(field_1_row_offset)).append("\n");
        for (int k = 0; k < field_2_cell_offsets.length; k++) {
            buffer.append("    .cell_").append(k).append(" = ")
                    .append(HexDump.shortToHex(field_2_cell_offsets[k])).append("\n");
        }
        buffer.append("[/DBCELL]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(field_1_row_offset);
        for (int k = 0; k < field_2_cell_offsets.length; k++) {
            out.writeShort(field_2_cell_offsets[k]);
        }
    }

    protected int getDataSize() {
        return 4 + field_2_cell_offsets.length * 2;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {

        return this;
    }

    public static final class Builder {
        private short[] _cellOffsets;
        private int _nCellOffsets;

        public Builder() {
            _cellOffsets = new short[4];
        }

        public void addCellOffset(int cellRefOffset) {
            if (_cellOffsets.length <= _nCellOffsets) {
                short[] temp = new short[_nCellOffsets * 2];
                System.arraycopy(_cellOffsets, 0, temp, 0, _nCellOffsets);
                _cellOffsets = temp;
            }
            _cellOffsets[_nCellOffsets] = (short) cellRefOffset;
            _nCellOffsets++;
        }

        public DBCellRecord build(int rowOffset) {
            short[] cellOffsets = new short[_nCellOffsets];
            System.arraycopy(_cellOffsets, 0, cellOffsets, 0, _nCellOffsets);
            return new DBCellRecord(rowOffset, cellOffsets);
        }
    }
}
