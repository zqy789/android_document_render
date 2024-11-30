package com.document.render.office.common.shape;

import com.document.render.office.thirdpart.achartengine.chart.AbstractChart;

public class WPChartShape extends WPAutoShape {

    private AbstractChart chart;

    public AbstractChart getAChart() {
        return chart;
    }

    public void setAChart(AbstractChart chart) {
        this.chart = chart;
    }
}
