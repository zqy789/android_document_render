

package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.ss.usermodel.charts.ChartAxis;
import com.document.render.office.fc.ss.usermodel.charts.ChartAxisFactory;
import com.document.render.office.fc.ss.usermodel.charts.ChartData;
import com.document.render.office.fc.ss.usermodel.charts.ChartDataFactory;
import com.document.render.office.fc.ss.usermodel.charts.ChartLegend;
import com.document.render.office.fc.ss.usermodel.charts.ManuallyPositionable;

import java.util.List;



public interface Chart extends ManuallyPositionable {


    ChartDataFactory getChartDataFactory();


    ChartAxisFactory getChartAxisFactory();


    ChartLegend getOrCreateLegend();


    void deleteLegend();


    List<? extends ChartAxis> getAxis();


    void plot(ChartData data, ChartAxis... axis);
}
