

package com.document.render.office.fc.hslf.model.textproperties;


public class BitMaskTextProp extends TextProp implements Cloneable {
    private String[] subPropNames;
    private int[] subPropMasks;
    private boolean[] subPropMatches;

    public BitMaskTextProp(int sizeOfDataBlock, int maskInHeader, String overallName,
                           String[] subPropNames) {
        super(sizeOfDataBlock, maskInHeader, "bitmask");
        this.subPropNames = subPropNames;
        this.propName = overallName;
        subPropMasks = new int[subPropNames.length];
        subPropMatches = new boolean[subPropNames.length];


        for (int i = 0; i < subPropMasks.length; i++) {
            subPropMasks[i] = (1 << i);
        }
    }


    public String[] getSubPropNames() {
        return subPropNames;
    }


    public boolean[] getSubPropMatches() {
        return subPropMatches;
    }


    public int getWriteMask() {
        return dataValue;
    }


    public void setValue(int val) {
        dataValue = val;


        for (int i = 0; i < subPropMatches.length; i++) {
            subPropMatches[i] = false;
            if ((dataValue & subPropMasks[i]) != 0) {
                subPropMatches[i] = true;
            }
        }
    }


    public boolean getSubValue(int idx) {
        return subPropMatches[idx];
    }


    public void setSubValue(boolean value, int idx) {
        if (subPropMatches[idx] == value) {
            return;
        }
        if (value) {
            dataValue += subPropMasks[idx];
        } else {
            dataValue -= subPropMasks[idx];
        }
        subPropMatches[idx] = value;
    }

    public Object clone() {
        BitMaskTextProp newObj = (BitMaskTextProp) super.clone();



        newObj.subPropMatches = new boolean[subPropMatches.length];

        return newObj;
    }
}
