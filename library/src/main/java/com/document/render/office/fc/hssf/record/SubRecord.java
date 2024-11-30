

package com.document.render.office.fc.hssf.record;


import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.LittleEndianOutputStream;

import java.io.ByteArrayOutputStream;


public abstract class SubRecord {
    protected SubRecord() {

    }


    public static SubRecord createSubRecord(LittleEndianInput in, int cmoOt) {
        int sid = in.readUShort();
        int secondUShort = in.readUShort();

        switch (sid) {
            case CommonObjectDataSubRecord.sid:
                return new CommonObjectDataSubRecord(in, secondUShort);
            case EmbeddedObjectRefSubRecord.sid:
                return new EmbeddedObjectRefSubRecord(in, secondUShort);
            case GroupMarkerSubRecord.sid:
                return new GroupMarkerSubRecord(in, secondUShort);
            case EndSubRecord.sid:
                return new EndSubRecord(in, secondUShort);
            case NoteStructureSubRecord.sid:
                return new NoteStructureSubRecord(in, secondUShort);
            case LbsDataSubRecord.sid:
                return new LbsDataSubRecord(in, secondUShort, cmoOt);
            case FtCblsSubRecord.sid:
                return new FtCblsSubRecord(in, secondUShort);
        }
        return new UnknownSubRecord(in, sid, secondUShort);
    }


    protected abstract int getDataSize();

    public byte[] serialize() {
        int size = getDataSize() + 4;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        serialize(new LittleEndianOutputStream(baos));
        if (baos.size() != size) {
            throw new RuntimeException("write size mismatch");
        }
        return baos.toByteArray();
    }

    public abstract void serialize(LittleEndianOutput out);

    public abstract Object clone();


    public boolean isTerminating() {
        return false;
    }

    private static final class UnknownSubRecord extends SubRecord {

        private final int _sid;
        private final byte[] _data;

        public UnknownSubRecord(LittleEndianInput in, int sid, int size) {
            _sid = sid;
            byte[] buf = new byte[size];
            in.readFully(buf);
            _data = buf;
        }

        protected int getDataSize() {
            return _data.length;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(_sid);
            out.writeShort(_data.length);
            out.write(_data);
        }

        public Object clone() {
            return this;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName()).append(" [");
            sb.append("sid=").append(HexDump.shortToHex(_sid));
            sb.append(" size=").append(_data.length);
            sb.append(" : ").append(HexDump.toHex(_data));
            sb.append("]\n");
            return sb.toString();
        }
    }
}
