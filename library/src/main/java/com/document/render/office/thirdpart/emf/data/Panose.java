

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;

import java.io.IOException;


public class Panose implements EMFConstants {

    private int familyType;

    private int serifStyle;

    private int weight;

    private int proportion;

    private int contrast;

    private int strokeVariation;

    private int armStyle;

    private int letterForm;

    private int midLine;

    private int xHeight;

    public Panose() {

        this.familyType = PAN_NO_FIT;
        this.serifStyle = PAN_NO_FIT;
        this.proportion = PAN_NO_FIT;
        this.weight = PAN_NO_FIT;
        this.contrast = PAN_NO_FIT;
        this.strokeVariation = PAN_NO_FIT;
        this.armStyle = PAN_ANY;
        this.letterForm = PAN_ANY;
        this.midLine = PAN_ANY;
        this.xHeight = PAN_ANY;
    }

    public Panose(EMFInputStream emf) throws IOException {
        familyType = emf.readBYTE();
        serifStyle = emf.readBYTE();
        proportion = emf.readBYTE();
        weight = emf.readBYTE();
        contrast = emf.readBYTE();
        strokeVariation = emf.readBYTE();
        armStyle = emf.readBYTE();
        letterForm = emf.readBYTE();
        midLine = emf.readBYTE();
        xHeight = emf.readBYTE();
    }


    public String toString() {
        return "  Panose\n" + "    familytype: " + familyType + "\n    serifStyle: " + serifStyle
                + "\n    weight: " + weight + "\n    proportion: " + proportion + "\n    contrast: "
                + contrast + "\n    strokeVariation: " + strokeVariation + "\n    armStyle: "
                + armStyle + "\n    letterForm: " + letterForm + "\n    midLine: " + midLine
                + "\n    xHeight: " + xHeight;
    }
}
