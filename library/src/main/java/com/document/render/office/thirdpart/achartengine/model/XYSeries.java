
package com.document.render.office.thirdpart.achartengine.model;

import com.document.render.office.thirdpart.achartengine.util.MathHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class XYSeries implements Serializable {

    private String mTitle;

    private List<Double> mX = new ArrayList<Double>();

    private List<Double> mY = new ArrayList<Double>();

    private double mMinX = MathHelper.NULL_VALUE;

    private double mMaxX = -MathHelper.NULL_VALUE;

    private double mMinY = MathHelper.NULL_VALUE;

    private double mMaxY = -MathHelper.NULL_VALUE;

    private int mScaleNumber;


    public XYSeries(String title) {
        this(title, 0);
    }


    public XYSeries(String title, int scaleNumber) {
        mTitle = title;
        mScaleNumber = scaleNumber;
        initRange();
    }

    public int getScaleNumber() {
        return mScaleNumber;
    }


    private void initRange() {
        mMinX = MathHelper.NULL_VALUE;
        mMaxX = -MathHelper.NULL_VALUE;
        mMinY = MathHelper.NULL_VALUE;
        mMaxY = -MathHelper.NULL_VALUE;
        int length = getItemCount();
        for (int k = 0; k < length; k++) {
            double x = getX(k);
            double y = getY(k);
            updateRange(x, y);
        }
    }


    private void updateRange(double x, double y) {
        mMinX = Math.min(mMinX, x);
        mMaxX = Math.max(mMaxX, x);
        mMinY = Math.min(mMinY, y);
        mMaxY = Math.max(mMaxY, y);
    }


    public String getTitle() {
        return mTitle;
    }


    public void setTitle(String title) {
        mTitle = title;
    }


    public synchronized void add(double x, double y) {
        mX.add(x);
        mY.add(y);
        updateRange(x, y);
    }


    public synchronized void remove(int index) {
        double removedX = mX.remove(index);
        double removedY = mY.remove(index);
        if (removedX == mMinX || removedX == mMaxX || removedY == mMinY || removedY == mMaxY) {
            initRange();
        }
    }


    public synchronized void clear() {
        mX.clear();
        mY.clear();
        initRange();
    }


    public synchronized double getX(int index) {
        return mX.get(index);
    }


    public synchronized double getY(int index) {
        return mY.get(index);
    }


    public synchronized int getItemCount() {
        return mX.size();
    }


    public double getMinX() {
        return mMinX;
    }


    public double getMinY() {
        return mMinY;
    }


    public double getMaxX() {
        return mMaxX;
    }


    public double getMaxY() {
        return mMaxY;
    }
}
