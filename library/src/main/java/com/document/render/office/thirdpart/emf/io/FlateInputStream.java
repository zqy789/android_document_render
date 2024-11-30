
package com.document.render.office.thirdpart.emf.io;

import android.graphics.Bitmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;


public class FlateInputStream extends InflaterInputStream {


    public FlateInputStream(InputStream in) {
        super(in);
    }


    public Bitmap readImage() throws IOException {
        return null;
    }
}
