
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.model.XYValueSeries;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYSeriesRenderer;


public class BubbleChart extends XYChart {

    public static final String TYPE = "Bubble";

    private static final int SHAPE_WIDTH = 10;

    private static final int MIN_BUBBLE_SIZE = 2;

    private static final int MAX_BUBBLE_SIZE = 20;

    BubbleChart() {
    }


    public BubbleChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        super(dataset, renderer);
    }


    public void drawSeries(Canvas canvas, Paint paint, float[] points,
                           SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
        XYSeriesRenderer renderer = (XYSeriesRenderer) seriesRenderer;
        paint.setColor(renderer.getColor());
        paint.setStyle(Style.FILL);
        int length = points.length;
        XYValueSeries series = (XYValueSeries) mDataset.getSeriesAt(seriesIndex);
        double max = series.getMaxValue();

        double coef = MAX_BUBBLE_SIZE / max;
        for (int i = 0; i < length; i += 2) {
            double size = series.getValue(i / 2) * coef + MIN_BUBBLE_SIZE;
            drawCircle(canvas, paint, points[i], points[i + 1], (float) size);
        }
    }


    public int getLegendShapeWidth(int seriesIndex) {
        return SHAPE_WIDTH;
    }


    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {
        paint.setStyle(Style.FILL);
        drawCircle(canvas, paint, x + SHAPE_WIDTH, y, 3);
    }


    private void drawCircle(Canvas canvas, Paint paint, float x, float y, float radius) {
        canvas.drawCircle(x, y, radius, paint);
    }


    public String getChartType() {
        return TYPE;
    }

}
