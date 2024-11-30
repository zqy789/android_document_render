

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;

import java.io.IOException;


public class BlendFunction implements EMFConstants {

    public static final int size = 4;

    private int blendOp;

    private int blendFlags;

    private int sourceConstantAlpha;

    private int alphaFormat;

    public BlendFunction(int blendOp, int blendFlags, int sourceConstantAlpha, int alphaFormat) {
        this.blendOp = blendOp;
        this.blendFlags = blendFlags;
        this.sourceConstantAlpha = sourceConstantAlpha;
        this.alphaFormat = alphaFormat;
    }

    public BlendFunction(EMFInputStream emf) throws IOException {
        blendOp = emf.readUnsignedByte();
        blendFlags = emf.readUnsignedByte();
        sourceConstantAlpha = emf.readUnsignedByte();
        alphaFormat = emf.readUnsignedByte();
    }

    public String toString() {
        return "BlendFunction";
    }

    public int getSourceConstantAlpha() {
        return sourceConstantAlpha;
    }

    public int getAlphaFormat() {
        return alphaFormat;
    }
}
