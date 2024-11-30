

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;



public class InteractiveInfo extends RecordContainer {
    private static long _type = 4082;
    private byte[] _header;

    private InteractiveInfoAtom infoAtom;


    protected InteractiveInfo(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);
        findInterestingChildren();
    }


    public InteractiveInfo() {
        _header = new byte[8];
        _children = new Record[1];


        _header[0] = 0x0f;
        LittleEndian.putShort(_header, 2, (short) _type);


        infoAtom = new InteractiveInfoAtom();
        _children[0] = infoAtom;
    }


    public InteractiveInfoAtom getInteractiveInfoAtom() {
        return infoAtom;
    }


    private void findInterestingChildren() {

        if (_children[0] instanceof InteractiveInfoAtom) {
            infoAtom = (InteractiveInfoAtom) _children[0];
        } else {
            throw new IllegalStateException(
                    "First child record wasn't a InteractiveInfoAtom, was of type "
                            + _children[0].getRecordType());
        }
    }


    public long getRecordType() {
        return _type;
    }


    public void dispose() {
        _header = null;
        if (infoAtom != null) {
            infoAtom.dispose();
            infoAtom = null;
        }
    }
}
