
package com.document.render.office.thirdpart.achartengine.tools;

import com.document.render.office.thirdpart.achartengine.chart.AbstractChart;
import com.document.render.office.thirdpart.achartengine.chart.XYChart;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;



public abstract class AbstractTool {

    protected AbstractChart mChart;

    protected XYMultipleSeriesRenderer mRenderer;


    public AbstractTool(AbstractChart chart) {
        mChart = chart;
        if (chart instanceof XYChart) {
            mRenderer = ((XYChart) chart).getRenderer();
        }
    }

    public double[] getRange(int scale) {
        double minX = mRenderer.getXAxisMin(scale);
        double maxX = mRenderer.getXAxisMax(scale);
        double minY = mRenderer.getYAxisMin(scale);
        double maxY = mRenderer.getYAxisMax(scale);
        return new double[]{minX, maxX, minY, maxY};
    }

    public void checkRange(double[] range, int scale) {
        if (mChart instanceof XYChart) {
            double[] calcRange = ((XYChart) mChart).getCalcRange(scale);
            if (calcRange != null) {
                if (!mRenderer.isMinXSet(scale)) {
                    range[0] = calcRange[0];
                    mRenderer.setXAxisMin(range[0], scale);
                }
                if (!mRenderer.isMaxXSet(scale)) {
                    range[1] = calcRange[1];
                    mRenderer.setXAxisMax(range[1], scale);
                }
                if (!mRenderer.isMinYSet(scale)) {
                    range[2] = calcRange[2];
                    mRenderer.setYAxisMin(range[2], scale);
                }
                if (!mRenderer.isMaxYSet(scale)) {
                    range[3] = calcRange[3];
                    mRenderer.setYAxisMax(range[3], scale);
                }
            }
        }
    }

    protected void setXRange(double min, double max, int scale) {
        mRenderer.setXAxisMin(min, scale);
        mRenderer.setXAxisMax(max, scale);
    }

    protected void setYRange(double min, double max, int scale) {
        mRenderer.setYAxisMin(min, scale);
        mRenderer.setYAxisMax(max, scale);
    }

}
