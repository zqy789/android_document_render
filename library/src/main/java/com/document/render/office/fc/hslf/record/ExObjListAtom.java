

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;




public class ExObjListAtom extends RecordAtom {

    private byte[] _header;


    private byte[] _data;


    protected ExObjListAtom() {
        _header = new byte[8];
        _data = new byte[4];

        LittleEndian.putShort(_header, 2, (short) getRecordType());
        LittleEndian.putInt(_header, 4, _data.length);


    }


    protected ExObjListAtom(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _data = new byte[len - 8];
        System.arraycopy(source, start + 8, _data, 0, len - 8);


        if (_data.length < 4) {
            throw new IllegalArgumentException(
                    "The length of the data for a ExObjListAtom must be at least 4 bytes, but was only "
                            + _data.length);
        }
    }


    public long getObjectIDSeed() {
        return LittleEndian.getUInt(_data, 0);
    }


    public void setObjectIDSeed(int seed) {
        LittleEndian.putInt(_data, 0, seed);
    }


    public long getRecordType() {
        return RecordTypes.ExObjListAtom.typeID;
    }


    public void dispose() {
        _header = null;
        _data = null;
    }

}
