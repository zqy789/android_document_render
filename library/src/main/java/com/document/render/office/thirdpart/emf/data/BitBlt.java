

package com.document.render.office.thirdpart.emf.data;

import android.graphics.Bitmap;

import com.document.render.office.java.awt.Color;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.geom.AffineTransform;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFImageLoader;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class BitBlt extends EMFTag implements EMFConstants {

    private final static int size = 100;

    private Rectangle bounds;

    private int x, y, width, height;

    private int dwROP;

    private int xSrc, ySrc;

    private AffineTransform transform;

    private Color bkg;

    private int usage;

    private BitmapInfo bmi;


    private Bitmap image;

    public BitBlt() {
        super(76, 1);
    }

    public BitBlt(Rectangle bounds, int x, int y, int width, int height, AffineTransform transform,
                  Bitmap image, Color bkg) {
        this();
        this.bounds = bounds;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.dwROP = SRCCOPY;
        this.xSrc = 0;
        this.ySrc = 0;
        this.transform = transform;
        this.bkg = bkg;
        this.usage = DIB_RGB_COLORS;
        this.image = image;
        this.bmi = null;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        BitBlt tag = new BitBlt();

        tag.bounds = emf.readRECTL();
        tag.x = emf.readLONG();
        tag.y = emf.readLONG();
        tag.width = emf.readLONG();
        tag.height = emf.readLONG();
        tag.dwROP = emf.readDWORD();
        tag.xSrc = emf.readLONG();
        tag.ySrc = emf.readLONG();
        tag.transform = emf.readXFORM();
        tag.bkg = emf.readCOLORREF();
        tag.usage = emf.readDWORD();



        emf.readDWORD();
        int bmiSize = emf.readDWORD();

        emf.readDWORD();
        int bitmapSize = emf.readDWORD();


        if (bmiSize > 0) {
            tag.bmi = new BitmapInfo(emf);
        } else {
            tag.bmi = null;
        }

        if (bitmapSize > 0 && tag.bmi != null) {
            tag.image = EMFImageLoader.readImage(tag.bmi.getHeader(), tag.width, tag.height, emf,
                    bitmapSize, null);
        } else {
            tag.image = null;
        }

        return tag;
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds + "\n  x, y, w, h: " + x + " " + y + " "
                + width + " " + height + "\n  dwROP: 0x" + Integer.toHexString(dwROP)
                + "\n  xSrc, ySrc: " + xSrc + " " + ySrc + "\n  transform: " + transform + "\n  bkg: "
                + bkg + "\n  usage: " + usage + "\n"
                + ((bmi != null) ? bmi.toString() : "  bitmap: null");
    }


    public void render(EMFRenderer renderer) {
        if (image != null) {
            renderer.drawImage(image, transform);
        } else if (!bounds.isEmpty() && dwROP == 0x00F00021) {
            bounds.x = x;
            bounds.y = y;

            renderer.fillShape(bounds);
        }
        GeneralPath currentFigure = renderer.getFigure();

        if (currentFigure != null) {
            renderer.fillAndDrawShape(currentFigure);
        }
    }
}
