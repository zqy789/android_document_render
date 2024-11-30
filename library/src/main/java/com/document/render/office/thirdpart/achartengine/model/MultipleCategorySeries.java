
package com.document.render.office.thirdpart.achartengine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MultipleCategorySeries implements Serializable {

    private String mTitle;

    private List<String> mCategories = new ArrayList<String>();

    private List<String[]> mTitles = new ArrayList<String[]>();

    private List<double[]> mValues = new ArrayList<double[]>();


    public MultipleCategorySeries(String title) {
        mTitle = title;
    }


    public void add(String[] titles, double[] values) {
        add(mCategories.size() + "", titles, values);
    }


    public void add(String category, String[] titles, double[] values) {
        mCategories.add(category);
        mTitles.add(titles);
        mValues.add(values);
    }


    public void remove(int index) {
        mCategories.remove(index);
        mTitles.remove(index);
        mValues.remove(index);
    }


    public void clear() {
        mCategories.clear();
        mTitles.clear();
        mValues.clear();
    }


    public double[] getValues(int index) {
        return mValues.get(index);
    }


    public String getCategory(int index) {
        return mCategories.get(index);
    }


    public int getCategoriesCount() {
        return mCategories.size();
    }


    public int getItemCount(int index) {
        return mValues.get(index).length;
    }


    public String[] getTitles(int index) {
        return mTitles.get(index);
    }


    public XYSeries toXYSeries() {
        XYSeries xySeries = new XYSeries(mTitle);
        return xySeries;
    }
}
