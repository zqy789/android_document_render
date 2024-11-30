
package com.document.render.office.wp.model;

import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.simpletext.model.AbstractElement;


public class CellElement extends AbstractElement {

    public CellElement() {
        super();
    }


    public short getType() {
        return WPModelConstant.TABLE_CELL_ELEMENT;
    }
}
