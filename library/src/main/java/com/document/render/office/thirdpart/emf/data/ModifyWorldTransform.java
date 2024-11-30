

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.geom.AffineTransform;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ModifyWorldTransform extends EMFTag implements EMFConstants {

    private AffineTransform transform;

    private int mode;

    public ModifyWorldTransform() {
        super(36, 1);
    }

    public ModifyWorldTransform(AffineTransform transform, int mode) {
        this();
        this.transform = transform;
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new ModifyWorldTransform(emf.readXFORM(), emf.readDWORD());
    }

    public String toString() {
        return super.toString() + "\n  transform: " + transform + "\n  mode: " + mode;
    }


    public void render(EMFRenderer renderer) {



        if (mode == EMFConstants.MWT_IDENTITY) {
            if (renderer.getPath() != null) {
                renderer.setPathTransform(new AffineTransform());
            } else {
                renderer.resetTransformation();
            }
        }





        else if (mode == EMFConstants.MWT_LEFTMULTIPLY) {
            if (renderer.getPath() != null) {
                renderer.getPathTransform().concatenate(transform);
                renderer.transform(transform);
            } else {
                renderer.transform(transform);
            }
        }





        else if (mode != EMFConstants.MWT_RIGHTMULTIPLY) {




        }


        else {
        }
    }
}
