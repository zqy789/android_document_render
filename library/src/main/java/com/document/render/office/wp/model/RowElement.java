
package com.document.render.office.wp.model;

import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.simpletext.model.AbstractElement;
import com.document.render.office.simpletext.model.ElementCollectionImpl;
import com.document.render.office.simpletext.model.IElement;


public class RowElement extends AbstractElement {

    private ElementCollectionImpl cellElement;


    public RowElement() {
        super();
        cellElement = new ElementCollectionImpl(10);
    }


    public short getType() {
        return WPModelConstant.TABLE_ROW_ELEMENT;
    }


    public void appendCell(CellElement cellElem) {
        cellElement.addElement(cellElem);
    }


    public IElement getCellElement(long offset) {
        return cellElement.getElement(offset);
    }


    public IElement getElementForIndex(int index) {
        return cellElement.getElementForIndex(index);
    }


    public void insertElementForIndex(IElement element, int index) {
        cellElement.insertElementForIndex(element, index);
    }


    public int getCellNumber() {
        return cellElement.size();
    }

}
