

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.util.Hashtable;




public final class UserEditAtom extends PositionDependentRecordAtom {
    public static final int LAST_VIEW_NONE = 0;
    public static final int LAST_VIEW_SLIDE_VIEW = 1;
    public static final int LAST_VIEW_OUTLINE_VIEW = 2;
    public static final int LAST_VIEW_NOTES = 3;
    private static long _type = 4085l;
    private byte[] _header;
    private byte[] reserved;

    private int lastViewedSlideID;
    private int pptVersion;
    private int lastUserEditAtomOffset;
    private int persistPointersOffset;
    private int docPersistRef;
    private int maxPersistWritten;
    private short lastViewType;


    protected UserEditAtom(byte[] source, int start, int len) {

        if (len < 34) {
            len = 34;
        }


        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        lastViewedSlideID = LittleEndian.getInt(source, start + 0 + 8);


        pptVersion = LittleEndian.getInt(source, start + 4 + 8);




        lastUserEditAtomOffset = LittleEndian.getInt(source, start + 8 + 8);




        persistPointersOffset = LittleEndian.getInt(source, start + 12 + 8);



        docPersistRef = LittleEndian.getInt(source, start + 16 + 8);


        maxPersistWritten = LittleEndian.getInt(source, start + 20 + 8);


        lastViewType = LittleEndian.getShort(source, start + 24 + 8);


        reserved = new byte[len - 26 - 8];
        System.arraycopy(source, start + 26 + 8, reserved, 0, reserved.length);
    }


    public int getLastViewedSlideID() {
        return lastViewedSlideID;
    }

    public short getLastViewType() {
        return lastViewType;
    }

    public void setLastViewType(short type) {
        lastViewType = type;
    }


    public int getLastUserEditAtomOffset() {
        return lastUserEditAtomOffset;
    }


    public void setLastUserEditAtomOffset(int offset) {
        lastUserEditAtomOffset = offset;
    }

    public int getPersistPointersOffset() {
        return persistPointersOffset;
    }

    public void setPersistPointersOffset(int offset) {
        persistPointersOffset = offset;
    }

    public int getDocPersistRef() {
        return docPersistRef;
    }

    public int getMaxPersistWritten() {
        return maxPersistWritten;
    }



    public void setMaxPersistWritten(int max) {
        maxPersistWritten = max;
    }


    public long getRecordType() {
        return _type;
    }


    public void updateOtherRecordReferences(Hashtable<Integer, Integer> oldToNewReferencesLookup) {

        if (lastUserEditAtomOffset != 0) {
            Integer newLocation = oldToNewReferencesLookup.get(Integer.valueOf(lastUserEditAtomOffset));
            if (newLocation == null) {
                throw new RuntimeException("Couldn't find the new location of the UserEditAtom that used to be at " + lastUserEditAtomOffset);
            }
            lastUserEditAtomOffset = newLocation.intValue();
        }


        Integer newLocation = oldToNewReferencesLookup.get(Integer.valueOf(persistPointersOffset));
        if (newLocation == null) {
            throw new RuntimeException("Couldn't find the new location of the PersistPtr that used to be at " + persistPointersOffset);
        }
        persistPointersOffset = newLocation.intValue();
    }


    public void dispose() {
        _header = null;
        reserved = null;
    }
}
