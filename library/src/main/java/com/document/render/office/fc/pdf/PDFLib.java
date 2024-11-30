
package com.document.render.office.fc.pdf;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;


public class PDFLib {

    private static PDFLib lib = new PDFLib();


    static {
        System.loadLibrary("wxiweiPDF");
    }


    public float pageWidth;

    public float pageHeight;

    private int currentPageIndex = -1;

    private int pageCount = -1;



    private boolean isDrawPDFFinished = true;


    public static PDFLib getPDFLib() {
        return lib;
    }


    private static native int openFile(String filePath);


    private static native int getPageCount();


    private static native Rect[] getPagesSize();


    private static native void showPage(int pageIndex);


    private static native float getPageWidth();


    private static native float getPageHeight();


    private static native void drawPage(Bitmap bitmap, float pageW, float pageH, int patchX, int patchY,
                                        int patchW, int patchH);


    private static native RectF[] searchContent(String str);


    private static native int getHyperlinkCount(int pageIndex, float x, float y);


    private static native PDFHyperlinkInfo[] getHyperlinkInfo(int pageIndex);


    private static native PDFOutlineItem[] getOutline();


    private static native boolean hasOutline();




    private static native boolean hasPassword();


    private static native boolean authenticatePassword(String password);


    private static native int setStopFlag(int flag);


    private static native void destroy();


    private static native int convertFile(String infilename, String outfilename, int x, int y);


    private static native int convertPicture2PNG(String in, String out, String picType);


    public synchronized void openFileSync(String filename) throws Exception {
        if (openFile(filename) <= 0) {
            throw new Exception("Format error");
        }
        pageCount = -1;
        currentPageIndex = -1;
    }


    public int getPageCountSync() {
        if (pageCount < 0) {
            pageCount = getPageCount();
        }
        return pageCount;
    }


    private void showPageSync(int pageIndex) {
        if (pageCount == -1) {
            pageCount = getPageCount();
        }
        if (pageIndex > pageCount - 1) {
            pageIndex = pageCount - 1;
        } else if (pageIndex < 0) {
            pageIndex = 0;
        }
        if (this.currentPageIndex == pageIndex) {
            return;
        }
        this.currentPageIndex = pageIndex;
        showPage(pageIndex);
        this.pageWidth = getPageWidth();
        this.pageHeight = getPageHeight();
    }


    public Rect[] getAllPagesSize() {
        return getPagesSize();
    }


    public boolean isDrawPageSyncFinished() {
        return isDrawPDFFinished;
    }


    public synchronized void drawPageSync(Bitmap bitmap, int pageIndex, float pageWidth, float pageHeight,
                                          int paintX, int paintY, int paintWidth, int paintHeight, int drawObject) {
        isDrawPDFFinished = false;
        showPageSync(pageIndex);
        drawPage(bitmap, pageWidth, pageHeight, paintX, paintY, paintWidth, paintHeight);
        isDrawPDFFinished = true;
    }


    public synchronized int getHyperlinkCountSync(int pageIndex, float x, float y) {
        return getHyperlinkCount(pageIndex, x, y);
    }


    public synchronized PDFHyperlinkInfo[] getHyperlinkInfoSync(int pageIndex) {
        return getHyperlinkInfo(pageIndex);
    }


    public synchronized RectF[] searchContentSync(int pageIndex, String text) {
        showPageSync(pageIndex);
        return searchContent(text);
    }


    public synchronized boolean hasOutlineSync() {
        return hasOutline();
    }


    public synchronized PDFOutlineItem[] getOutlineSync() {
        return getOutline();
    }


    public synchronized boolean hasPasswordSync() {
        return hasPassword();
    }


    public synchronized boolean authenticatePasswordSync(String password) {
        return authenticatePassword(password);
    }
    ;


    public void setStopFlagSync(int flag) {
        setStopFlag(flag);
    }


    public int wmf2Jpg(String infilename, String outfilename, int width, int height) {
        return convertFile(infilename, outfilename, width, height);
    }


    public boolean convertToPNG(String in, String out, String picType) {
        if ("png".equalsIgnoreCase(picType) || "jpeg".equalsIgnoreCase(picType)) {
            return convertPicture2PNG(in, out, picType.toLowerCase()) != 0;
        }

        return false;
    }


    public synchronized void dispose() {

    }
}
