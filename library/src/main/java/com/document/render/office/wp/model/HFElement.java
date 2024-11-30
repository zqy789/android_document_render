
package com.document.render.office.wp.model;

import com.document.render.office.simpletext.model.AbstractElement;


public class HFElement extends AbstractElement {


    private byte hfType;

    private short elemType;


    public HFElement(short elemType, byte hftype) {
        super();
        this.hfType = hftype;
        this.elemType = elemType;
    }


    public short getType() {
        return elemType;
    }


    public byte getHFType() {
        return hfType;
    }
}
