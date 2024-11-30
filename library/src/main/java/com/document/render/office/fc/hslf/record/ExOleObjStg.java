

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;



public class ExOleObjStg extends RecordAtom implements PositionDependentRecord, PersistRecord {


    protected int myLastOnDiskOffset;
    private int _persistId;

    private byte[] _header;

    private byte[] _data;


    public ExOleObjStg() {
        _header = new byte[8];
        _data = new byte[0];

        LittleEndian.putShort(_header, 0, (short) 0x10);
        LittleEndian.putShort(_header, 2, (short) getRecordType());
        LittleEndian.putInt(_header, 4, _data.length);
    }


    protected ExOleObjStg(byte[] source, int start, int len) {


    }


    public int getDataLength() {
        return LittleEndian.getInt(_data, 0);
    }


    public InputStream getData() {
        InputStream compressedStream = new ByteArrayInputStream(_data, 4, _data.length);
        return new InflaterInputStream(compressedStream);
    }


    public void setData(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] b = new byte[4];
        LittleEndian.putInt(b, data.length);
        out.write(b);

        DeflaterOutputStream def = new DeflaterOutputStream(out);
        def.write(data, 0, data.length);
        def.finish();
        _data = out.toByteArray();
        LittleEndian.putInt(_header, 4, _data.length);
    }

    public byte[] getRawData() {
        return _data;
    }


    public long getRecordType() {
        return RecordTypes.ExOleObjStg.typeID;
    }


    public int getPersistId() {
        return _persistId;
    }


    public void setPersistId(int id) {
        _persistId = id;
    }


    public int getLastOnDiskOffset() {
        return myLastOnDiskOffset;
    }


    public void setLastOnDiskOffset(int offset) {
        myLastOnDiskOffset = offset;
    }

    public void updateOtherRecordReferences(Hashtable<Integer, Integer> oldToNewReferencesLookup) {
        return;
    }


    public void dispose() {
        _header = null;
        _data = null;

    }
}
