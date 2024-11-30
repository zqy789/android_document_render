

package com.document.render.office.ss.model;


public abstract class Anchor {


    protected int dx1;

    protected int dy1;

    protected int dx2;

    protected int dy2;

    public Anchor() {
    }


    public Anchor(int dx1, int dy1, int dx2, int dy2) {
        this.dx1 = dx1;
        this.dy1 = dy1;
        this.dx2 = dx2;
        this.dy2 = dy2;
    }


    public int getDx1() {
        return dx1;
    }


    public void setDx1(int dx1) {
        this.dx1 = dx1;
    }


    public int getDy1() {
        return dy1;
    }


    public void setDy1(int dy1) {
        this.dy1 = dy1;
    }


    public int getDy2() {
        return dy2;
    }


    public void setDy2(int dy2) {
        this.dy2 = dy2;
    }


    public int getDx2() {
        return dx2;
    }


    public void setDx2(int dx2) {
        this.dx2 = dx2;
    }

    public abstract boolean isHorizontallyFlipped();

    public abstract boolean isVerticallyFlipped();
}
