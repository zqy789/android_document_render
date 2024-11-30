
package com.document.render.office.simpletext.model;



public abstract class AbstractElement implements IElement {

    protected long start;

    protected long end;

    protected IAttributeSet attr;


    public AbstractElement() {
        attr = new AttributeSetImpl();
    }


    public short getType() {
        return -1;
    }


    public long getStartOffset() {
        return start;
    }


    public void setStartOffset(long start) {
        this.start = start;
    }


    public long getEndOffset() {
        return this.end;
    }


    public void setEndOffset(long end) {
        this.end = end;
    }


    public IAttributeSet getAttribute() {
        return this.attr;
    }


    public void setAttribute(IAttributeSet attrSet) {
        this.attr = attrSet;
    }


    public String getText(IDocument doc) {
        return null;
    }


    public String toString() {
        return "[" + start + ", " + end + "]：" + getText(null);
    }


    public void dispose() {
        if (attr != null) {
            attr.dispose();
            attr = null;
        }
    }
}
