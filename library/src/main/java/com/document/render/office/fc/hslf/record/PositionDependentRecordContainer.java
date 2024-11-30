

package com.document.render.office.fc.hslf.record;

import java.util.Hashtable;



public abstract class PositionDependentRecordContainer extends RecordContainer implements
        PositionDependentRecord {

    protected int myLastOnDiskOffset;
    private int sheetId;


    public int getSheetId() {
        return sheetId;
    }


    public void setSheetId(int id) {
        sheetId = id;
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
}
