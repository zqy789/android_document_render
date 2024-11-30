
package com.document.render.office.pg.model.tableStyle;

import com.document.render.office.fc.dom4j.Element;


public class TableCellBorders {

    private Element left;
    private Element top;
    private Element right;
    private Element bottom;

    public Element getLeftBorder() {
        return left;
    }

    public void setLeftBorder(Element left) {
        this.left = left;
    }

    public Element getTopBorder() {
        return top;
    }

    public void setTopBorder(Element top) {
        this.top = top;
    }

    public Element getRightBorder() {
        return right;
    }

    public void setRightBorder(Element right) {
        this.right = right;
    }

    public Element getBottomBorder() {
        return bottom;
    }

    public void setBottomBorder(Element bottom) {
        this.bottom = bottom;
    }
}
