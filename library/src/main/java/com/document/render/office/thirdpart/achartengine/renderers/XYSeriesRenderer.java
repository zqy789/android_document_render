
package com.document.render.office.thirdpart.achartengine.renderers;

import android.graphics.Color;

import com.document.render.office.thirdpart.achartengine.chart.PointStyle;


public class XYSeriesRenderer extends SimpleSeriesRenderer {

    private boolean mFillPoints = true;

    private boolean mFillBelowLine = false;

    private int mFillColor = Color.argb(125, 0, 0, 200);

    private PointStyle mPointStyle = PointStyle.POINT;

    private float mLineWidth = 3;


    public boolean isFillBelowLine() {
        return mFillBelowLine;
    }


    public void setFillBelowLine(boolean fill) {
        mFillBelowLine = fill;
    }


    public boolean isFillPoints() {
        return mFillPoints;
    }


    public void setFillPoints(boolean fill) {
        mFillPoints = fill;
    }


    public int getFillBelowLineColor() {
        return mFillColor;
    }


    public void setFillBelowLineColor(int color) {
        mFillColor = color;
    }


    public PointStyle getPointStyle() {
        return mPointStyle;
    }


    public void setPointStyle(PointStyle style) {
        mPointStyle = style;
    }


    public float getLineWidth() {
        return mLineWidth;
    }


    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
    }

}
