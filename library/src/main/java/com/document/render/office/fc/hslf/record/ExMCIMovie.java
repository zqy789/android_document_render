

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogger;



public class ExMCIMovie extends RecordContainer {
    private byte[] _header;


    private ExVideoContainer exVideo;


    protected ExMCIMovie(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);
        findInterestingChildren();
    }


    public ExMCIMovie() {
        _header = new byte[8];

        _header[0] = 0x0f;
        LittleEndian.putShort(_header, 2, (short) getRecordType());

        exVideo = new ExVideoContainer();
        _children = new Record[]{exVideo};

    }


    private void findInterestingChildren() {


        if (_children[0] instanceof ExVideoContainer) {
            exVideo = (ExVideoContainer) _children[0];
        } else {
            logger.log(POILogger.ERROR, "First child record wasn't a ExVideoContainer, was of type " + _children[0].getRecordType());
        }
    }


    public long getRecordType() {
        return RecordTypes.ExMCIMovie.typeID;
    }


    public ExVideoContainer getExVideo() {
        return exVideo;
    }


    public void dispose() {
        super.dispose();
        _header = null;
        if (exVideo != null) {
            exVideo.dispose();
            exVideo = null;
        }
    }


}
