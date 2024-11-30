
package com.document.render.office.ss.model.drawing;


public class CellAnchor {
    public final static short ONECELLANCHOR = 0x0;

    public final static short TWOCELLANCHOR = 0x1;
    protected AnchorPoint start;
    private short type = TWOCELLANCHOR;
    private AnchorPoint end;

    private int width;
    private int height;

    public CellAnchor(short type) {
        this.type = type;
    }

    public short getType() {
        return type;
    }

    public AnchorPoint getStart() {
        return start;
    }

    public void setStart(AnchorPoint start) {
        this.start = start;
    }

    public AnchorPoint getEnd() {
        return end;
    }

    public void setEnd(AnchorPoint end) {
        this.end = end;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void dispose() {
        if (start != null) {
            start.dispose();
            start = null;
        }

        if (end != null) {
            end.dispose();
            end = null;
        }
    }

}
