
package com.document.render.office.common.shape;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;
import com.document.render.office.java.awt.Rectanglef;


public class TableCell {

    protected Rectanglef rect;

    private Line left;

    private Line right;

    private Line top;

    private Line bottom;

    private TextBox textBox;

    private BackgroundAndFill bgFill;


    public TableCell() {
        super();
    }


    public Line getLeftLine() {
        return left;
    }


    public void setLeftLine(Line left) {
        this.left = left;
    }


    public Line getRightLine() {
        return right;
    }


    public void setRightLine(Line right) {
        this.right = right;
    }


    public Line getTopLine() {
        return top;
    }


    public void setTopLine(Line top) {
        this.top = top;
    }


    public Line getBottomLine() {
        return bottom;
    }


    public void setBottomLine(Line bottom) {
        this.bottom = bottom;
    }


    public TextBox getText() {
        return textBox;
    }


    public void setText(TextBox textBox) {
        this.textBox = textBox;
    }


    public Rectanglef getBounds() {
        return rect;
    }


    public void setBounds(Rectanglef rect) {
        this.rect = rect;
    }


    public BackgroundAndFill getBackgroundAndFill() {
        return bgFill;
    }


    public void setBackgroundAndFill(BackgroundAndFill bgFill) {
        this.bgFill = bgFill;
    }


    public void dispose() {
        if (textBox != null) {
            textBox.dispose();
            textBox = null;
        }
        rect = null;
        if (bgFill != null) {
            bgFill.dispose();
            bgFill = null;
        }
    }
}
