

package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.model.XYSeries;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;


public class ColumnBarChart extends XYChart {
    
    public static final String TYPE = "Column Bar";

    
    protected Type mType = Type.DEFAULT;

    ColumnBarChart() {
    }

    
    public ColumnBarChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, Type type) {
        super(dataset, renderer);
        mType = type;
    }

    
    public void drawSeries(Canvas canvas, Paint paint, float[] points,
                           SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex) {
        int seriesNr = mDataset.getSeriesCount();
        int length = points.length;
        paint.setColor(seriesRenderer.getColor());
        paint.setStyle(Style.FILL);
        float halfDiffX = getHalfDiffX(points, length, seriesNr);
        for (int i = 0; i < length; i += 2) {
            float x = points[i];
            float y = points[i + 1];
            drawBar(canvas, x, yAxisValue, x, y, halfDiffX, seriesNr, seriesIndex, paint);
        }
        paint.setColor(seriesRenderer.getColor());
    }

    protected void drawBar(Canvas canvas, float xMin, float yMin, float xMax, float yMax,
                           float halfDiffX, int seriesNr, int seriesIndex, Paint paint) {
        int scale = mDataset.getSeriesAt(seriesIndex).getScaleNumber();
        if (mType == Type.STACKED) {
            drawBar(canvas, xMin - halfDiffX, yMax, xMax + halfDiffX, yMin, scale, seriesIndex,
                    paint);
        } else {
            float startX = xMin - seriesNr * halfDiffX + seriesIndex * 2 * halfDiffX;
            drawBar(canvas, startX, yMax, startX + 2 * halfDiffX, yMin, scale, seriesIndex, paint);
        }
    }

    private void drawBar(Canvas canvas, float xMin, float yMin, float xMax, float yMax, int scale,
                         int seriesIndex, Paint paint) {
        SimpleSeriesRenderer renderer = mRenderer.getSeriesRendererAt(seriesIndex);
        if (renderer.isGradientEnabled()) {
            float minY = (float) toScreenPoint(new double[]{0, renderer.getGradientStopValue()},
                    scale)[1];
            float maxY = (float) toScreenPoint(new double[]{0, renderer.getGradientStartValue()},
                    scale)[1];
            float gradientMinY = Math.max(minY, yMin);
            float gradientMaxY = Math.min(maxY, yMax);
            int gradientMinColor = renderer.getGradientStopColor();
            int gradientMaxColor = renderer.getGradientStartColor();
            int gradientStartColor = gradientMinColor;
            int gradientStopColor = gradientMaxColor;

            if (yMin < minY) {
                paint.setColor(gradientMaxColor);
                canvas.drawRect(Math.round(xMin), Math.round(yMin), Math.round(xMax),
                        Math.round(gradientMinY), paint);
            } else {
                gradientStopColor = getGradientPartialColor(gradientMaxColor, gradientMinColor,
                        (maxY - gradientMinY) / (maxY - minY));
            }
            if (yMax > maxY) {
                paint.setColor(gradientMinColor);
                canvas.drawRect(Math.round(xMin), Math.round(gradientMaxY), Math.round(xMax),
                        Math.round(yMax), paint);
            } else {
                gradientStartColor = getGradientPartialColor(gradientMinColor, gradientMaxColor,
                        (gradientMaxY - minY) / (maxY - minY));
            }
            GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{
                    gradientStartColor, gradientStopColor});
            gradient.setBounds(Math.round(xMin), Math.round(gradientMinY), Math.round(xMax),
                    Math.round(gradientMaxY));
            gradient.draw(canvas);
        } else {
            if (Math.abs(yMax - yMin) < 0.0000001f) {
                return;
            }

            canvas.drawRect(Math.round(xMin), Math.round(yMin), Math.round(xMax), Math.round(yMax),
                    paint);


            int color = paint.getColor();
            paint.setColor(Color.BLACK);
            paint.setStyle(Style.STROKE);

            canvas.drawRect(Math.round(xMin), Math.round(yMin), Math.round(xMax), Math.round(yMax),
                    paint);
            paint.setStyle(Style.FILL);
            paint.setColor(color);
        }
    }

    private int getGradientPartialColor(int minColor, int maxColor, float fraction) {
        int alpha = Math.round(fraction * Color.alpha(minColor) + (1 - fraction)
                * Color.alpha(maxColor));
        int r = Math.round(fraction * Color.red(minColor) + (1 - fraction) * Color.red(maxColor));
        int g = Math.round(fraction * Color.green(minColor) + (1 - fraction)
                * Color.green(maxColor));
        int b = Math.round(fraction * Color.blue(minColor) + (1 - fraction)
                * Color.blue((maxColor)));
        return Color.argb(alpha, r, g, b);
    }

    
    protected void drawChartValuesText(Canvas canvas, XYSeries series, Paint paint, float[] points,
                                       int seriesIndex) {
        int seriesNr = mDataset.getSeriesCount();
        float halfDiffX = getHalfDiffX(points, points.length, seriesNr);
        for (int k = 0; k < points.length; k += 2) {
            float x = points[k];
            if (mType == Type.DEFAULT) {
                x += seriesIndex * 2 * halfDiffX - (seriesNr - 1.5f) * halfDiffX;
            }
            drawText(canvas, getLabel(series.getY(k / 2)), x, points[k + 1] - 3.5f, paint, 0);
        }
    }

    
    public int getLegendShapeWidth(int seriesIndex) {
        return (int) getRenderer().getLegendTextSize();
    }

    
    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {
        float shapeWidth = getRenderer().getLegendTextSize() * mRenderer.getZoomRate();
        float halfShapeWidth = shapeWidth / 2;
        x += halfShapeWidth;

        canvas.drawRect(x, y - halfShapeWidth, x + shapeWidth, y + halfShapeWidth, paint);
        
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(x, y - halfShapeWidth, x + shapeWidth, y + halfShapeWidth, paint);
        paint.setStyle(Style.FILL);
    }

    
    protected float getHalfDiffX(float[] points, int length, int seriesNr) {
        int div = length;
        if (length > 2) {
            div = length - 2;
        }
        float halfDiffX = (points[length - 2] - points[0]) / div;
        if (halfDiffX == 0) {
            halfDiffX = getScreenR().width() / 2;
            ;
        }

        if (mType != Type.STACKED) {
            halfDiffX /= (seriesNr + 1);
        }
        return (float) (halfDiffX / (getCoeficient() * (1 + mRenderer.getBarSpacing())));
    }

    
    protected float getCoeficient() {
        return 1f;
    }

    
    public double getDefaultMinimum() {
        return 0;
    }

    
    public String getChartType() {
        return TYPE;
    }

    
    public enum Type {
        DEFAULT, STACKED;
    }
}
