

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public abstract class CellRecord extends StandardRecord implements CellValueRecordInterface {
    private int _rowIndex;
    private int _columnIndex;
    private int _formatIndex;

    protected CellRecord() {

    }

    protected CellRecord(RecordInputStream in) {
        _rowIndex = in.readUShort();
        _columnIndex = in.readUShort();
        _formatIndex = in.readUShort();
    }

    public final int getRow() {
        return _rowIndex;
    }

    public final void setRow(int row) {
        _rowIndex = row;
    }

    public final short getColumn() {
        return (short) _columnIndex;
    }

    public final void setColumn(short col) {
        _columnIndex = col;
    }


    public final short getXFIndex() {
        return (short) _formatIndex;
    }


    public final void setXFIndex(short xf) {
        _formatIndex = xf;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        String recordName = getRecordName();

        sb.append("[").append(recordName).append("]\n");
        sb.append("    .row    = ").append(HexDump.shortToHex(getRow())).append("\n");
        sb.append("    .col    = ").append(HexDump.shortToHex(getColumn())).append("\n");
        sb.append("    .xfindex= ").append(HexDump.shortToHex(getXFIndex())).append("\n");
        appendValueText(sb);
        sb.append("\n");
        sb.append("[/").append(recordName).append("]\n");
        return sb.toString();
    }


    protected abstract void appendValueText(StringBuilder sb);


    protected abstract String getRecordName();


    protected abstract void serializeValue(LittleEndianOutput out);


    protected abstract int getValueDataSize();

    public final void serialize(LittleEndianOutput out) {
        out.writeShort(getRow());
        out.writeShort(getColumn());
        out.writeShort(getXFIndex());
        serializeValue(out);
    }

    protected final int getDataSize() {
        return 6 + getValueDataSize();
    }

    protected final void copyBaseFields(CellRecord rec) {
        rec._rowIndex = _rowIndex;
        rec._columnIndex = _columnIndex;
        rec._formatIndex = _formatIndex;
    }
}
