
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

import com.document.render.office.system.IControl;
import com.document.render.office.thirdpart.achartengine.model.CategorySeries;
import com.document.render.office.thirdpart.achartengine.renderers.DefaultRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;


public abstract class RoundChart extends AbstractChart {

    protected static final int SHAPE_WIDTH = 10;

    protected CategorySeries mDataset;

    protected DefaultRenderer mRenderer;


    public RoundChart(CategorySeries dataset, DefaultRenderer renderer) {
        mDataset = dataset;
        mRenderer = renderer;
    }

    public float getZoomRate() {
        return mRenderer.getZoomRate();
    }


    @Override
    public void setZoomRate(float rate) {
        this.mRenderer.setZoomRate(rate);
    }


    @Override
    public void draw(Canvas canvas, IControl control, int x, int y, int width, int height, Paint paint) {
        Rect rect = new Rect(x, y, x + width, y + height);
        canvas.save();
        canvas.clipRect(rect);

        paint.setAntiAlias(mRenderer.isAntialiasing());
        paint.setStyle(Style.FILL);
        paint.setTextSize(mRenderer.getLabelsTextSize());
        int legendSize = mRenderer.getLegendHeight();
        if (mRenderer.isShowLegend() && legendSize == 0) {
            legendSize = height / 5;
        }
        int left = x;
        int top = y;
        int right = x + width;
        int sLength = mDataset.getItemCount();
        double total = 0;
        String[] titles = new String[sLength];
        for (int i = 0; i < sLength; i++) {
            total += mDataset.getValue(i);
            titles[i] = mDataset.getCategory(i);
        }
        if (mRenderer.isFitLegend()) {
            legendSize = drawLegend(canvas, mRenderer, titles, left, y, width, height,
                    paint, true);
        }
        int bottom = y + height - legendSize;
        drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);

        float currentAngle = 0;
        int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
        int radius = (int) (mRadius * 0.35 * mRenderer.getScale());
        int centerX = (left + right) / 2;
        int centerY = (bottom + top) / 2;
        float shortRadius = radius * 0.9f;
        float longRadius = radius * 1.1f;

        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        List<RectF> prevLabelsBounds = new ArrayList<RectF>();
        for (int i = 0; i < sLength; i++) {
            paint.setColor(mRenderer.getSeriesRendererAt(i).getColor());
            float value = (float) mDataset.getValue(i);
            float angle = (float) (value / total * 360);
            canvas.drawArc(oval, currentAngle, angle, true, paint);
            drawLabel(canvas, mDataset.getCategory(i), mRenderer, prevLabelsBounds, centerX, centerY,
                    shortRadius, longRadius, currentAngle, angle, left, right, paint);
            currentAngle += angle;
        }
        prevLabelsBounds.clear();
        drawLegend(canvas, mRenderer, titles, left, y, width, height, paint, false);

        canvas.restore();
    }


    public int getLegendShapeWidth(int seriesIndex) {
        return (int) getRenderer().getLegendTextSize();
    }


    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {
        float shapeWidth = getLegendShapeWidth(0) * mRenderer.getZoomRate();
        float halfShapeWidth = shapeWidth / 2;
        x += halfShapeWidth;
        canvas.drawRect(x, y - halfShapeWidth, x + shapeWidth, y + halfShapeWidth, paint);

        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRect(Math.round(x), y - halfShapeWidth, x + shapeWidth, y + halfShapeWidth, paint);
        paint.setStyle(Style.FILL);
    }


    public DefaultRenderer getRenderer() {
        return mRenderer;
    }

}
