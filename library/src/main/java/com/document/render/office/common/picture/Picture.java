
package com.document.render.office.common.picture;

public class Picture {

    public static final byte EMF = 2;

    public static final byte WMF = 3;

    public static final byte PICT = 4;

    public static final byte JPEG = 5;

    public static final byte PNG = 6;

    public static final byte DIB = 7;

    public static final byte GIF = 8;

    public static final String EMF_TYPE = "emf";

    public static final String WMF_TYPE = "wmf";

    public static final String PICT_TYPE = "pict";

    public static final String JPEG_TYPE = "jpeg";

    public static final String PNG_TYPE = "png";

    public static final String DIB_TYPE = "dib";

    public static final String GIF_TYPE = "gif";

    private byte type;

    private byte[] data;

    private short zoomX;

    private short zoomY;

    private String tempFilePath;


    public String getTempFilePath() {
        return tempFilePath;
    }


    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }


    public byte[] getData() {
        return data;
    }


    public void setData(byte[] data) {
        this.data = data;
    }


    public byte getPictureType() {
        return type;
    }


    public void setPictureType(byte type) {
        this.type = type;
    }


    public void setPictureType(String typeName) {
        if (typeName.equalsIgnoreCase(EMF_TYPE)) {
            this.type = EMF;
        } else if (typeName.equalsIgnoreCase(WMF_TYPE)) {
            this.type = WMF;
        } else if (typeName.equalsIgnoreCase(PICT_TYPE)) {
            this.type = PICT;
        } else if (typeName.equalsIgnoreCase(JPEG_TYPE)) {
            this.type = JPEG;
        } else if (typeName.equalsIgnoreCase(PNG_TYPE)) {
            this.type = PNG;
        } else if (typeName.equalsIgnoreCase(DIB_TYPE)) {
            this.type = DIB;
        } else if (typeName.equalsIgnoreCase(GIF_TYPE)) {
            this.type = GIF;
        }
    }


    public void dispose() {
        tempFilePath = null;
    }


    public short getZoomX() {
        return zoomX;
    }


    public void setZoomX(short zoomX) {
        this.zoomX = zoomX;
    }


    public short getZoomY() {
        return zoomY;
    }


    public void setZoomY(short zoomY) {
        this.zoomY = zoomY;
    }
}
