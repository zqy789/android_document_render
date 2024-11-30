

package com.document.render.office.fc.hssf.record.pivottable;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public final class ViewFieldsRecord extends StandardRecord {
    public static final short sid = 0x00B1;


    private static final int STRING_NOT_PRESENT_LEN = 0xFFFF;

    private static final int BASE_SIZE = 10;

    private int _sxaxis;
    private int _cSub;
    private int _grbitSub;
    private int _cItm;

    private String _name;

    public ViewFieldsRecord(RecordInputStream in) {
        _sxaxis = in.readShort();
        _cSub = in.readShort();
        _grbitSub = in.readShort();
        _cItm = in.readShort();

        int cchName = in.readUShort();
        if (cchName != STRING_NOT_PRESENT_LEN) {
            int flag = in.readByte();
            if ((flag & 0x01) != 0) {
                _name = in.readUnicodeLEString(cchName);
            } else {
                _name = in.readCompressedUnicode(cchName);
            }
        }
    }

    @Override
    protected void serialize(LittleEndianOutput out) {

        out.writeShort(_sxaxis);
        out.writeShort(_cSub);
        out.writeShort(_grbitSub);
        out.writeShort(_cItm);

        if (_name != null) {
            StringUtil.writeUnicodeString(out, _name);
        } else {
            out.writeShort(STRING_NOT_PRESENT_LEN);
        }
    }

    @Override
    protected int getDataSize() {
        if (_name == null) {
            return BASE_SIZE;
        }
        return BASE_SIZE
                + 1
                + _name.length() * (StringUtil.hasMultibyte(_name) ? 2 : 1);
    }

    @Override
    public short getSid() {
        return sid;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[SXVD]\n");
        buffer.append("    .sxaxis    = ").append(HexDump.shortToHex(_sxaxis)).append('\n');
        buffer.append("    .cSub      = ").append(HexDump.shortToHex(_cSub)).append('\n');
        buffer.append("    .grbitSub  = ").append(HexDump.shortToHex(_grbitSub)).append('\n');
        buffer.append("    .cItm      = ").append(HexDump.shortToHex(_cItm)).append('\n');
        buffer.append("    .name      = ").append(_name).append('\n');

        buffer.append("[/SXVD]\n");
        return buffer.toString();
    }


    private static final class Axis {
        public static final int NO_AXIS = 0;
        public static final int ROW = 1;
        public static final int COLUMN = 2;
        public static final int PAGE = 4;
        public static final int DATA = 8;
    }
}
