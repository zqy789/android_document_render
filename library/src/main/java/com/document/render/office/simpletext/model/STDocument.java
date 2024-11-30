
package com.document.render.office.simpletext.model;

import com.document.render.office.constant.wp.WPModelConstant;


public class STDocument implements IDocument {

    private SectionElement section;


    public STDocument() {

    }


    public long getArea(long offset) {
        return offset & WPModelConstant.AREA_MASK;
    }


    public long getAreaStart(long offset) {
        long area = offset & WPModelConstant.AREA_MASK;

        if (area == WPModelConstant.TEXTBOX) {
            return offset & WPModelConstant.TEXTBOX_MASK;
        }
        return area;
    }


    public long getAreaEnd(long offset) {
        return getAreaStart(offset) + getLength(offset);
    }


    public long getLength(long offset) {
        return section.getEndOffset() - section.getStartOffset();
    }


    public IElement getSection(long offset) {
        return section;
    }


    public void appendSection(IElement elem) {
        section = (SectionElement) elem;
    }


    public void appendElement(IElement elem, long offset) {

    }


    public IElement getHFElement(long area, byte type) {
        return null;
    }


    public IElement getFEElement(long offset) {
        return null;
    }


    public IElement getParagraph(long offset) {
        return section.getParaCollection().getElement(offset);
    }


    public IElement getParagraphForIndex(int index, long area) {
        return section.getParaCollection().getElementForIndex(index);
    }


    public void appendParagraph(IElement element, long offset) {
        section.appendParagraph(element, offset);
    }


    public void insertString(String str, IAttributeSet attr, long offset) {

    }


    public IElement getLeaf(long offset) {
        IElement para = getParagraph(offset);
        if (para != null) {

            return ((ParagraphElement) para).getLeaf(offset);
        }
        return null;
    }


    public void setSectionAttr(long start, int len, IAttributeSet attr) {
        section.getAttribute().mergeAttribute(attr);
    }


    public void setParagraphAttr(long start, int len, IAttributeSet attr) {
        long end = start + len;

        while (start < end) {
            IElement para = section.getParaCollection().getElement(start);
            para.getAttribute().mergeAttribute(attr);
            start = para.getEndOffset();
        }
    }


    public void setLeafAttr(long start, int len, IAttributeSet attr) {
        long end = start + len;

        while (start < end) {
            IElement leaf = getLeaf(start);
            leaf.getAttribute().mergeAttribute(attr);
            start = leaf.getEndOffset();
        }
    }


    public int getParaCount(long area) {
        return section.getParaCollection().size();
    }


    public String getText(long start, long end) {
        String str = "";
        long len = end - start;
        if (len == 0 || getArea(start) != getArea(end)) {
            return str;
        }
        IElement leaf = getLeaf(start);
        String t = leaf.getText(null);
        int sIndex = (int) (start - leaf.getStartOffset());
        int eIndex = (int) (end >= leaf.getEndOffset() ? t.length() : end - leaf.getStartOffset());
        str = t.substring(sIndex, eIndex);

        start = leaf.getEndOffset();
        while (start < end) {
            leaf = getLeaf(start);
            t = leaf.getText(null);
            eIndex = (int) (end >= leaf.getEndOffset() ? t.length() : end - leaf.getStartOffset());
            str = t.substring(0, eIndex);
            start = leaf.getEndOffset();
        }
        return str;
    }


    public void dispose() {
        if (section != null) {
            section.dispose();
            section = null;
        }
    }
}
