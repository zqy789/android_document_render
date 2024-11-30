
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.model.XYSeries;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer.Orientation;

import java.util.List;


public class CombinedXYChart extends XYChart {
    
    private XYChart[] mCharts;
    
    private Class[] xyChartTypes = new Class[]{TimeChart.class, LineChart.class, ColumnBarChart.class,
            BubbleChart.class, LineChart.class, ScatterChart.class, RangeBarChart.class};

    
    public CombinedXYChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
                           String[] types) {
        super(dataset, renderer);
        int length = types.length;
        mCharts = new XYChart[length];
        for (int i = 0; i < length; i++) {
            try {
                mCharts[i] = getXYChart(types[i]);
            } catch (Exception e) {
                
            }
            if (mCharts[i] == null) {
                throw new IllegalArgumentException("Unknown chart type " + types[i]);
            } else {
                XYMultipleSeriesDataset newDataset = new XYMultipleSeriesDataset();
                newDataset.addSeries(dataset.getSeriesAt(i));
                XYMultipleSeriesRenderer newRenderer = new XYMultipleSeriesRenderer();
                
                newRenderer.setBarSpacing(renderer.getBarSpacing());
                newRenderer.setPointSize(renderer.getPointSize());
                int scale = dataset.getSeriesAt(i).getScaleNumber();
                if (renderer.isMinXSet(scale)) {
                    newRenderer.setXAxisMin(renderer.getXAxisMin(scale));
                }
                if (renderer.isMaxXSet(scale)) {
                    newRenderer.setXAxisMax(renderer.getXAxisMax(scale));
                }
                if (renderer.isMinYSet(scale)) {
                    newRenderer.setYAxisMin(renderer.getYAxisMin(scale));
                }
                if (renderer.isMaxYSet(scale)) {
                    newRenderer.setYAxisMax(renderer.getYAxisMax(scale));
                }
                newRenderer.addSeriesRenderer(renderer.getSeriesRendererAt(i));
                mCharts[i].setDatasetRenderer(newDataset, newRenderer);
            }
        }
    }

    private XYChart getXYChart(String type) throws IllegalAccessException, InstantiationException {
        XYChart chart = null;
        int length = xyChartTypes.length;
        for (int i = 0; i < length && chart == null; i++) {
            XYChart newChart = (XYChart) xyChartTypes[i].newInstance();
            if (type.equals(newChart.getChartType())) {
                chart = newChart;
            }
        }
        return chart;
    }

    
    public void drawSeries(Canvas canvas, Paint paint, float[] points,
                           SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
        mCharts[seriesIndex].setScreenR(getScreenR());
        mCharts[seriesIndex].setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex).getScaleNumber()), 0);
        mCharts[seriesIndex].drawSeries(canvas, paint, points, seriesRenderer, yAxisValue, 0);
    }

    @Override
    protected void drawSeries(XYSeries series, Canvas canvas, Paint paint, List<Float> pointsList,
                              SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, Orientation or) {
        mCharts[seriesIndex].setScreenR(getScreenR());
        mCharts[seriesIndex].setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex).getScaleNumber()), 0);
        mCharts[seriesIndex].drawSeries(series, canvas, paint, pointsList, seriesRenderer, yAxisValue,
                0, or);
    }

    
    public int getLegendShapeWidth(int seriesIndex) {
        return mCharts[seriesIndex].getLegendShapeWidth(0);
    }

    
    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {
        mCharts[seriesIndex].drawLegendShape(canvas, renderer, x, y, 0, paint);
    }

    
    public String getChartType() {
        return "Combined";
    }

}
