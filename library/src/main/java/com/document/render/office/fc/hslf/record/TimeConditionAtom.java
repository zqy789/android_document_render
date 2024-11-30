
package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.util.Hashtable;


public class TimeConditionAtom extends PositionDependentRecordAtom {
    
    public static final int TOT_None = 0;               
    public static final int TOT_VisualElement = 1;      
    public static final int TOT_TimeNode = 2;           
    public static final int TOT_RuntimeNodeRef = 3;     
    private static long _type = 0xF128;
    private byte[] _header;
    
    private int triggerObject;
    
    private int triggerEvent;

    
    private int id;

    
    private int delay;

    
    protected TimeConditionAtom(byte[] source, int start, int len) {
        if (len < 40) {
            len = 40;
        }
        
        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        triggerObject = LittleEndian.getInt(source, start + 8);
        triggerEvent = LittleEndian.getInt(source, start + 12);
        id = LittleEndian.getInt(source, start + 16);
        delay = LittleEndian.getInt(source, start + 20);
    }

    
    public long getRecordType() {
        return _type;
    }

    
    public void updateOtherRecordReferences(Hashtable<Integer, Integer> oldToNewReferencesLookup) {

    }

    
    public void dispose() {
        _header = null;
    }
}
