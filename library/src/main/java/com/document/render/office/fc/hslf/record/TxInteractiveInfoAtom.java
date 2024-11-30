

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;



public final class TxInteractiveInfoAtom extends RecordAtom {

    private byte[] _header;


    private byte[] _data;


    public TxInteractiveInfoAtom() {
        _header = new byte[8];
        _data = new byte[8];

        LittleEndian.putShort(_header, 2, (short) getRecordType());
        LittleEndian.putInt(_header, 4, _data.length);
    }


    protected TxInteractiveInfoAtom(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _data = new byte[len - 8];
        System.arraycopy(source, start + 8, _data, 0, len - 8);

    }


    public int getStartIndex() {
        return LittleEndian.getInt(_data, 0);
    }


    public void setStartIndex(int idx) {
        LittleEndian.putInt(_data, 0, idx);
    }


    public int getEndIndex() {
        return LittleEndian.getInt(_data, 4);
    }


    public void setEndIndex(int idx) {
        LittleEndian.putInt(_data, 4, idx);
    }


    public long getRecordType() {
        return RecordTypes.TxInteractiveInfoAtom.typeID;
    }


    public void dispose() {
        _header = null;
        _data = null;
    }
}
