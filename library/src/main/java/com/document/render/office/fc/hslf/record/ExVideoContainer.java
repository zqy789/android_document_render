

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogger;



public final class ExVideoContainer extends RecordContainer {
    private byte[] _header;


    private ExMediaAtom mediaAtom;

    private CString pathAtom;


    protected ExVideoContainer(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);
        findInterestingChildren();
    }


    public ExVideoContainer() {

        _header = new byte[8];
        _header[0] = 0x0f;
        LittleEndian.putShort(_header, 2, (short) getRecordType());

        _children = new Record[2];
        _children[0] = mediaAtom = new ExMediaAtom();
        _children[1] = pathAtom = new CString();
    }


    private void findInterestingChildren() {


        if (_children[0] instanceof ExMediaAtom) {
            mediaAtom = (ExMediaAtom) _children[0];
        } else {
            logger.log(POILogger.ERROR, "First child record wasn't a ExMediaAtom, was of type "
                    + _children[0].getRecordType());
        }
        if (_children[1] instanceof CString) {
            pathAtom = (CString) _children[1];
        } else {
            logger.log(POILogger.ERROR, "Second child record wasn't a CString, was of type "
                    + _children[1].getRecordType());
        }
    }


    public long getRecordType() {
        return RecordTypes.ExVideoContainer.typeID;
    }


    public ExMediaAtom getExMediaAtom() {
        return mediaAtom;
    }


    public CString getPathAtom() {
        return pathAtom;
    }


    public void dispose() {
        super.dispose();
        _header = null;
        if (pathAtom != null) {
            pathAtom.dispose();
            pathAtom = null;
        }
        if (mediaAtom != null) {
            mediaAtom.dispose();
            mediaAtom = null;
        }
    }

}
