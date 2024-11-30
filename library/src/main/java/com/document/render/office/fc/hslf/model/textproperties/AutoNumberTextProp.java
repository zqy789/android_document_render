
package com.document.render.office.fc.hslf.model.textproperties;


public class AutoNumberTextProp {

    private int numberingType = -1;

    private int start = 0;


    public AutoNumberTextProp() {

    }


    public AutoNumberTextProp(int numberingType, int start) {
        this.numberingType = numberingType;
        this.start = start;
    }


    public int getNumberingType() {
        return numberingType;
    }


    public void setNumberingType(int numberingType) {
        this.numberingType = numberingType;
    }


    public int getStart() {
        return start;
    }


    public void setStart(int start) {
        this.start = start;
    }


    public void dispose() {

    }
}
