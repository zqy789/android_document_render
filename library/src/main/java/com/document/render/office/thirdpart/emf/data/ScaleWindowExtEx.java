

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ScaleWindowExtEx extends EMFTag {

    private int xNum, xDenom, yNum, yDenom;

    public ScaleWindowExtEx() {
        super(32, 1);
    }

    public ScaleWindowExtEx(int xNum, int xDenom, int yNum, int yDenom) {
        this();
        this.xNum = xNum;
        this.xDenom = xDenom;
        this.yNum = yNum;
        this.yDenom = yDenom;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new ScaleWindowExtEx(emf.readLONG(), emf.readLONG(), emf.readLONG(), emf.readLONG());
    }

    public String toString() {
        return super.toString() + "\n  xNum: " + xNum + "\n  xDenom: " + xDenom + "\n  yNum: "
                + yNum + "\n  yDenom: " + yDenom;
    }
}
