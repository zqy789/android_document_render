
package com.document.render.office.simpletext.model;


public interface IElement {

    public short getType();

    public long getStartOffset();


    public void setStartOffset(long start);

    public long getEndOffset();


    public void setEndOffset(long end);


    public IAttributeSet getAttribute();


    public void setAttribute(IAttributeSet attrSet);


    public String getText(IDocument doc);


    public void dispose();

}
