
package com.document.render.office.simpletext.model;



public interface IDocument {


    public long getArea(long offset);


    public long getAreaStart(long offset);


    public long getAreaEnd(long offset);


    public long getLength(long offset);


    public IElement getSection(long offset);


    public IElement getParagraph(long offset);


    public IElement getParagraphForIndex(int index, long area);


    public void appendParagraph(IElement element, long offset);


    public void appendSection(IElement elem);


    public void appendElement(IElement elem, long offset);


    public IElement getLeaf(long offset);


    public IElement getHFElement(long area, byte type);


    public IElement getFEElement(long offset);


    public void insertString(String str, IAttributeSet attr, long offset);


    public void setSectionAttr(long start, int len, IAttributeSet attr);


    public void setParagraphAttr(long start, int len, IAttributeSet attr);


    public void setLeafAttr(long start, int len, IAttributeSet attr);


    public int getParaCount(long area);


    public String getText(long start, long end);


    public void dispose();

}
