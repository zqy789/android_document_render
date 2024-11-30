
package com.document.render.office.thirdpart.achartengine.renderers;

import android.graphics.Color;

import java.io.Serializable;


public class SimpleSeriesRenderer implements Serializable {

    private int mColor = Color.BLUE;

    private boolean mDisplayChartValues;

    private float mChartValuesTextSize = 10;

    private BasicStroke mStroke;

    private boolean mGradientEnabled = false;

    private double mGradientStartValue;

    private int mGradientStartColor;

    private double mGradientStopValue;

    private int mGradientStopColor;


    public int getColor() {
        return mColor;
    }


    public void setColor(int color) {
        mColor = color;
    }


    public boolean isDisplayChartValues() {
        return mDisplayChartValues;
    }


    public void setDisplayChartValues(boolean display) {
        mDisplayChartValues = display;
    }


    public float getChartValuesTextSize() {
        return mChartValuesTextSize;
    }


    public void setChartValuesTextSize(float textSize) {
        mChartValuesTextSize = textSize;
    }


    public BasicStroke getStroke() {
        return mStroke;
    }


    public void setStroke(BasicStroke stroke) {
        mStroke = stroke;
    }


    public boolean isGradientEnabled() {
        return mGradientEnabled;
    }


    public void setGradientEnabled(boolean enabled) {
        mGradientEnabled = enabled;
    }


    public double getGradientStartValue() {
        return mGradientStartValue;
    }


    public int getGradientStartColor() {
        return mGradientStartColor;
    }


    public void setGradientStart(double start, int color) {
        mGradientStartValue = start;
        mGradientStartColor = color;
    }


    public double getGradientStopValue() {
        return mGradientStopValue;
    }


    public int getGradientStopColor() {
        return mGradientStopColor;
    }


    public void setGradientStop(double start, int color) {
        mGradientStopValue = start;
        mGradientStopColor = color;
    }

}
