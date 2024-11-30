

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Bitmap;

import com.document.render.office.java.awt.Color;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFImageLoader;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class StretchDIBits extends EMFTag implements EMFConstants {

    public final static int size = 80;

    private Rectangle bounds;

    private int x, y, width, height;

    private int xSrc, ySrc, widthSrc, heightSrc;

    private int usage, dwROP;

    private Color bkg;

    private BitmapInfo bmi;


    private Bitmap image;

    public StretchDIBits() {
        super(81, 1);
    }

    public StretchDIBits(Rectangle bounds, int x, int y, int width, int height,
                         Bitmap image, Color bkg) {
        this();
        this.bounds = bounds;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xSrc = 0;
        this.ySrc = 0;
        this.widthSrc = image.getWidth();
        this.heightSrc = image.getHeight();
        this.usage = DIB_RGB_COLORS;
        this.dwROP = SRCCOPY;

        this.bkg = bkg;
        this.image = image;
        this.bmi = null;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        StretchDIBits tag = new StretchDIBits();
        tag.bounds = emf.readRECTL();
        tag.x = emf.readLONG();
        tag.y = emf.readLONG();
        tag.xSrc = emf.readLONG();
        tag.ySrc = emf.readLONG();
        tag.width = emf.readLONG();
        tag.height = emf.readLONG();

        emf.readDWORD();
        emf.readDWORD();
        emf.readDWORD();
        emf.readDWORD();

        tag.usage = emf.readDWORD();
        tag.dwROP = emf.readDWORD();
        tag.widthSrc = emf.readLONG();
        tag.heightSrc = emf.readLONG();


        tag.bmi = new BitmapInfo(emf);

        tag.image = EMFImageLoader.readImage(tag.bmi.getHeader(), tag.width, tag.height, emf, len
                - 72 - BitmapInfoHeader.size, null);

        return tag;
    }


    public String toString() {
        return super.toString() + "\n  bounds: " + bounds + "\n  x, y, w, h: " + x + " " + y + " "
                + width + " " + height + "\n  xSrc, ySrc, widthSrc, heightSrc: " + xSrc + " " + ySrc
                + " " + widthSrc + " " + heightSrc + "\n  usage: " + usage + "\n  dwROP: " + dwROP
                + "\n  bkg: " + bkg + "\n" + bmi.toString();
    }


    public void render(EMFRenderer renderer) {






        if (image != null) {
            renderer.drawImage(image, x, y, widthSrc, heightSrc);
        }
    }
}
