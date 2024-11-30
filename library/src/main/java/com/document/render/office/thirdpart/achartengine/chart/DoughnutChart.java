
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.document.render.office.system.IControl;
import com.document.render.office.thirdpart.achartengine.model.MultipleCategorySeries;
import com.document.render.office.thirdpart.achartengine.renderers.DefaultRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;


public class DoughnutChart extends RoundChart {

    private MultipleCategorySeries mDataset;

    private int mStep;


    public DoughnutChart(MultipleCategorySeries dataset, DefaultRenderer renderer) {
        super(null, renderer);
        mDataset = dataset;
    }


    @Override
    public void draw(Canvas canvas, IControl control, int x, int y, int width, int height, Paint paint) {
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
        int cLength = mDataset.getCategoriesCount();
        String[] categories = new String[cLength];
        for (int category = 0; category < cLength; category++) {
            categories[category] = mDataset.getCategory(category);
        }
        if (mRenderer.isFitLegend()) {
            legendSize = drawLegend(canvas, mRenderer, categories, left, y, width, height,
                    paint, true);
        }

        int bottom = y + height - legendSize;
        drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);
        mStep = SHAPE_WIDTH * 3 / 4;

        int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
        double rCoef = 0.35 * mRenderer.getScale();
        double decCoef = 0.2 / cLength;
        int radius = (int) (mRadius * rCoef);
        int centerX = (left + right) / 2;
        int centerY = (bottom + top) / 2;
        float shortRadius = radius * 0.9f;
        float longRadius = radius * 1.1f;
        List<RectF> prevLabelsBounds = new ArrayList<RectF>();
        for (int category = 0; category < cLength; category++) {
            int sLength = mDataset.getItemCount(category);
            double total = 0;
            String[] titles = new String[sLength];
            for (int i = 0; i < sLength; i++) {
                total += mDataset.getValues(category)[i];
                titles[i] = mDataset.getTitles(category)[i];
            }
            float currentAngle = 0;
            RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            for (int i = 0; i < sLength; i++) {
                paint.setColor(mRenderer.getSeriesRendererAt(i).getColor());
                float value = (float) mDataset.getValues(category)[i];
                float angle = (float) (value / total * 360);
                canvas.drawArc(oval, currentAngle, angle, true, paint);
                drawLabel(canvas, mDataset.getTitles(category)[i], mRenderer, prevLabelsBounds, centerX,
                        centerY, shortRadius, longRadius, currentAngle, angle, left, right, paint);
                currentAngle += angle;
            }
            radius -= (int) mRadius * decCoef;
            shortRadius -= mRadius * decCoef - 2;
            if (mRenderer.getBackgroundColor() != 0) {
                paint.setColor(mRenderer.getBackgroundColor());
            } else {
                paint.setColor(Color.WHITE);
            }
            paint.setStyle(Style.FILL);
            oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawArc(oval, 0, 360, true, paint);
            radius -= 1;
        }
        prevLabelsBounds.clear();
        drawLegend(canvas, mRenderer, categories, left, y, width, height, paint,
                false);
    }


    public int getLegendShapeWidth(int seriesIndex) {
        return SHAPE_WIDTH;
    }


    public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
                                int seriesIndex, Paint paint) {
        mStep--;
        canvas.drawCircle(x + SHAPE_WIDTH - mStep, y, mStep, paint);
    }

}
