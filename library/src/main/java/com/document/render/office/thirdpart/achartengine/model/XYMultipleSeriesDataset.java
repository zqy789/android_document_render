
package com.document.render.office.thirdpart.achartengine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class XYMultipleSeriesDataset implements Serializable {

    private List<XYSeries> mSeries = new ArrayList<XYSeries>();


    public synchronized void addSeries(XYSeries series) {
        mSeries.add(series);
    }


    public synchronized void addSeries(int index, XYSeries series) {
        mSeries.add(index, series);
    }


    public synchronized void removeSeries(int index) {
        mSeries.remove(index);
    }


    public synchronized void removeSeries(XYSeries series) {
        mSeries.remove(series);
    }


    public synchronized XYSeries getSeriesAt(int index) {
        return mSeries.get(index);
    }


    public synchronized int getSeriesCount() {
        return mSeries.size();
    }


    public synchronized XYSeries[] getSeries() {
        return mSeries.toArray(new XYSeries[0]);
    }

}
