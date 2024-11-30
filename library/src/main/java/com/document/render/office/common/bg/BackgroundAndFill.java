
package com.document.render.office.common.bg;

import com.document.render.office.common.picture.Picture;
import com.document.render.office.common.pictureefftect.PictureStretchInfo;
import com.document.render.office.common.shape.AbstractShape;
import com.document.render.office.system.IControl;

public class BackgroundAndFill {

    public static final byte FILL_NO = -1;

    public static final byte FILL_SOLID = 0;

    public static final byte FILL_PATTERN = 1;

    public static final byte FILL_SHADE_TILE = 2;

    public static final byte FILL_PICTURE = 3;



    public static final byte FILL_SHADE_RECT = 5;

    public static final byte FILL_SHADE_RADIAL = 4;

    public static final byte FILL_SHADE_SHAPE = 6;

    public static final byte FILL_SHADE_LINEAR = 7;

    public static final byte FILL_TEXTURE = 8;

    public static final byte FILL_BACKGROUND = 9;

    private boolean isSlideBackgroundFill;
    private PictureStretchInfo stretch;

    private byte fillType;

    private int bgColor;

    private int fgColor;

    private int pictureIndex;

    private AShader shader;


    public short getType() {
        return AbstractShape.SHAPE_BG_FILL;
    }

    public boolean isSlideBackgroundFill() {
        return isSlideBackgroundFill;
    }

    public void setSlideBackgroundFill(boolean isSlideBackgroundFill) {
        this.isSlideBackgroundFill = isSlideBackgroundFill;
    }


    public byte getFillType() {
        return fillType;
    }


    public void setFillType(byte fillType) {
        this.fillType = fillType;
    }


    public int getForegroundColor() {
        return fgColor;
    }


    public void setForegroundColor(int fgColor) {
        this.fgColor = fgColor;
    }


    public int getBackgoundColor() {
        return bgColor;
    }


    public void setBackgoundColor(int bgColor) {
        this.bgColor = bgColor;
    }


    public int getPictureIndex() {
        return pictureIndex;
    }


    public void setPictureIndex(int pictureIndex) {
        this.pictureIndex = pictureIndex;
    }


    public Picture getPicture(IControl control) {
        return control.getSysKit().getPictureManage().getPicture(pictureIndex);
    }

    public AShader getShader() {
        return shader;
    }

    public void setShader(AShader shader) {
        this.shader = shader;
    }

    public PictureStretchInfo getStretch() {
        return stretch;
    }

    public void setStretch(PictureStretchInfo stretch) {
        this.stretch = stretch;
    }


    public void dispose() {
        stretch = null;
        if (shader != null) {
            shader.dispose();
            shader = null;
        }
    }
}
