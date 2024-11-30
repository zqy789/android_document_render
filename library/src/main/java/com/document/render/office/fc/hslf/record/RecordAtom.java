

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.hslf.model.textproperties.AutoNumberTextProp;

import java.util.LinkedList;



public abstract class RecordAtom extends Record {

    public boolean isAnAtom() {
        return true;
    }


    public Record[] getChildRecords() {
        return null;
    }


    public LinkedList<AutoNumberTextProp> getExtendedParagraphPropList() {
        return null;
    }


}
