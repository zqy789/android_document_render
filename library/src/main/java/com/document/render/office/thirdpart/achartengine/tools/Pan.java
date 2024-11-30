
package com.document.render.office.thirdpart.achartengine.tools;

import com.document.render.office.thirdpart.achartengine.chart.XYChart;


public class Pan extends AbstractTool {


    public Pan(XYChart chart) {
        super(chart);
    }


    public void apply(float oldX, float oldY, float newX, float newY) {
        int scales = mRenderer.getScalesCount();
        double[] limits = mRenderer.getPanLimits();
        boolean limited = limits != null && limits.length == 4;
        XYChart chart = (XYChart) mChart;
        for (int i = 0; i < scales; i++) {
            double[] range = getRange(i);
            double[] calcRange = chart.getCalcRange(i);
            if (range[0] == range[1] && calcRange[0] == calcRange[1] || range[2] == range[3]
                    && calcRange[2] == calcRange[3]) {
                return;
            }
            checkRange(range, i);

            double[] realPoint = chart.toRealPoint(oldX, oldY);
            double[] realPoint2 = chart.toRealPoint(newX, newY);
            double deltaX = realPoint[0] - realPoint2[0];
            double deltaY = realPoint[1] - realPoint2[1];
            if (mRenderer.isPanXEnabled()) {
                if (limited) {
                    if (limits[0] > range[0] + deltaX) {
                        setXRange(limits[0], limits[0] + (range[1] - range[0]), i);
                    } else if (limits[1] < range[1] + deltaX) {
                        setXRange(limits[1] - (range[1] - range[0]), limits[1], i);
                    } else {
                        setXRange(range[0] + deltaX, range[1] + deltaX, i);
                    }
                } else {
                    setXRange(range[0] + deltaX, range[1] + deltaX, i);
                }
            }
            if (mRenderer.isPanYEnabled()) {
                if (limited) {
                    if (limits[2] > range[2] + deltaY) {
                        setYRange(limits[2], limits[2] + (range[3] - range[2]), i);
                    } else if (limits[3] < range[3] + deltaY) {
                        setYRange(limits[3] - (range[3] - range[2]), limits[3], i);
                    } else {
                        setYRange(range[2] + deltaY, range[3] + deltaY, i);
                    }
                } else {
                    setYRange(range[2] + deltaY, range[3] + deltaY, i);
                }
            }
        }
    }
}
