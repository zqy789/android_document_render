
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.model.XYSeries;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;


public class RangeBarChart extends ColumnBarChart {

    public static final String TYPE = "RangeBar";

    RangeBarChart() {
    }


    public RangeBarChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, Type type) {
        super(dataset, renderer, type);
    }


    public void drawSeries(Canvas canvas, Paint paint, float[] points,
                           SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
        int seriesNr = mDataset.getSeriesCount();
        int length = points.length;
        paint.setColor(seriesRenderer.getColor());
        paint.setStyle(Style.FILL);
        float halfDiffX = getHalfDiffX(points, length, seriesNr);
        for (int i = 0; i < length; i += 4) {
            float xMin = points[i];
            float yMin = points[i + 1];

            float xMax = points[i + 2];
            float yMax = points[i + 3];
            drawBar(canvas, xMin, yMin, xMax, yMax, halfDiffX, seriesNr, seriesIndex, paint);
        }
        paint.setColor(seriesRenderer.getColor());
    }


    protected void drawChartValuesText(Canvas canvas, XYSeries series, Paint paint, float[] points,
                                       int seriesIndex) {
        int seriesNr = mDataset.getSeriesCount();
        float halfDiffX = getHalfDiffX(points, points.length, seriesNr);
        for (int k = 0; k < points.length; k += 4) {
            float x = points[k];
            if (mType == Type.DEFAULT) {
                x += seriesIndex * 2 * halfDiffX - (seriesNr - 1.5f) * halfDiffX;
            }

            drawText(canvas, getLabel(series.getY(k / 2 + 1)), x, points[k + 3] - 3f, paint, 0);

            drawText(canvas, getLabel(series.getY(k / 2)), x, points[k + 1] + 7.5f, paint, 0);
        }
    }


    protected float getCoeficient() {
        return 0.5f;
    }


    public String getChartType() {
        return TYPE;
    }

}
