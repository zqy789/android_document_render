
package com.document.render.office.common.picture;



public class VectorgraphConverterThread extends Thread {
    private PictureConverterMgr converterMgr;
    private byte type;
    private String sourPath;
    private String destPath;
    private int picWidth;
    private int picHeight;
    public VectorgraphConverterThread(PictureConverterMgr converterMgr, byte type, String srcPath, String dstPath, int width, int height) {
        this.converterMgr = converterMgr;
        this.type = type;

        this.sourPath = srcPath;
        this.destPath = dstPath;
        this.picWidth = width;
        this.picHeight = height;
    }

    public void run() {
        converterMgr.convertWMF_EMF(type, sourPath, destPath, picWidth, picHeight, false);
    }
}
