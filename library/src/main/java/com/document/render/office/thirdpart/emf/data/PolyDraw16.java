

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class PolyDraw16 extends EMFTag implements EMFConstants {

    private Rectangle bounds;

    private Point[] points;

    private byte[] types;

    public PolyDraw16() {
        super(92, 1);
    }

    public PolyDraw16(Rectangle bounds, Point[] points, byte[] types) {
        this();
        this.bounds = bounds;
        this.points = points;
        this.types = types;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        int n;
        return new PolyDraw16(emf.readRECTL(), emf.readPOINTS(n = emf.readDWORD()), emf.readBYTE(n));
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds + "\n  #points: " + points.length;
    }
}
