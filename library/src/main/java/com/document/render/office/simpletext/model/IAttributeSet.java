
package com.document.render.office.simpletext.model;


public interface IAttributeSet {


    public int getID();


    public void setAttribute(short attrID, int value);


    public void removeAttribute(short attrID);


    public int getAttribute(short attrID);


    public void mergeAttribute(IAttributeSet attr);


    public IAttributeSet clone();


    public void dispose();

}
