
package com.document.render.office.thirdpart.achartengine;

import com.document.render.office.thirdpart.achartengine.chart.AbstractChart;
import com.document.render.office.thirdpart.achartengine.chart.BubbleChart;
import com.document.render.office.thirdpart.achartengine.chart.ColumnBarChart;
import com.document.render.office.thirdpart.achartengine.chart.ColumnBarChart.Type;
import com.document.render.office.thirdpart.achartengine.chart.CombinedXYChart;
import com.document.render.office.thirdpart.achartengine.chart.DialChart;
import com.document.render.office.thirdpart.achartengine.chart.DoughnutChart;
import com.document.render.office.thirdpart.achartengine.chart.LineChart;
import com.document.render.office.thirdpart.achartengine.chart.PieChart;
import com.document.render.office.thirdpart.achartengine.chart.RangeBarChart;
import com.document.render.office.thirdpart.achartengine.chart.ScatterChart;
import com.document.render.office.thirdpart.achartengine.chart.TimeChart;
import com.document.render.office.thirdpart.achartengine.model.CategorySeries;
import com.document.render.office.thirdpart.achartengine.model.MultipleCategorySeries;
import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.renderers.DefaultRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.DialRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;


public class ChartFactory {

    public static final String CHART = "chart";


    public static final String TITLE = "title";

    private ChartFactory() {

    }


    public static final AbstractChart getLineChart(XYMultipleSeriesDataset dataset,
                                                   XYMultipleSeriesRenderer renderer) {
        checkParameters(dataset, renderer);
        return new LineChart(dataset, renderer);
    }


    public static final AbstractChart getScatterChart(XYMultipleSeriesDataset dataset,
                                                      XYMultipleSeriesRenderer renderer) {
        checkParameters(dataset, renderer);
        return new ScatterChart(dataset, renderer);
    }


    public static final AbstractChart getBubbleChart(XYMultipleSeriesDataset dataset,
                                                     XYMultipleSeriesRenderer renderer) {
        checkParameters(dataset, renderer);
        return new BubbleChart(dataset, renderer);
    }


    public static final AbstractChart getTimeChart(XYMultipleSeriesDataset dataset,
                                                   XYMultipleSeriesRenderer renderer, String format) {
        checkParameters(dataset, renderer);
        TimeChart chart = new TimeChart(dataset, renderer);
        chart.setDateFormat(format);
        return chart;
    }


    public static final AbstractChart getColumnBarChart(XYMultipleSeriesDataset dataset,
                                                        XYMultipleSeriesRenderer renderer, Type type) {
        checkParameters(dataset, renderer);
        return new ColumnBarChart(dataset, renderer, type);
    }


    public static final AbstractChart getRangeBarChart(XYMultipleSeriesDataset dataset,
                                                       XYMultipleSeriesRenderer renderer, Type type) {
        checkParameters(dataset, renderer);
        return new RangeBarChart(dataset, renderer, type);
    }


    public static final AbstractChart getCombinedXYChart(XYMultipleSeriesDataset dataset,
                                                         XYMultipleSeriesRenderer renderer, String[] types) {
        if (dataset == null || renderer == null || types == null || dataset.getSeriesCount() != types.length) {
            throw new IllegalArgumentException(
                    "Dataset, renderer and types should be not null and the datasets series count should be equal to the types length");
        }
        checkParameters(dataset, renderer);
        return new CombinedXYChart(dataset, renderer, types);
    }


    public static final AbstractChart getPieChart(CategorySeries dataset,
                                                  DefaultRenderer renderer) {
        checkParameters(dataset, renderer);
        return new PieChart(dataset, renderer);
    }


    public static final AbstractChart getDialChartView(CategorySeries dataset,
                                                       DialRenderer renderer) {
        checkParameters(dataset, renderer);
        return new DialChart(dataset, renderer);
    }


    public static final AbstractChart getDoughnutChartView(MultipleCategorySeries dataset,
                                                           DefaultRenderer renderer) {
        checkParameters(dataset, renderer);
        return new DoughnutChart(dataset, renderer);
    }


    private static void checkParameters(XYMultipleSeriesDataset dataset,
                                        XYMultipleSeriesRenderer renderer) {
        if (dataset == null || renderer == null
                || dataset.getSeriesCount() != renderer.getSeriesRendererCount()) {
            throw new IllegalArgumentException(
                    "Dataset and renderer should be not null and should have the same number of series");
        }
    }


    private static void checkParameters(CategorySeries dataset, DefaultRenderer renderer) {
        if (dataset == null || renderer == null
                || dataset.getItemCount() != renderer.getSeriesRendererCount()) {
            throw new IllegalArgumentException(
                    "Dataset and renderer should be not null and the dataset number of items should be equal to the number of series renderers");
        }
    }


    private static void checkParameters(MultipleCategorySeries dataset, DefaultRenderer renderer) {
        if (dataset == null || renderer == null
                || !checkMultipleSeriesItems(dataset, renderer.getSeriesRendererCount())) {
            throw new IllegalArgumentException(
                    "Titles and values should be not null and the dataset number of items should be equal to the number of series renderers");
        }
    }

    private static boolean checkMultipleSeriesItems(MultipleCategorySeries dataset, int value) {
        int count = dataset.getCategoriesCount();
        boolean equal = true;
        for (int k = 0; k < count && equal; k++) {
            equal = dataset.getValues(k).length == dataset.getTitles(k).length;
        }
        return equal;
    }

}
