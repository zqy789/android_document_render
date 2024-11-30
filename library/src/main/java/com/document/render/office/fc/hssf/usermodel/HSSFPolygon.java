

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ddf.EscherContainerRecord;



public class HSSFPolygon
        extends HSSFShape {
    int[] xPoints;
    int[] yPoints;
    int drawAreaWidth = 100;
    int drawAreaHeight = 100;

    HSSFPolygon(EscherContainerRecord escherContainer, HSSFShape parent, HSSFAnchor anchor) {
        super(escherContainer, parent, anchor);
    }

    public int[] getXPoints() {
        return xPoints;
    }

    public int[] getYPoints() {
        return yPoints;
    }

    public void setPoints(int[] xPoints, int[] yPoints) {
        this.xPoints = cloneArray(xPoints);
        this.yPoints = cloneArray(yPoints);
    }

    private int[] cloneArray(int[] a) {
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++)
            result[i] = a[i];

        return result;
    }


    public void setPolygonDrawArea(int width, int height) {
        this.drawAreaWidth = width;
        this.drawAreaHeight = height;
    }

    public int getDrawAreaWidth() {
        return drawAreaWidth;
    }

    public int getDrawAreaHeight() {
        return drawAreaHeight;
    }


}
