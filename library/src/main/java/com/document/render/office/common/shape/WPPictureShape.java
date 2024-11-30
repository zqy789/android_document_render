
package com.document.render.office.common.shape;


public class WPPictureShape extends WPAutoShape {

    private PictureShape pictureShape;


    public short getType() {
        return SHAPE_PICTURE;
    }

    public PictureShape getPictureShape() {
        return pictureShape;
    }

    public void setPictureShape(PictureShape pictureShape) {
        this.pictureShape = pictureShape;

        if (rect == null) {
            rect = pictureShape.getBounds();
        }
    }


    public boolean isWatermarkShape() {
        return false;
    }

    public void dispose() {
        if (pictureShape != null) {
            pictureShape.dispose();
            pictureShape = null;
        }
    }
}
