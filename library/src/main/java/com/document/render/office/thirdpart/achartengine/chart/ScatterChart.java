
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYSeriesRenderer;


public class ScatterChart extends XYChart {

    public static final String TYPE = "Scatter";

    private static final float SIZE = 3;


    private float size = SIZE;

    private boolean drawFrame = true;

    ScatterChart() {
    }


    public ScatterChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        super(dataset, renderer);
        size = renderer.getPointSize();
    }


    protected void setDatasetRenderer(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        super.setDatasetRenderer(dataset, renderer);
        size = renderer.getPointSize();
    }


    public void drawSeries(Canvas canvas, Paint paint, float[] points,
                           SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
        XYSeriesRenderer renderer = (XYSeriesRenderer) seriesRenderer;
        paint.setColor(renderer.getColor());
        if (renderer.isFillPoints()) {
            paint.setStyle(Style.FILL);
        } else {
            paint.setStyle(Style.STROKE);
        }
        int length = points.length;
        switch (renderer.getPointStyle()) {
            case X:
                for (int i = 0; i < length; i += 2) {
                    drawX(canvas, paint, points[i], points[i + 1]);
                }
                break;
            case CIRCLE:
                for (int i = 0; i < length; i += 2) {
                    drawCircle(canvas, paint, points[i], points[i + 1]);
                }
                break;
            case TRIANGLE:
                float[] path = new float[6];
                for (int i = 0; i < length; i += 2) {
                    drawTriangle(canvas, paint, path, points[i], points[i + 1]);
                }
                break;
            case SQUARE:
                for (int i = 0; i < length; i += 2) {
                    drawSquare(canvas, paint, points[i], points[i + 1]);
                }
                break;
            case DIAMOND:
                path = new float[8];
                for (int i = 0; i < length; i += 2) {
                    drawDiamond(canvas, paint, path, points[i], points[i + 1]);
                }
                break;
            case POINT:
                canvas.drawPoints(points, paint);
                break;
        }
    }


    public int getLegendShapeWidth(int seriesIndex) {
        return (int) getRenderer().getLegendTextSize();
    }


    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {
        if (((XYSeriesRenderer) renderer).isFillPoints()) {
            paint.setStyle(Style.FILL);
        } else {
            paint.setStyle(Style.STROKE);
        }

        float shapeWidth = (int) getRenderer().getLegendTextSize() * mRenderer.getZoomRate();
        x += shapeWidth / 2;

        switch (((XYSeriesRenderer) renderer).getPointStyle()) {
            case X:
                drawX(canvas, paint, x, y);
                break;
            case CIRCLE:
                drawCircle(canvas, paint, x, y);
                break;
            case TRIANGLE:
                drawTriangle(canvas, paint, new float[6], x, y);
                break;
            case SQUARE:
                drawSquare(canvas, paint, x, y);
                break;
            case DIAMOND:
                drawDiamond(canvas, paint, new float[8], x, y);
                break;
            case POINT:
                canvas.drawPoint(x, y, paint);
                break;
        }











    }


    private void drawX(Canvas canvas, Paint paint, float x, float y) {
        float temSize = size * mRenderer.getZoomRate();
        canvas.drawLine(x - temSize, y - temSize, x + temSize, y + temSize, paint);
        canvas.drawLine(x + temSize, y - temSize, x - temSize, y + temSize, paint);
    }


    private void drawCircle(Canvas canvas, Paint paint, float x, float y) {
        float temSize = size * mRenderer.getZoomRate();
        canvas.drawCircle(x, y, temSize, paint);
    }


    private void drawTriangle(Canvas canvas, Paint paint, float[] path, float x, float y) {
        float temSize = size * mRenderer.getZoomRate();
        path[0] = x;
        path[1] = y - temSize - temSize / 2;
        path[2] = x - temSize;
        path[3] = y + temSize;
        path[4] = x + temSize;
        path[5] = path[3];
        drawPath(canvas, path, paint, true);
    }


    private void drawSquare(Canvas canvas, Paint paint, float x, float y) {
        float temSize = size * mRenderer.getZoomRate();
        canvas.drawRect(x - temSize, y - temSize, x + temSize, y + temSize, paint);
    }


    private void drawDiamond(Canvas canvas, Paint paint, float[] path, float x, float y) {
        float temSize = size * mRenderer.getZoomRate();
        path[0] = x;
        path[1] = y - temSize;
        path[2] = x - temSize;
        path[3] = y;
        path[4] = x;
        path[5] = y + temSize;
        path[6] = x + temSize;
        path[7] = y;
        drawPath(canvas, path, paint, true);
    }


    public String getChartType() {
        return TYPE;
    }

    public void setDrawFrameFlag(boolean drawFrame) {
        this.drawFrame = drawFrame;
    }

    public boolean isDrawFrame() {
        return drawFrame;
    }
}
