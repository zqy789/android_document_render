

package com.document.render.office.fc.hslf.model.textproperties;


public class TextProp implements Cloneable {
    protected int sizeOfDataBlock;
    protected String propName;
    protected int dataValue;
    protected int maskInHeader;


    public TextProp(int sizeOfDataBlock, int maskInHeader, String propName) {
        this.sizeOfDataBlock = sizeOfDataBlock;
        this.maskInHeader = maskInHeader;
        this.propName = propName;
        this.dataValue = 0;
    }


    public String getName() {
        return propName;
    }


    public int getSize() {
        return sizeOfDataBlock;
    }


    public int getMask() {
        return maskInHeader;
    }


    public int getWriteMask() {
        return getMask();
    }


    public int getValue() {
        return dataValue;
    }


    public void setValue(int val) {
        dataValue = val;
    }


    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }


    public void dispose() {
        propName = null;
    }
}
