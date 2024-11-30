

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;

import java.io.IOException;


public class BitmapInfo {

    private BitmapInfoHeader header;

    public BitmapInfo(BitmapInfoHeader header) {
        this.header = header;
    }

    public BitmapInfo(EMFInputStream emf) throws IOException {
        header = new BitmapInfoHeader(emf);

    }

    public String toString() {
        return "  BitmapInfo\n" + header.toString();
    }

    public BitmapInfoHeader getHeader() {
        return header;
    }
}
