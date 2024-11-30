
package com.document.render.office.wp.model;

import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.simpletext.model.ElementCollectionImpl;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.model.LeafElement;
import com.document.render.office.simpletext.model.ParagraphElement;


public class TableElement extends ParagraphElement {

    private ElementCollectionImpl rowElement;


    public TableElement() {
        super();
        rowElement = new ElementCollectionImpl(10);
    }


    public short getType() {
        return WPModelConstant.TABLE_ELEMENT;
    }


    public void appendRow(RowElement rowElem) {
        rowElement.addElement(rowElem);
    }


    public IElement getRowElement(long offset) {
        return rowElement.getElement(offset);
    }


    public IElement getElementForIndex(int index) {
        return rowElement.getElementForIndex(index);
    }


    public String getText(IDocument doc) {
        return "";
    }


    public void appendLeaf(LeafElement leafElem) {
    }


    public IElement getLeaf(long offset) {
        return null;
    }

}
