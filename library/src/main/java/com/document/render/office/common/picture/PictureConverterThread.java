package com.document.render.office.common.picture;

public class PictureConverterThread extends Thread {
    private PictureConverterMgr converterMgr;
    private String type;
    private String sourPath;
    private String destPath;
    public PictureConverterThread(PictureConverterMgr converterMgr, String srcPath, String dstPath, String type) {
        this.converterMgr = converterMgr;
        this.type = type;

        this.sourPath = srcPath;
        this.destPath = dstPath;
    }

    public void run() {
        converterMgr.convertPNG(sourPath, destPath, type, false);
    }
}
