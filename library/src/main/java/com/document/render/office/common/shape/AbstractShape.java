
package com.document.render.office.common.shape;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.pg.animate.IAnimation;


public class AbstractShape implements IShape {

    public static final short SHAPE_PICTURE = 0;

    public static final short SHAPE_TEXTBOX = SHAPE_PICTURE + 1;

    public static final short SHAPE_AUTOSHAPE = SHAPE_TEXTBOX + 1;

    public static final short SHAPE_BG_FILL = SHAPE_AUTOSHAPE + 1;

    public static final short SHAPE_LINE = SHAPE_BG_FILL + 1;

    public static final short SHAPE_CHART = SHAPE_LINE + 1;

    public static final short SHAPE_TABLE = SHAPE_CHART + 1;

    public static final short SHAPE_GROUP = SHAPE_TABLE + 1;

    public static final short SHAPE_SMARTART = SHAPE_GROUP + 1;

    protected Rectangle rect;

    private IShape parent;

    private int grpSpID = -1;

    private int id;

    private BackgroundAndFill bgFill;

    private boolean flipH;

    private boolean flipV;

    private float angle;
    private boolean hidden;
    private IAnimation animation;
    private boolean hasLine;
    private Line line;
    private int placeHolderID;


    public short getType() {
        return -1;
    }


    public IShape getParent() {
        return parent;
    }


    public void setParent(IShape shape) {
        this.parent = shape;
    }


    public int getGroupShapeID() {
        return grpSpID;
    }


    public void setGroupShapeID(int id) {
        grpSpID = id;
    }

    public int getShapeID() {
        return id;
    }

    public void setShapeID(int id) {
        this.id = id;
    }


    public Rectangle getBounds() {
        return rect;
    }


    public void setBounds(Rectangle rect) {
        this.rect = rect;
    }


    public Object getData() {
        return null;
    }


    public void setData(Object data) {

    }


    public BackgroundAndFill getBackgroundAndFill() {
        return bgFill;
    }


    public void setBackgroundAndFill(BackgroundAndFill bgFill) {
        this.bgFill = bgFill;
    }


    public boolean getFlipHorizontal() {
        return flipH;
    }


    public void setFlipHorizontal(boolean flipH) {
        this.flipH = flipH;
    }


    public boolean getFlipVertical() {
        return flipV;
    }


    public void setFlipVertical(boolean flipV) {
        this.flipV = flipV;
    }


    public float getRotation() {
        return angle;
    }


    public void setRotation(float angle) {
        this.angle = angle;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public IAnimation getAnimation() {
        return animation;
    }


    public void setAnimation(IAnimation animation) {
        this.animation = animation;
    }


    public boolean hasLine() {
        return line != null;
    }


    public Line createLine() {
        line = new Line();
        return line;
    }


    public Line getLine() {
        return line;
    }

    public void setLine(boolean hasLine) {
        this.hasLine = hasLine;
        if (hasLine && line == null) {
            line = new Line();
        }
    }

    public void setLine(Line line) {
        this.line = line;
        if (line != null) {
            this.hasLine = true;
        }
    }


    public int getPlaceHolderID() {
        return placeHolderID;
    }


    public void setPlaceHolderID(int placeHolderID) {
        this.placeHolderID = placeHolderID;
    }


    public void dispose() {
        if (parent != null) {
            parent.dispose();
            parent = null;
        }
        rect = null;
        if (animation != null) {
            animation.dispose();
            animation = null;
        }
        if (bgFill != null) {
            bgFill.dispose();
            bgFill = null;
        }

        if (line != null) {
            line.dispose();
            line = null;
        }
    }
}
