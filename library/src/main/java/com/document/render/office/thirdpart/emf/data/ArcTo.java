
package com.document.render.office.thirdpart.emf.data;

import android.graphics.Point;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.Arc2D;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ArcTo extends AbstractArc {

    public ArcTo() {
        super(55, 1, null, null, null);
    }

    public ArcTo(Rectangle bounds, Point start, Point end) {
        super(55, 1, bounds, start, end);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new ArcTo(
                emf.readRECTL(),
                emf.readPOINTL(),
                emf.readPOINTL());
    }

    
    public void render(EMFRenderer renderer) {
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

        renderer.getFigure().append(
                getShape(renderer, Arc2D.OPEN), true);
    }
}
