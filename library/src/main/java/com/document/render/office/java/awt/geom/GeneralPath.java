

package com.document.render.office.java.awt.geom;

import com.document.render.office.java.awt.Shape;


public final class GeneralPath extends Path2D.Float {

    private static final long serialVersionUID = -8327096662768731142L;


    public GeneralPath() {
        super(WIND_NON_ZERO, INIT_SIZE);
    }


    public GeneralPath(int rule) {
        super(rule, INIT_SIZE);
    }


    public GeneralPath(int rule, int initialCapacity) {
        super(rule, initialCapacity);
    }


    public GeneralPath(Shape s) {
        super(s, null);
    }

    GeneralPath(int windingRule, byte[] pointTypes, int numTypes, float[] pointCoords, int numCoords) {


        this.windingRule = windingRule;
        this.pointTypes = pointTypes;
        this.numTypes = numTypes;
        this.floatCoords = pointCoords;
        this.numCoords = numCoords;
    }
}
