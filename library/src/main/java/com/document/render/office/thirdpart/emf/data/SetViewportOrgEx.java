

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetViewportOrgEx extends EMFTag {

    private Point point;

    public SetViewportOrgEx() {
        super(12, 1);
    }

    public SetViewportOrgEx(Point point) {
        this();
        this.point = point;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetViewportOrgEx(emf.readPOINTL());
    }

    public String toString() {
        return super.toString() + "\n  point: " + point;
    }


    public void render(EMFRenderer renderer) {









        renderer.setViewportOrigin(point);

    }
}
