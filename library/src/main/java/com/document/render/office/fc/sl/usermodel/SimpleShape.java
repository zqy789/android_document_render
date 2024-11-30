

package com.document.render.office.fc.sl.usermodel;

public interface SimpleShape extends Shape {
    public Fill getFill();

    public LineStyle getLineStyle();

    public Hyperlink getHyperlink();

    public void setHyperlink(Hyperlink hyperlink);
}
