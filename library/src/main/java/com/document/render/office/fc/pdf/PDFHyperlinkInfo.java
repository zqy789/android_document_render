
package com.document.render.office.fc.pdf;

import android.graphics.RectF;


public class PDFHyperlinkInfo extends RectF {


    private int pageNumber;
    private String strURI;


    public PDFHyperlinkInfo(float l, float t, float r, float b, int pageNumber, String uri) {
        super(l, t, r, b);
        this.pageNumber = pageNumber;
        this.strURI = uri;
    }


    public int getPageNumber() {
        return this.pageNumber;
    }


    public String getURL() {
        return this.strURI;
    }
}
