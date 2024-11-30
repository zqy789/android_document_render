
package com.document.render.office.thirdpart.emf.data;


import android.graphics.Matrix;

import com.document.render.office.java.awt.Shape;
import com.document.render.office.java.awt.geom.Area;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;


public abstract class AbstractClipPath extends EMFTag {

    private int mode;

    protected AbstractClipPath(int id, int version, int mode) {
        super(id, version);
        this.mode = mode;
    }

    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }

    public int getMode() {
        return mode;
    }


    public void render(EMFRenderer renderer, Shape shape) {
        if (shape != null) {


            if (mode == EMFConstants.RGN_AND) {
                renderer.clip(shape);
            }

            else if (mode == EMFConstants.RGN_COPY) {


                Matrix matrix = renderer.getMatrix();


                renderer.resetTransformation();

                renderer.setClip(renderer.getInitialClip());

                renderer.setMatrix(matrix);
                renderer.clip(shape);
            }


            else if (mode == EMFConstants.RGN_DIFF) {
                Shape clip = renderer.getClip();
                if (clip != null) {
                    Area a = new Area(shape);
                    a.subtract(new Area(clip));
                    renderer.setClip(a);
                } else {
                    renderer.setClip(shape);
                }
            }


            else if (mode == EMFConstants.RGN_OR) {
                GeneralPath path = new GeneralPath(shape);
                Shape clip = renderer.getClip();
                if (clip != null) {
                    path.append(clip, false);
                }
                renderer.setClip(path);
            }


            else if (mode == EMFConstants.RGN_XOR) {
                Shape clip = renderer.getClip();
                if (clip != null) {
                    Area a = new Area(shape);
                    a.exclusiveOr(new Area(clip));
                    renderer.setClip(a);
                } else {
                    renderer.setClip(shape);
                }
            }
        }


        renderer.setPath(null);
    }
}
