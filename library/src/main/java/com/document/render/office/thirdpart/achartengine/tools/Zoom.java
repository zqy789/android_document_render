
package com.document.render.office.thirdpart.achartengine.tools;

import com.document.render.office.thirdpart.achartengine.chart.AbstractChart;
import com.document.render.office.thirdpart.achartengine.chart.RoundChart;
import com.document.render.office.thirdpart.achartengine.chart.XYChart;
import com.document.render.office.thirdpart.achartengine.renderers.DefaultRenderer;



public class Zoom extends AbstractTool {

    private boolean mZoomIn;

    private float mZoomRate;


    public Zoom(AbstractChart chart, boolean in, float rate) {
        super(chart);
        mZoomIn = in;
        setZoomRate(rate);
    }


    public void setZoomRate(float rate) {
        mZoomRate = rate;
    }


    public void apply() {
        if (mChart instanceof XYChart) {
            int scales = mRenderer.getScalesCount();
            for (int i = 0; i < scales; i++) {
                double[] range = getRange(i);
                checkRange(range, i);
                double[] limits = mRenderer.getZoomLimits();
                boolean limited = limits != null && limits.length == 4;

                double centerX = (range[0] + range[1]) / 2;
                double centerY = (range[2] + range[3]) / 2;
                double newWidth = range[1] - range[0];
                double newHeight = range[3] - range[2];
                if (mZoomIn) {
                    if (mRenderer.isZoomXEnabled()) {
                        newWidth /= mZoomRate;
                    }
                    if (mRenderer.isZoomYEnabled()) {
                        newHeight /= mZoomRate;
                    }
                } else {
                    if (mRenderer.isZoomXEnabled()) {
                        newWidth *= mZoomRate;
                    }
                    if (mRenderer.isZoomYEnabled()) {
                        newHeight *= mZoomRate;
                    }
                }

                if (mRenderer.isZoomXEnabled()) {
                    double newXMin = centerX - newWidth / 2;
                    double newXMax = centerX + newWidth / 2;
                    if (!limited || limits[0] <= newXMin && limits[1] >= newXMax) {
                        setXRange(newXMin, newXMax, i);
                    }
                }
                if (mRenderer.isZoomYEnabled()) {
                    double newYMin = centerY - newHeight / 2;
                    double newYMax = centerY + newHeight / 2;
                    if (!limited || limits[2] <= newYMin && limits[3] >= newYMax) {
                        setYRange(newYMin, newYMax, i);
                    }
                }
            }
        } else {
            DefaultRenderer renderer = ((RoundChart) mChart).getRenderer();
            if (mZoomIn) {
                renderer.setScale(renderer.getScale() * mZoomRate);
            } else {
                renderer.setScale(renderer.getScale() / mZoomRate);
            }
        }
    }

}
