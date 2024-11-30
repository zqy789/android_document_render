
package com.document.render.office.ss.model.style;


public class CellBorder {
    private BorderStyle left;
    private BorderStyle top;
    private BorderStyle right;
    private BorderStyle bottom;

    public CellBorder() {
        left = new BorderStyle();
        top = new BorderStyle();
        right = new BorderStyle();
        bottom = new BorderStyle();
    }

    public BorderStyle getLeftBorder() {
        return left;
    }

    public void setLeftBorder(BorderStyle left) {
        this.left = left;
    }

    public BorderStyle getTopBorder() {
        return top;
    }

    public void setTopBorder(BorderStyle top) {
        this.top = top;
    }

    public BorderStyle getRightBorder() {
        return right;
    }

    public void setRightBorder(BorderStyle right) {
        this.right = right;
    }

    public BorderStyle getBottomBorder() {
        return bottom;
    }

    public void setBottomBorder(BorderStyle bottom) {
        this.bottom = bottom;
    }


    public void dispose() {
        if (left != null) {
            left.dispose();
            left = null;
        }

        if (top != null) {
            top.dispose();
            top = null;
        }

        if (right != null) {
            right.dispose();
            right = null;
        }

        if (bottom != null) {
            bottom.dispose();
            bottom = null;
        }
    }
}
