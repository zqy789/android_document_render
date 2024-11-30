

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Color;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;

import java.io.IOException;


public class LogPen extends AbstractPen {

    private int penStyle;

    private int width;

    private Color color;

    public LogPen(int penStyle, int width, Color color) {
        this.penStyle = penStyle;
        this.width = width;
        this.color = color;
    }

    public LogPen(EMFInputStream emf) throws IOException {
        penStyle = emf.readDWORD();
        width = emf.readDWORD();

        emf.readDWORD();
        color = emf.readCOLORREF();
    }

    public String toString() {
        return "  LogPen\n" + "    penstyle: " + penStyle + "\n    width: " + width
                + "\n    color: " + color;
    }


    public void render(EMFRenderer renderer) {
        renderer.setUseCreatePen(true);
        renderer.setPenPaint(color);
        renderer.setPenStroke(createStroke(renderer, penStyle, null, width));
    }
}
