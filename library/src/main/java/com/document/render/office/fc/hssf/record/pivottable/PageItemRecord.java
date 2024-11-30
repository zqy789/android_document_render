

package com.document.render.office.fc.hssf.record.pivottable;


import com.document.render.office.fc.hssf.record.RecordFormatException;
import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class PageItemRecord extends StandardRecord {
    public static final short sid = 0x00B6;
    private final FieldInfo[] _fieldInfos;

    public PageItemRecord(RecordInputStream in) {
        int dataSize = in.remaining();
        if (dataSize % FieldInfo.ENCODED_SIZE != 0) {
            throw new RecordFormatException("Bad data size " + dataSize);
        }

        int nItems = dataSize / FieldInfo.ENCODED_SIZE;

        FieldInfo[] fis = new FieldInfo[nItems];
        for (int i = 0; i < fis.length; i++) {
            fis[i] = new FieldInfo(in);
        }
        _fieldInfos = fis;
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        for (int i = 0; i < _fieldInfos.length; i++) {
            _fieldInfos[i].serialize(out);
        }
    }

    @Override
    protected int getDataSize() {
        return _fieldInfos.length * FieldInfo.ENCODED_SIZE;
    }

    @Override
    public short getSid() {
        return sid;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[SXPI]\n");
        for (int i = 0; i < _fieldInfos.length; i++) {
            sb.append("    item[").append(i).append("]=");
            _fieldInfos[i].appendDebugInfo(sb);
            sb.append('\n');
        }
        sb.append("[/SXPI]\n");
        return sb.toString();
    }

    private static final class FieldInfo {
        public static final int ENCODED_SIZE = 6;

        private int _isxvi;

        private int _isxvd;

        private int _idObj;

        public FieldInfo(RecordInputStream in) {
            _isxvi = in.readShort();
            _isxvd = in.readShort();
            _idObj = in.readShort();
        }

        protected void serialize(LittleEndianOutput out) {
            out.writeShort(_isxvi);
            out.writeShort(_isxvd);
            out.writeShort(_idObj);
        }

        public void appendDebugInfo(StringBuffer sb) {
            sb.append('(');
            sb.append("isxvi=").append(HexDump.shortToHex(_isxvi));
            sb.append(" isxvd=").append(HexDump.shortToHex(_isxvd));
            sb.append(" idObj=").append(HexDump.shortToHex(_idObj));
            sb.append(')');
        }
    }
}
