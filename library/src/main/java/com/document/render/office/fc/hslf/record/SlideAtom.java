

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.OutputStream;




public final class SlideAtom extends RecordAtom {
    public static final int MASTER_SLIDE_ID = 0;
    public static final int USES_MASTER_SLIDE_ID = -2147483648;
    private static long _type = 1007l;
    private byte[] _header;
    private int masterID;
    private int notesID;

    private boolean followMasterObjects;
    private boolean followMasterScheme;
    private boolean followMasterBackground;
    private SSlideLayoutAtom layoutAtom;
    private byte[] reserved;



    protected SlideAtom(byte[] source, int start, int len) {

        if (len < 30) {
            len = 30;
        }


        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        byte[] SSlideLayoutAtomData = new byte[12];
        System.arraycopy(source, start + 8, SSlideLayoutAtomData, 0, 12);

        layoutAtom = new SSlideLayoutAtom(SSlideLayoutAtomData);


        masterID = LittleEndian.getInt(source, start + 12 + 8);
        notesID = LittleEndian.getInt(source, start + 16 + 8);


        int flags = LittleEndian.getUShort(source, start + 20 + 8);
        if ((flags & 4) == 4) {
            followMasterBackground = true;
        } else {
            followMasterBackground = false;
        }
        if ((flags & 2) == 2) {
            followMasterScheme = true;
        } else {
            followMasterScheme = false;
        }
        if ((flags & 1) == 1) {
            followMasterObjects = true;
        } else {
            followMasterObjects = false;
        }



        reserved = new byte[len - 30];
        System.arraycopy(source, start + 30, reserved, 0, reserved.length);
    }


    public SlideAtom() {
        _header = new byte[8];
        LittleEndian.putUShort(_header, 0, 2);
        LittleEndian.putUShort(_header, 2, (int) _type);
        LittleEndian.putInt(_header, 4, 24);

        byte[] ssdate = new byte[12];
        layoutAtom = new SSlideLayoutAtom(ssdate);
        layoutAtom.setGeometryType(SSlideLayoutAtom.BLANK_SLIDE);

        followMasterObjects = true;
        followMasterScheme = true;
        followMasterBackground = true;
        masterID = -2147483648;
        notesID = 0;
        reserved = new byte[2];
    }


    public int getMasterID() {
        return masterID;
    }


    public void setMasterID(int id) {
        masterID = id;
    }


    public int getNotesID() {
        return notesID;
    }


    public void setNotesID(int id) {
        notesID = id;
    }


    public SSlideLayoutAtom getSSlideLayoutAtom() {
        return layoutAtom;
    }

    public boolean getFollowMasterObjects() {
        return followMasterObjects;
    }

    public void setFollowMasterObjects(boolean flag) {
        followMasterObjects = flag;
    }

    public boolean getFollowMasterScheme() {
        return followMasterScheme;
    }

    public void setFollowMasterScheme(boolean flag) {
        followMasterScheme = flag;
    }




    public boolean getFollowMasterBackground() {
        return followMasterBackground;
    }

    public void setFollowMasterBackground(boolean flag) {
        followMasterBackground = flag;
    }


    public long getRecordType() {
        return _type;
    }


    public void writeOut(OutputStream out) throws IOException {

        out.write(_header);


        layoutAtom.writeOut(out);


        writeLittleEndian(masterID, out);
        writeLittleEndian(notesID, out);


        short flags = 0;
        if (followMasterObjects) {
            flags += 1;
        }
        if (followMasterScheme) {
            flags += 2;
        }
        if (followMasterBackground) {
            flags += 4;
        }
        writeLittleEndian(flags, out);


        out.write(reserved);
    }


    public void dispose() {
        _header = null;
        if (layoutAtom != null) {
            layoutAtom.dispose();
            layoutAtom = null;
        }
        reserved = null;
    }


    public class SSlideLayoutAtom {

        public static final int TITLE_SLIDE = 0;
        public static final int TITLE_BODY_SLIDE = 1;
        public static final int TITLE_MASTER_SLIDE = 2;
        public static final int MASTER_SLIDE = 3;
        public static final int MASTER_NOTES = 4;
        public static final int NOTES_TITLE_BODY = 5;
        public static final int HANDOUT = 6;
        public static final int TITLE_ONLY = 7;
        public static final int TITLE_2_COLUMN_BODY = 8;
        public static final int TITLE_2_ROW_BODY = 9;
        public static final int TITLE_2_COLUNM_RIGHT_2_ROW_BODY = 10;
        public static final int TITLE_2_COLUNM_LEFT_2_ROW_BODY = 11;
        public static final int TITLE_2_ROW_BOTTOM_2_COLUMN_BODY = 12;
        public static final int TITLE_2_ROW_TOP_2_COLUMN_BODY = 13;
        public static final int FOUR_OBJECTS = 14;
        public static final int BIG_OBJECT = 15;
        public static final int BLANK_SLIDE = 16;
        public static final int VERTICAL_TITLE_BODY_LEFT = 17;
        public static final int VERTICAL_TITLE_2_ROW_BODY_LEFT = 17;


        private int geometry;

        private byte[] placeholderIDs;


        public SSlideLayoutAtom(byte[] data) {
            if (data.length != 12) {
                throw new RuntimeException("SSlideLayoutAtom created with byte array not 12 bytes long - was " + data.length + " bytes in size");
            }


            geometry = LittleEndian.getInt(data, 0);
            placeholderIDs = new byte[8];
            System.arraycopy(data, 4, placeholderIDs, 0, 8);
        }


        public int getGeometryType() {
            return geometry;
        }


        public void setGeometryType(int geom) {
            geometry = geom;
        }


        public void writeOut(OutputStream out) throws IOException {

            writeLittleEndian(geometry, out);

            out.write(placeholderIDs);
        }


        public void dispose() {
            placeholderIDs = null;
        }
    }
}
