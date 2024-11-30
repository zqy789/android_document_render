
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYSeriesRenderer;


public class LineChart extends XYChart {

    public static final String TYPE = "Line";

    private static final int SHAPE_WIDTH = 30;

    private ScatterChart pointsChart;

    LineChart() {
    }


    public LineChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        super(dataset, renderer);
        pointsChart = new ScatterChart(dataset, renderer);
    }


    protected void setDatasetRenderer(XYMultipleSeriesDataset dataset,
                                      XYMultipleSeriesRenderer renderer) {
        super.setDatasetRenderer(dataset, renderer);
        pointsChart = new ScatterChart(dataset, renderer);
    }


    public void drawSeries(Canvas canvas, Paint paint, float[] points,
                           SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
        int length = points.length;
        XYSeriesRenderer renderer = (XYSeriesRenderer) seriesRenderer;
        float lineWidth = paint.getStrokeWidth();
        paint.setStrokeWidth(Math.max(renderer.getLineWidth() * mRenderer.getZoomRate(), 1.0f));
        if (renderer.isFillBelowLine()) {
            paint.setColor(renderer.getFillBelowLineColor());
            int pLength = points.length;
            float[] fillPoints = new float[pLength + 4];
            System.arraycopy(points, 0, fillPoints, 0, length);
            fillPoints[0] = points[0] + 1;
            fillPoints[length] = fillPoints[length - 2];
            fillPoints[length + 1] = yAxisValue;
            fillPoints[length + 2] = fillPoints[0];
            fillPoints[length + 3] = fillPoints[length + 1];
            paint.setStyle(Style.FILL);
            drawPath(canvas, fillPoints, paint, true);
        }
        paint.setColor(seriesRenderer.getColor());
        paint.setStyle(Style.STROKE);
        drawPath(canvas, points, paint, false);
        paint.setStyle(Style.FILL);
        paint.setStrokeWidth(lineWidth);
    }


    public int getLegendShapeWidth(int seriesIndex) {
        return (int) getRenderer().getLegendTextSize();
    }


    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {

        if (isRenderPoints(renderer)) {
            pointsChart.setDrawFrameFlag(false);
            pointsChart.drawLegendShape(canvas, renderer, x, y, seriesIndex, paint);
        }
    }


    public boolean isRenderPoints(SimpleSeriesRenderer renderer) {
        return ((XYSeriesRenderer) renderer).getPointStyle() != PointStyle.POINT;
    }


    public ScatterChart getPointsChart() {
        return pointsChart;
    }


    public String getChartType() {
        return TYPE;
    }

}
