
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.system.IControl;
import com.document.render.office.thirdpart.achartengine.model.CategorySeries;
import com.document.render.office.thirdpart.achartengine.renderers.DefaultRenderer;

import java.util.ArrayList;
import java.util.List;


public class PieChart extends RoundChart {

    public PieChart(CategorySeries dataset, DefaultRenderer renderer) {
        super(dataset, renderer);
    }


    @Override
    public void draw(Canvas canvas, IControl control, int x, int y, int width, int height, Paint paint) {
        canvas.save();
        canvas.clipRect(x, y, x + width, y + height);

        paint.setAntiAlias(mRenderer.isAntialiasing());
        paint.setStyle(Style.FILL);
        paint.setTextSize(mRenderer.getLabelsTextSize());

        drawBackgroundAndFrame(mRenderer, canvas, control, new Rect(x, y, x + width, y + height), paint);

        int legendSize = mRenderer.getLegendHeight();
        if (mRenderer.isShowLegend() && legendSize == 0) {
            legendSize = height / 5;
        }

        int sLength = mDataset.getItemCount();
        double total = 0;
        String[] titles = new String[sLength];
        for (int i = 0; i < sLength; i++) {
            total += mDataset.getValue(i);
            titles[i] = mDataset.getCategory(i);
        }

        Rectangle titleAreaSize = getTitleTextAreaSize(mRenderer, width, height, paint);
        int legendH = height;
        if (titleAreaSize != null) {
            legendH -= titleAreaSize.height;
        }
        Rectangle legendAreaSize = getLegendAutoSize(mRenderer, titles, width, legendH, paint);

        double[] margins = mRenderer.getMargins();
        int left = x + (int) (margins[1] * width);
        int top = y + (int) (margins[0] * height);
        if (titleAreaSize != null) {
            top += titleAreaSize.height;
        }
        int right = x + width - (int) (margins[3] * width);
        if (legendAreaSize != null && (legendPos == LegendPosition_Left || legendPos == LegendPosition_Right)) {
            right -= legendAreaSize.width;
        }

        int bottom = y + height - (int) (margins[2] * height);
        if (legendAreaSize != null && (legendPos == LegendPosition_Top || legendPos == LegendPosition_Bottom)) {
            bottom -= legendAreaSize.height;
        }

        float size = mRenderer.getLegendTextSize() * mRenderer.getZoomRate();
        paint.setTextSize(size);
        paint.setTextAlign(Align.CENTER);
        paint.setFakeBoldText(true);


        if (mRenderer.isShowChartTitle()) {
            paint.setTextSize(mRenderer.getChartTitleTextSize() * mRenderer.getZoomRate());
            Rectangle maxTitleAreaSize = getMaxTitleAreaSize(width, height);
            drawTitle(canvas, mRenderer.getChartTitle(), 1.0f, x + width / 2, y + mRenderer.getChartTitleTextSize() * mRenderer.getZoomRate() * 2,
                    maxTitleAreaSize.width, maxTitleAreaSize.height, paint, 0);
        }

        paint.setFakeBoldText(false);

        bottom = y + height - legendSize;

        float currentAngle = 0;
        int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
        int radius = (int) (mRadius * 0.35 * mRenderer.getScale());

        int centerX = (int) (left + margins[1] * width + right - margins[3] * width) / 2;
        int centerY = (int) (bottom - margins[2] * height + top + margins[0] * height) / 2;
        float shortRadius = radius * 0.9f;
        float longRadius = radius * 1.1f;

        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        List<RectF> prevLabelsBounds = new ArrayList<RectF>();
        for (int i = 0; i < sLength; i++) {
            paint.setColor(mRenderer.getSeriesRendererAt(i).getColor());
            float value = (float) mDataset.getValue(i);
            float angle = (float) (value / total * 360);
            canvas.drawArc(oval, currentAngle - 90, angle, true, paint);


            currentAngle += angle;
        }


        prevLabelsBounds.clear();
        if (mRenderer.isShowLegend()) {
            int legendWidth = legendAreaSize.width;
            int legendHeight = Math.min(height, legendAreaSize.height);
            int legendLeft = x;
            int legendTop = y;
            switch (getLegendPosition()) {
                case LegendPosition_Right:
                case LegendPosition_Left:
                    legendLeft = x + width - legendWidth - (int) (mRenderer.getLegendTextSize() * mRenderer.getZoomRate());
                    if (titleAreaSize != null) {
                        legendTop = y + (height + titleAreaSize.height) / 2;
                    } else {
                        legendTop = y + (height - legendHeight) / 2;
                    }
                    break;

                case LegendPosition_Top:
                case LegendPosition_Bottom:
                    legendLeft = x + (width - legendWidth) / 2;
                    legendTop = y + height - legendHeight;
                    break;
            }

            drawLegend(canvas, mRenderer, titles, legendLeft, legendTop, legendWidth, legendHeight, paint, false);
        }

        canvas.restore();
    }
}
