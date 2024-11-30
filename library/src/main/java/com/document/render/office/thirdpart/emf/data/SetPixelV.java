

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Color;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetPixelV extends EMFTag {

    private Point point;

    private Color color;

    public SetPixelV() {
        super(15, 1);
    }

    public SetPixelV(Point point, Color color) {
        this();
        this.point = point;
        this.color = color;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetPixelV(emf.readPOINTL(), emf.readCOLORREF());
    }

    public String toString() {
        return super.toString() + "\n  point: " + point + "\n  color: " + color;
    }
}
