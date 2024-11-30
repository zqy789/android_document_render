
package com.document.render.office.ss.model.style;



public class Alignment {


    private short horizontal;
    private short vertival = CellStyle.VERTICAL_BOTTOM;
    private short rotation = 0;
    private boolean wrapText = false;
    private short indent = 0;


    public Alignment() {

    }


    public short getHorizontalAlign() {
        return horizontal;
    }


    public void setHorizontalAlign(short horizontal) {
        this.horizontal = horizontal;
    }


    public short getVerticalAlign() {
        return vertival;
    }


    public void setVerticalAlign(short v) {
        this.vertival = v;
    }



    public void setRotation(short rotation) {
        this.rotation = rotation;
    }


    public short getRotaion() {
        return rotation;
    }


    public boolean isWrapText() {
        return wrapText;
    }


    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
    }


    public short getIndent() {
        return indent;
    }


    public void setIndent(short indent) {
        this.indent = indent;
    }


    public void dispose() {

    }
}
