
package com.document.render.office.common.shape;


public class LineShape extends AutoShape {

    private Arrow startArrow;

    private Arrow endArrow;


    public LineShape() {

    }


    public short getType() {
        return SHAPE_LINE;
    }

    public void createStartArrow(byte type, int width, int length) {
        if (startArrow == null) {
            startArrow = new Arrow(type, width, length);
        } else {
            startArrow.setType(type);
            startArrow.setWidth(width);
            startArrow.setLength(length);
        }
    }

    public void createEndArrow(byte type, int width, int length) {
        if (endArrow == null) {
            endArrow = new Arrow(type, width, length);
        } else {
            endArrow.setType(type);
            endArrow.setWidth(width);
            endArrow.setLength(length);
        }
    }


    public boolean getStartArrowhead() {
        return startArrow != null;
    }

    public int getStartArrowWidth() {
        if (startArrow != null) {
            return startArrow.getWidth();
        }
        return -1;
    }

    public void setStartArrowWidth(int startArrowWidth) {
        if (startArrow != null) {
            startArrow.setWidth(startArrowWidth);
        }
    }

    public int getStartArrowLength() {
        if (startArrow != null) {
            return startArrow.getLength();
        }
        return -1;
    }

    public void setStartArrowLength(int startArrowLength) {
        if (startArrow != null) {
            startArrow.setLength(startArrowLength);
        }
    }


    public byte getStartArrowType() {
        if (startArrow != null) {
            return startArrow.getType();
        }

        return Arrow.Arrow_None;
    }


    public void setStartArrowType(byte type) {
        if (startArrow != null) {
            startArrow.setType(type);
        }
    }


    public boolean getEndArrowhead() {
        return endArrow != null;
    }

    public int getEndArrowWidth() {
        if (endArrow != null) {
            return endArrow.getWidth();
        }
        return -1;
    }

    public void setEndArrowWidth(int endArrowWidth) {
        if (endArrow != null) {
            endArrow.setWidth(endArrowWidth);
        }
    }

    public int getEndArrowLength() {
        if (endArrow != null) {
            return endArrow.getLength();
        }
        return -1;
    }

    public void setEndArrowLength(int endArrowLength) {
        if (endArrow != null) {
            endArrow.setLength(endArrowLength);
        }
    }


    public byte getEndArrowType() {
        if (endArrow != null) {
            return endArrow.getType();
        }
        return Arrow.Arrow_None;
    }


    public void setEndArrowType(byte type) {
        if (endArrow != null) {
            endArrow.setType(type);
        }
    }

    public Arrow getStartArrow() {
        return startArrow;
    }

    public Arrow getEndArrow() {
        return endArrow;
    }


    public void dispose() {
        startArrow = null;
        endArrow = null;
    }
}
