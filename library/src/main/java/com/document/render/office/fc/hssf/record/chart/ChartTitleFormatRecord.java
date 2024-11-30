


package com.document.render.office.fc.hssf.record.chart;


import com.document.render.office.fc.hssf.record.RecordInputStream;
import com.document.render.office.fc.hssf.record.StandardRecord;
import com.document.render.office.fc.util.LittleEndianOutput;



public class ChartTitleFormatRecord extends StandardRecord {
    public static final short sid = 0x1050;

    private CTFormat[] _formats;

    public ChartTitleFormatRecord(RecordInputStream in) {
        int nRecs = in.readUShort();
        _formats = new CTFormat[nRecs];

        for (int i = 0; i < nRecs; i++) {
            _formats[i] = new CTFormat(in);
        }
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(_formats.length);
        for (int i = 0; i < _formats.length; i++) {
            _formats[i].serialize(out);
        }
    }

    protected int getDataSize() {
        return 2 + CTFormat.ENCODED_SIZE * _formats.length;
    }

    public short getSid() {
        return sid;
    }

    public int getFormatCount() {
        return _formats.length;
    }

    public void modifyFormatRun(short oldPos, short newLen) {
        int shift = 0;
        for (int i = 0; i < _formats.length; i++) {
            CTFormat ctf = _formats[i];
            if (shift != 0) {
                ctf.setOffset(ctf.getOffset() + shift);
            } else if (oldPos == ctf.getOffset() && i < _formats.length - 1) {
                CTFormat nextCTF = _formats[i + 1];
                shift = newLen - (nextCTF.getOffset() - ctf.getOffset());
            }
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CHARTTITLEFORMAT]\n");
        buffer.append("    .format_runs       = ").append(_formats.length).append("\n");
        for (int i = 0; i < _formats.length; i++) {
            CTFormat ctf = _formats[i];
            buffer.append("       .char_offset= ").append(ctf.getOffset());
            buffer.append(",.fontidx= ").append(ctf.getFontIndex());
            buffer.append("\n");
        }
        buffer.append("[/CHARTTITLEFORMAT]\n");
        return buffer.toString();
    }

    private static final class CTFormat {
        public static final int ENCODED_SIZE = 4;
        private int _offset;
        private int _fontIndex;

        protected CTFormat(short offset, short fontIdx) {
            _offset = offset;
            _fontIndex = fontIdx;
        }

        public CTFormat(RecordInputStream in) {
            _offset = in.readShort();
            _fontIndex = in.readShort();
        }

        public int getOffset() {
            return _offset;
        }

        public void setOffset(int newOff) {
            _offset = newOff;
        }

        public int getFontIndex() {
            return _fontIndex;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(_offset);
            out.writeShort(_fontIndex);
        }
    }
}
