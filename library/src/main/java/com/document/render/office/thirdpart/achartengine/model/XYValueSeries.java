
package com.document.render.office.thirdpart.achartengine.model;

import com.document.render.office.thirdpart.achartengine.util.MathHelper;

import java.util.ArrayList;
import java.util.List;



public class XYValueSeries extends XYSeries {

    private List<Double> mValue = new ArrayList<Double>();

    private double mMinValue = MathHelper.NULL_VALUE;

    private double mMaxValue = -MathHelper.NULL_VALUE;


    public XYValueSeries(String title) {
        super(title);
    }


    public synchronized void add(double x, double y, double value) {
        super.add(x, y);
        mValue.add(value);
        updateRange(value);
    }


    private void initRange() {
        mMinValue = MathHelper.NULL_VALUE;
        mMaxValue = MathHelper.NULL_VALUE;
        int length = getItemCount();
        for (int k = 0; k < length; k++) {
            updateRange(getValue(k));
        }
    }


    private void updateRange(double value) {
        mMinValue = Math.min(mMinValue, value);
        mMaxValue = Math.max(mMaxValue, value);
    }


    public synchronized void add(double x, double y) {
        add(x, y, 0d);
    }


    public synchronized void remove(int index) {
        super.remove(index);
        double removedValue = mValue.remove(index);
        if (removedValue == mMinValue || removedValue == mMaxValue) {
            initRange();
        }
    }


    public synchronized void clear() {
        super.clear();
        mValue.clear();
        initRange();
    }


    public synchronized double getValue(int index) {
        return mValue.get(index);
    }


    public double getMinValue() {
        return mMinValue;
    }


    public double getMaxValue() {
        return mMaxValue;
    }

}
