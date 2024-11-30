
package com.document.render.office.thirdpart.achartengine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CategorySeries implements Serializable {

    private String mTitle;

    private List<String> mCategories = new ArrayList<String>();

    private List<Double> mValues = new ArrayList<Double>();


    public CategorySeries(String title) {
        mTitle = title;
    }


    public String getTitle() {
        return mTitle;
    }


    public synchronized void add(double value) {
        add(mCategories.size() + 1 + "", value);
    }


    public synchronized void add(String category, double value) {
        mCategories.add(category);
        mValues.add(value);
    }


    public synchronized void set(int index, String category, double value) {
        mCategories.set(index, category);
        mValues.set(index, value);
    }


    public synchronized void remove(int index) {
        mCategories.remove(index);
        mValues.remove(index);
    }


    public synchronized void clear() {
        mCategories.clear();
        mValues.clear();
    }


    public synchronized double getValue(int index) {
        return mValues.get(index);
    }


    public synchronized String getCategory(int index) {
        return mCategories.get(index);
    }


    public synchronized int getItemCount() {
        return mCategories.size();
    }


    public XYSeries toXYSeries() {
        XYSeries xySeries = new XYSeries(mTitle);
        int k = 0;
        for (double value : mValues) {
            xySeries.add(++k, value);
        }
        return xySeries;
    }
}
