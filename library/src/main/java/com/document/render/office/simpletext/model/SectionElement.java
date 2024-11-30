
package com.document.render.office.simpletext.model;

import com.document.render.office.constant.wp.WPModelConstant;


public class SectionElement extends AbstractElement {

    private IElementCollection paraCollection;


    public SectionElement() {
        super();
        paraCollection = new ElementCollectionImpl(10);
    }


    public short getType() {
        return WPModelConstant.SECTION_ELEMENT;
    }


    public void appendParagraph(IElement element, long offset) {
        ((ElementCollectionImpl) paraCollection).addElement(element);
    }


    public IElementCollection getParaCollection() {
        return this.paraCollection;
    }


    public String getText(IDocument doc) {
        int count = paraCollection.size();
        String text = "";
        for (int i = 0; i < count; i++) {
            text += paraCollection.getElementForIndex(i).getText(null);
        }
        return text;
    }


    public IElement getElement(long offset) {
        return paraCollection.getElement(offset);
    }


    public void dispose() {
        super.dispose();
        if (paraCollection != null) {
            paraCollection.dispose();
            paraCollection = null;
        }
    }
}
