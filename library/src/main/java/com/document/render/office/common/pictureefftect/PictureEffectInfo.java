
package com.document.render.office.common.pictureefftect;



public class PictureEffectInfo {

    private PictureCroppedInfo croppedRect;

    private PictureStretchInfo stretch;

    private Boolean grayscl;

    private Float threshold;

    private Float sat;

    private Float bright;

    private Float contrast;
    private Integer transparentColor;
    private Integer alpha;

    public PictureEffectInfo() {

    }

    public PictureCroppedInfo getPictureCroppedInfor() {
        return croppedRect;
    }

    public void setPictureCroppedInfor(PictureCroppedInfo rect) {
        this.croppedRect = rect;
    }

    public PictureStretchInfo getPictureStretchInfo() {
        return stretch;
    }

    public void setPictureStretchInfo(PictureStretchInfo stretch) {
        this.stretch = stretch;
    }

    public void setGrayScale(boolean grayscl) {
        this.grayscl = grayscl;
    }

    public Boolean isGrayScale() {
        return grayscl;
    }

    public Float getBlackWhiteThreshold() {
        return threshold;
    }

    public void setBlackWhiteThreshold(float threshold) {
        this.threshold = threshold;
    }

    public Float getSaturation() {
        return sat;
    }

    public void setSaturation(float sat) {
        this.sat = sat;
    }

    public Float getBrightness() {
        return bright;
    }

    public void setBrightness(float bright) {
        this.bright = bright;
    }

    public Float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
    }

    public Integer getTransparentColor() {
        return transparentColor;
    }

    public void setTransparentColor(int color) {
        this.transparentColor = color;
    }

    public Integer getAlpha() {
        return alpha;
    }

    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
    }

    public void dispose() {
        croppedRect = null;
        grayscl = null;
        threshold = null;
        sat = null;
        bright = null;
        contrast = null;
        alpha = null;
    }
}
