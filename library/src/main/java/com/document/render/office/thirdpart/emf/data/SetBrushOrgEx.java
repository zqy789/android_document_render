

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetBrushOrgEx extends EMFTag {

    private Point point;

    public SetBrushOrgEx() {
        super(13, 1);
    }

    public SetBrushOrgEx(Point point) {
        this();
        this.point = point;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetBrushOrgEx(emf.readPOINTL());
    }

    public String toString() {
        return super.toString() + "\n  point: " + point;
    }


    public void render(EMFRenderer renderer) {



        renderer.setBrushOrigin(point);
    }
}
