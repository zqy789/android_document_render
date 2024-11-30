

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianInputStream;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;



public final class ObjRecord extends Record {
    @Keep
    public final static short sid = 0x005D;

    private static final int NORMAL_PAD_ALIGNMENT = 2;
    private static int MAX_PAD_ALIGNMENT = 4;

    private final byte[] _uninterpretedData;
    private List<SubRecord> subrecords;

    private boolean _isPaddedToQuadByteMultiple;





    public ObjRecord() {
        subrecords = new ArrayList<SubRecord>(2);

        _uninterpretedData = null;
    }

    public ObjRecord(RecordInputStream in) {











        byte[] subRecordData = in.readRemainder();
        if (LittleEndian.getUShort(subRecordData, 0) != CommonObjectDataSubRecord.sid) {



            _uninterpretedData = subRecordData;
            subrecords = null;
            return;
        }




        subrecords = new ArrayList<SubRecord>();
        ByteArrayInputStream bais = new ByteArrayInputStream(subRecordData);
        LittleEndianInputStream subRecStream = new LittleEndianInputStream(bais);
        CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord) SubRecord.createSubRecord(subRecStream, 0);
        subrecords.add(cmo);
        while (true) {
            SubRecord subRecord = SubRecord.createSubRecord(subRecStream, cmo.getObjectType());
            subrecords.add(subRecord);
            if (subRecord.isTerminating()) {
                break;
            }
        }
        int nRemainingBytes = bais.available();
        if (nRemainingBytes > 0) {

            _isPaddedToQuadByteMultiple = subRecordData.length % MAX_PAD_ALIGNMENT == 0;
            if (nRemainingBytes >= (_isPaddedToQuadByteMultiple ? MAX_PAD_ALIGNMENT : NORMAL_PAD_ALIGNMENT)) {
                if (!canPaddingBeDiscarded(subRecordData, nRemainingBytes)) {
                    String msg = "Leftover " + nRemainingBytes
                            + " bytes in subrecord data " + HexDump.toHex(subRecordData);
                    throw new RecordFormatException(msg);
                }
                _isPaddedToQuadByteMultiple = false;
            }

        } else {
            _isPaddedToQuadByteMultiple = false;
        }
        _uninterpretedData = null;
    }


    private static boolean canPaddingBeDiscarded(byte[] data, int nRemainingBytes) {

        for (int i = data.length - nRemainingBytes; i < data.length; i++) {
            if (data[i] != 0x00) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[OBJ]\n");
        for (int i = 0; i < subrecords.size(); i++) {
            SubRecord record = subrecords.get(i);
            sb.append("SUBRECORD: ").append(record.toString());
        }
        sb.append("[/OBJ]\n");
        return sb.toString();
    }

    public int getRecordSize() {
        if (_uninterpretedData != null) {
            return _uninterpretedData.length + 4;
        }
        int size = 0;
        for (int i = subrecords.size() - 1; i >= 0; i--) {
            SubRecord record = subrecords.get(i);
            size += record.getDataSize() + 4;
        }
        if (_isPaddedToQuadByteMultiple) {
            while (size % MAX_PAD_ALIGNMENT != 0) {
                size++;
            }
        } else {
            while (size % NORMAL_PAD_ALIGNMENT != 0) {
                size++;
            }
        }
        return size + 4;
    }

    public int serialize(int offset, byte[] data) {
        int recSize = getRecordSize();
        int dataSize = recSize - 4;
        LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(data, offset, recSize);

        out.writeShort(sid);
        out.writeShort(dataSize);

        if (_uninterpretedData == null) {

            for (int i = 0; i < subrecords.size(); i++) {
                SubRecord record = subrecords.get(i);
                record.serialize(out);
            }
            int expectedEndIx = offset + dataSize;

            while (out.getWriteIndex() < expectedEndIx) {
                out.writeByte(0);
            }
        } else {
            out.write(_uninterpretedData);
        }
        return recSize;
    }

    public short getSid() {
        return sid;
    }

    public List<SubRecord> getSubRecords() {
        return subrecords;
    }

    public void clearSubRecords() {
        subrecords.clear();
    }

    public void addSubRecord(int index, SubRecord element) {
        subrecords.add(index, element);
    }

    public boolean addSubRecord(SubRecord o) {
        return subrecords.add(o);
    }

    public Object clone() {
        ObjRecord rec = new ObjRecord();

        for (int i = 0; i < subrecords.size(); i++) {
            SubRecord record = subrecords.get(i);
            rec.addSubRecord((SubRecord) record.clone());
        }
        return rec;
    }
}
