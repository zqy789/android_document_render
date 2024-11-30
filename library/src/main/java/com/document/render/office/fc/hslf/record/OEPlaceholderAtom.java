

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;



public final class OEPlaceholderAtom extends RecordAtom {


    public static final int PLACEHOLDER_FULLSIZE = 0;


    public static final int PLACEHOLDER_HALFSIZE = 1;


    public static final int PLACEHOLDER_QUARTSIZE = 2;


    public static final byte None = 0;


    public static final byte MasterTitle = 1;


    public static final byte MasterBody = 2;


    public static final byte MasterCenteredTitle = 3;


    public static final byte MasterSubTitle = 4;


    public static final byte MasterNotesSlideImage = 5;


    public static final byte MasterNotesBody = 6;


    public static final byte MasterDate = 7;


    public static final byte MasterSlideNumber = 8;


    public static final byte MasterFooter = 9;


    public static final byte MasterHeader = 10;


    public static final byte NotesSlideImage = 11;


    public static final byte NotesBody = 12;


    public static final byte Title = 13;


    public static final byte Body = 14;


    public static final byte CenteredTitle = 15;


    public static final byte Subtitle = 16;


    public static final byte VerticalTextTitle = 17;


    public static final byte VerticalTextBody = 18;


    public static final byte Object = 19;


    public static final byte Graph = 20;


    public static final byte Table = 21;


    public static final byte ClipArt = 22;


    public static final byte OrganizationChart = 23;


    public static final byte MediaClip = 24;

    private byte[] _header;

    private int placementId;
    private int placeholderId;
    private int placeholderSize;


    public OEPlaceholderAtom() {
        _header = new byte[8];
        LittleEndian.putUShort(_header, 0, 0);
        LittleEndian.putUShort(_header, 2, (int) getRecordType());
        LittleEndian.putInt(_header, 4, 8);

        placementId = 0;
        placeholderId = 0;
        placeholderSize = 0;
    }


    protected OEPlaceholderAtom(byte[] source, int start, int len) {
        _header = new byte[8];
        int offset = start;
        System.arraycopy(source, start, _header, 0, 8);
        offset += _header.length;

        placementId = LittleEndian.getInt(source, offset);
        offset += 4;
        placeholderId = LittleEndian.getUnsignedByte(source, offset);
        offset++;
        placeholderSize = LittleEndian.getUnsignedByte(source, offset);
        offset++;
    }


    public long getRecordType() {
        return RecordTypes.OEPlaceholderAtom.typeID;
    }


    public int getPlacementId() {
        return placementId;
    }


    public void setPlacementId(int id) {
        placementId = id;
    }


    public int getPlaceholderId() {
        return placeholderId;
    }


    public void setPlaceholderId(byte id) {
        placeholderId = id;
    }


    public int getPlaceholderSize() {
        return placeholderSize;
    }


    public void setPlaceholderSize(byte size) {
        placeholderSize = size;
    }


    public void dispose() {
        _header = null;
    }
}
