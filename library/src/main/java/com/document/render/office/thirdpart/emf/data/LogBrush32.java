

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Color;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;

import java.io.IOException;
import java.util.logging.Logger;


public class LogBrush32 implements EMFConstants, GDIObject {

    private int style;

    private Color color;

    private int hatch;

    public LogBrush32(int style, Color color, int hatch) {
        this.style = style;
        this.color = color;
        this.hatch = hatch;
    }

    public LogBrush32(EMFInputStream emf) throws IOException {
        style = emf.readUINT();
        color = emf.readCOLORREF();
        hatch = emf.readULONG();
    }

    public String toString() {
        return "  LogBrush32\n" + "    style: " + style + "\n    color: " + color + "\n    hatch: "
                + hatch;
    }


    public void render(EMFRenderer renderer) {
        if (style == EMFConstants.BS_SOLID) {
            renderer.setBrushPaint(color);
        } else if (style == EMFConstants.BS_NULL) {



            renderer.setBrushPaint(new Color(0, 0, 0, 0));




        } else {
            Logger logger = Logger.getLogger("org.freehep.graphicsio.emf");
            logger.warning("LogBrush32 style not supported: " + toString());
            renderer.setBrushPaint(color);
        }
    }
}
