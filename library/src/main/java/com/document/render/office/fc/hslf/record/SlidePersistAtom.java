

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.OutputStream;


public final class SlidePersistAtom extends RecordAtom {
    private static long _type = 1011l;
    private byte[] _header;

    private int refID;
    private boolean hasShapesOtherThanPlaceholders;

    private int numPlaceholderTexts;

    private int slideIdentifier;

    private byte[] reservedFields;


    protected SlidePersistAtom(byte[] source, int start, int len) {

        if (len < 8) {
            len = 8;
        }


        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        refID = LittleEndian.getInt(source, start + 8);


        int flags = LittleEndian.getInt(source, start + 12);
        if (flags == 4) {
            hasShapesOtherThanPlaceholders = true;
        } else {
            hasShapesOtherThanPlaceholders = false;
        }


        numPlaceholderTexts = LittleEndian.getInt(source, start + 16);


        slideIdentifier = LittleEndian.getInt(source, start + 20);



        reservedFields = new byte[len - 24];
        System.arraycopy(source, start + 24, reservedFields, 0, reservedFields.length);
    }


    public SlidePersistAtom() {
        _header = new byte[8];
        LittleEndian.putUShort(_header, 0, 0);
        LittleEndian.putUShort(_header, 2, (int) _type);
        LittleEndian.putInt(_header, 4, 20);

        hasShapesOtherThanPlaceholders = true;
        reservedFields = new byte[4];
    }

    public int getRefID() {
        return refID;
    }


    public void setRefID(int id) {
        refID = id;
    }

    public int getSlideIdentifier() {
        return slideIdentifier;
    }

    public void setSlideIdentifier(int id) {
        slideIdentifier = id;
    }



    public int getNumPlaceholderTexts() {
        return numPlaceholderTexts;
    }

    public boolean getHasShapesOtherThanPlaceholders() {
        return hasShapesOtherThanPlaceholders;
    }


    public long getRecordType() {
        return _type;
    }


    public void writeOut(OutputStream out) throws IOException {

        out.write(_header);


        int flags = 0;
        if (hasShapesOtherThanPlaceholders) {
            flags = 4;
        }


        writeLittleEndian(refID, out);
        writeLittleEndian(flags, out);
        writeLittleEndian(numPlaceholderTexts, out);
        writeLittleEndian(slideIdentifier, out);
        out.write(reservedFields);
    }


    public void dispose() {
        _header = null;
        reservedFields = null;
    }
}
