
package com.document.render.office.common.pictureefftect;




public class PictureCroppedInfo {

    private float leftOff;
    private float topOff;
    private float rightOff;
    private float bottomOff;

    public PictureCroppedInfo(float leftOff, float topOff, float rightOff, float bottomOff) {
        this.leftOff = leftOff;
        this.topOff = topOff;
        this.rightOff = rightOff;
        this.bottomOff = bottomOff;
    }

    public float getLeftOff() {
        return leftOff;
    }

    public void setLeftOff(float leftOff) {
        this.leftOff = leftOff;
    }

    public float getTopOff() {
        return topOff;
    }

    public void setTopOff(float topOff) {
        this.topOff = topOff;
    }

    public float getRightOff() {
        return rightOff;
    }

    public void setRightOff(float rightOff) {
        this.rightOff = rightOff;
    }

    public float getBottomOff() {
        return bottomOff;
    }

    public void setBottomOff(float bottomOff) {
        this.bottomOff = bottomOff;
    }
}
