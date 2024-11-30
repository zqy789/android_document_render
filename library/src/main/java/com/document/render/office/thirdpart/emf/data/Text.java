

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFConstants;


public abstract class Text implements EMFConstants {

    Point pos;

    String string;

    int options;

    Rectangle bounds;

    int[] widths;

    protected Text(Point pos, String string, int options, Rectangle bounds, int[] widths) {
        this.pos = pos;
        this.string = string;
        this.options = options;
        this.bounds = bounds;
        this.widths = widths;
    }

    public Point getPos() {
        return pos;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getString() {
        return string;
    }
}
