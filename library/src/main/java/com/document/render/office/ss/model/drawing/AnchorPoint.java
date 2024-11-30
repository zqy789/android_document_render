
package com.document.render.office.ss.model.drawing;


public class AnchorPoint {


    protected int row;

    protected short col;

    protected int dx;

    protected int dy;

    public AnchorPoint() {
    }

    public AnchorPoint(short col, int row, int dx, int dy) {
        this.row = row;
        this.col = col;
        this.dx = dx;
        this.dy = dy;
    }


    public int getRow() {
        return row;
    }


    public void setRow(int row) {
        this.row = row;
    }


    public short getColumn() {
        return col;
    }


    public void setColumn(short col) {
        this.col = col;
    }


    public int getDX() {
        return dx;
    }


    public void setDX(int dx) {
        this.dx = dx;
    }


    public int getDY() {
        return dy;
    }

    public void setDY(int dy) {
        this.dy = dy;
    }

    public void dispose() {

    }
}
