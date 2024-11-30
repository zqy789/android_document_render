
package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Stroke;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class WidenPath extends EMFTag {

    public WidenPath() {
        super(66, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return this;
    }


    public void render(EMFRenderer renderer) {
        GeneralPath currentPath = renderer.getPath();
        Stroke currentPenStroke = renderer.getPenStroke();



        if (currentPath != null && currentPenStroke != null) {
            GeneralPath newPath = new GeneralPath(
                    renderer.getWindingRule());
            newPath.append(currentPenStroke.createStrokedShape(currentPath), false);
            renderer.setPath(newPath);
        }
    }
}
