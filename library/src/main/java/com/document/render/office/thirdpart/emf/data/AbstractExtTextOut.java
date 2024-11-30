

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;


public abstract class AbstractExtTextOut extends EMFTag implements EMFConstants {

    private Rectangle bounds;

    private int mode;

    private float xScale, yScale;


    protected AbstractExtTextOut(int id, int version, Rectangle bounds, int mode, float xScale,
                                 float yScale) {

        super(id, version);
        this.bounds = bounds;
        this.mode = mode;
        this.xScale = xScale;
        this.yScale = yScale;
    }

    public abstract Text getText();

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds + "\n  mode: " + mode + "\n  xScale: "
                + xScale + "\n  yScale: " + yScale + "\n" + getText().toString();
    }


    public void render(EMFRenderer renderer) {
        Text text = getText();
        renderer.drawOrAppendText(text.getString(), text.getPos().x, text.getPos().y);
    }
}
