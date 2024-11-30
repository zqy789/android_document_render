

package com.document.render.office.fc.ss.usermodel.charts;

import com.document.render.office.fc.ss.util.DataMarker;

import java.util.List;



public interface ScatterChartData extends ChartData {

    ScatterChartSerie addSerie(DataMarker xMarker, DataMarker yMarker);


    List<? extends ScatterChartSerie> getSeries();
}
