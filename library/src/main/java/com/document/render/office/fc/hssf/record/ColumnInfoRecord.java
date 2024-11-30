

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class ColumnInfoRecord extends StandardRecord {
    @Keep
    public static final short sid = 0x007D;
    private static final BitField hidden = BitFieldFactory.getInstance(0x01);
    private static final BitField outlevel = BitFieldFactory.getInstance(0x0700);
    private static final BitField collapsed = BitFieldFactory.getInstance(0x1000);
    private int _firstCol;
    private int _lastCol;
    private int _colWidth;
    private int _xfIndex;
    private int _options;

    private int field_6_reserved;


    private int colPixelWidth = 74;


    public ColumnInfoRecord() {
        setColumnWidth(2275);
        _options = 2;
        _xfIndex = 0x0f;
        field_6_reserved = 2;
    }

    public ColumnInfoRecord(RecordInputStream in) {
        _firstCol = in.readUShort();
        _lastCol = in.readUShort();
        _colWidth = in.readUShort();
        _xfIndex = in.readUShort();
        _options = in.readUShort();
        switch (in.remaining()) {
            case 2:
                field_6_reserved = in.readUShort();
                break;
            case 1:


                field_6_reserved = in.readByte();
                break;
            case 0:



                field_6_reserved = 0;
                break;
            default:
                throw new RuntimeException("Unusual record size remaining=(" + in.remaining() + ")");
        }
    }


    public int getFirstColumn() {
        return _firstCol;
    }


    public void setFirstColumn(int fc) {
        _firstCol = fc;
    }


    public int getLastColumn() {
        return _lastCol;
    }


    public void setLastColumn(int lc) {
        _lastCol = lc;
    }


    public int getColumnWidth() {
        return _colWidth;
    }


    public void setColumnWidth(int cw) {
        _colWidth = cw;
    }


    public int getXFIndex() {
        return _xfIndex;
    }


    public void setXFIndex(int xfi) {
        _xfIndex = xfi;
    }


    public boolean getHidden() {
        return hidden.isSet(_options);
    }


    public void setHidden(boolean ishidden) {
        _options = hidden.setBoolean(_options, ishidden);
    }


    public int getOutlineLevel() {
        return outlevel.getValue(_options);
    }


    public void setOutlineLevel(int olevel) {
        _options = outlevel.setValue(_options, olevel);
    }


    public boolean getCollapsed() {
        return collapsed.isSet(_options);
    }


    public void setCollapsed(boolean isCollapsed) {
        _options = collapsed.setBoolean(_options, isCollapsed);
    }

    public boolean containsColumn(int columnIndex) {
        return _firstCol <= columnIndex && columnIndex <= _lastCol;
    }

    public boolean isAdjacentBefore(ColumnInfoRecord other) {
        return _lastCol == other._firstCol - 1;
    }


    public boolean formatMatches(ColumnInfoRecord other) {
        if (_xfIndex != other._xfIndex) {
            return false;
        }
        if (_options != other._options) {
            return false;
        }
        if (_colWidth != other._colWidth) {
            return false;
        }
        return true;
    }

    public short getSid() {
        return sid;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getFirstColumn());
        out.writeShort(getLastColumn());
        out.writeShort(getColumnWidth());
        out.writeShort(getXFIndex());
        out.writeShort(_options);
        out.writeShort(field_6_reserved);
    }

    protected int getDataSize() {
        return 12;
    }


    public int getColPixelWidth() {
        return colPixelWidth;
    }


    public void setColPixelWidth(int colPixelWidth) {
        this.colPixelWidth = colPixelWidth;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[COLINFO]\n");
        sb.append("  colfirst = ").append(getFirstColumn()).append("\n");
        sb.append("  collast  = ").append(getLastColumn()).append("\n");
        sb.append("  colwidth = ").append(getColumnWidth()).append("\n");
        sb.append("  xfindex  = ").append(getXFIndex()).append("\n");
        sb.append("  options  = ").append(HexDump.shortToHex(_options)).append("\n");
        sb.append("    hidden   = ").append(getHidden()).append("\n");
        sb.append("    olevel   = ").append(getOutlineLevel()).append("\n");
        sb.append("    collapsed= ").append(getCollapsed()).append("\n");
        sb.append("[/COLINFO]\n");
        return sb.toString();
    }

    public Object clone() {
        ColumnInfoRecord rec = new ColumnInfoRecord();
        rec._firstCol = _firstCol;
        rec._lastCol = _lastCol;
        rec._colWidth = _colWidth;
        rec._xfIndex = _xfIndex;
        rec._options = _options;
        rec.field_6_reserved = field_6_reserved;
        return rec;
    }
}
