

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.java.awt.Color;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class SetTextColor extends EMFTag {

    private Color color;

    public SetTextColor() {
        super(24, 1);
    }

    public SetTextColor(Color color) {
        this();
        this.color = color;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new SetTextColor(emf.readCOLORREF());
    }


    public String toString() {
        return super.toString() + "\n  color: " + color;
    }


    public void render(EMFRenderer renderer) {
        renderer.setTextColor(color);
    }
}
