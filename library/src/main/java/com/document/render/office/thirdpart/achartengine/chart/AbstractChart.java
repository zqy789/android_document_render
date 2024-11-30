
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;

import com.document.render.office.common.BackgroundDrawer;
import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.system.IControl;
import com.document.render.office.thirdpart.achartengine.renderers.DefaultRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.SimpleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer.Orientation;

import java.util.List;


public abstract class AbstractChart {
    public final static short CHART_AREA = 0;
    public final static short CHART_BAR = 1;
    public final static short CHART_LINE = 2;
    public final static short CHART_PIE = 3;
    public final static short CHART_SCATTER = 4;
    public final static short CHART_STOCK = 5;
    public final static short CHART_SURFACE = 6;
    public final static short CHART_DOUGHNUT = 7;
    public final static short CHART_BUBBLE = 8;
    public final static short CHART_RADAR = 9;
    public final static short CHART_UNKOWN = 10;


    public final static byte LegendPosition_Left = 0;
    public final static byte LegendPosition_Top = 1;
    public final static byte LegendPosition_Right = 2;
    public final static byte LegendPosition_Bottom = 3;
    protected byte legendPos = LegendPosition_Right;
    private int categoryAxisTextColor = Color.BLACK;
    private Rectangle legendArea = null;

    public int getCategoryAxisTextColor() {
        return categoryAxisTextColor;
    }

    public void setCategoryAxisTextColor(int categoryAxisTextColor) {
        this.categoryAxisTextColor = categoryAxisTextColor;
    }

    public byte getLegendPosition() {
        return legendPos;
    }

    public void setLegendPosition(byte legendPos) {
        this.legendPos = legendPos;
    }


    public abstract void draw(Canvas canvas, IControl control, int x, int y, int width, int height, Paint paint);

    public abstract float getZoomRate();

    public abstract void setZoomRate(float rate);


    protected void drawBackground(DefaultRenderer renderer, Canvas canvas, int x, int y, int width,
                                  int height, Paint paint, boolean newColor, int color) {
        if (renderer.isApplyBackgroundColor() || newColor) {
            if (newColor) {
                paint.setColor(color);
            } else {
                paint.setColor(renderer.getBackgroundColor());
            }
            paint.setStyle(Style.FILL);
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }


    protected void drawBackgroundAndFrame(DefaultRenderer renderer, Canvas canvas, IControl control, Rect rect, Paint paint) {
        int alpha = paint.getAlpha();
        Path path = new Path();
        path.addRect((float) rect.left, (float) rect.top, (float) rect.right, (float) rect.bottom, Direction.CCW);

        BackgroundAndFill fill = renderer.getBackgroundAndFill();
        if (fill != null) {
            paint.setStyle(Style.FILL);
            BackgroundDrawer.drawPathBackground(canvas, control, 1, fill, rect, null, 1.0f, path, paint);
            paint.setAlpha(alpha);
        }


        Line frame = renderer.getChartFrame();
        if (frame != null) {
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(2);
            if (frame.isDash()) {
                DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5, 5}, 10);
                paint.setPathEffect(dashPathEffect);
            }

            BackgroundDrawer.drawPathBackground(canvas, null, 1, frame.getBackgroundAndFill(), rect, null, 1.0f, path, paint);
            paint.setStyle(Style.FILL);
            paint.setAlpha(alpha);
        }

        paint.reset();
        paint.setAntiAlias(true);
    }


    public Rectangle getMaxTitleAreaSize(int chartWidth, int chartHeight) {
        return new Rectangle((int) (chartWidth * 0.8f), chartHeight / 2);
    }

    public Rectangle getTitleTextAreaSize(DefaultRenderer renderer, int chartWidth, int chartHeight, Paint paint) {
        if (renderer.isShowChartTitle()) {
            float maxWidth = chartWidth * 0.8f;
            float maxHeight = chartHeight * 0.5f;
            return getTextSize(renderer.getChartTitle(), renderer.getChartTitleTextSize() * renderer.getZoomRate(), maxWidth, maxHeight, paint);
        } else {
            return null;
        }
    }


    public Rectangle getTextSize(String text, float fontSize, float maxWidth, float maxHeight, Paint paint) {
        if (text == null || text.length() <= 0) {
            return null;
        }
        float titleHeight = 0;
        paint.setTextSize(fontSize);
        FontMetrics fm = paint.getFontMetrics();
        float lineHeight = (float) Math.ceil(fm.descent - fm.ascent);

        float textWidth = paint.measureText(text);
        if (textWidth < maxWidth) {
            return new Rectangle((int) Math.ceil(textWidth), (int) Math.ceil(lineHeight));
        } else {

            String title = text;
            float[] charWidth = new float[]{};
            while (title.length() > 0 && titleHeight + lineHeight <= maxHeight) {
                int cnt = paint.breakText(title, true, maxWidth, charWidth);
                if (cnt == 0) {

                    cnt = 1;
                }
                String drawedText = title.substring(0, cnt);
                title = title.substring(cnt, title.length());
                if (title.length() > 0 && titleHeight + lineHeight * 2 > maxHeight) {
                    titleHeight += lineHeight;
                    break;
                } else {
                    titleHeight += lineHeight;
                }
            }
        }

        return new Rectangle((int) Math.ceil(maxWidth), (int) Math.ceil(titleHeight));
    }


    protected void drawTitle(Canvas canvas, String title, float zoom, float x, float y, float maxWidth, float maxHeight, Paint paint, float angle) {
        x *= zoom;
        y *= zoom;
        maxWidth *= zoom;
        maxHeight *= zoom;

        float initX = x;
        float initY = y;

        if (angle != 0) {
            canvas.rotate(angle, initX, initY);
        }

        {
            FontMetrics fm = paint.getFontMetrics();
            float lineHeight = (float) Math.ceil(fm.descent - fm.ascent);

            float textWidth = paint.measureText(title);
            if (textWidth < maxWidth) {
                canvas.drawText(title, x, y, paint);
            } else {

                float[] charWidth = new float[]{};
                float sum = 0;
                while (title.length() > 0 && sum + lineHeight <= maxHeight) {
                    int cnt = paint.breakText(title, true, maxWidth, charWidth);
                    if (cnt == 0) {

                        cnt = 1;
                    }
                    String drawedText = title.substring(0, cnt);
                    title = title.substring(cnt, title.length());
                    if (title.length() > 0 && sum + lineHeight * 2 > maxHeight) {

                        drawedText = drawedText.substring(0, drawedText.length() - 1) + "...";
                        canvas.drawText(drawedText, x, y + fm.descent, paint);
                        y += lineHeight;
                        sum += lineHeight;
                        break;
                    } else {
                        canvas.drawText(drawedText, x, y + fm.descent, paint);
                        y += lineHeight;
                        sum += lineHeight;
                    }
                }
            }
        }

        if (angle != 0) {
            canvas.rotate(-angle, initX, initY);
        }
    }


    public int getMaxLegendWidth(float chartWidth) {
        if (legendPos == LegendPosition_Left || legendPos == LegendPosition_Right) {
            return Math.round(chartWidth * 0.35f);
        } else {
            return Math.round(chartWidth * 0.9f);
        }
    }


    public int getMaxLegendHeight(float chartHeight) {
        if (legendPos == LegendPosition_Left || legendPos == LegendPosition_Right) {
            return Math.round(chartHeight * 0.9f);
        } else {
            return Math.round(chartHeight * 0.35f);
        }
    }


    public Rectangle getLegendAutoSize(DefaultRenderer renderer, String[] titles, int chartWidth, int chartHeight, Paint paint) {
        if (!renderer.isShowLegend()) {
            return null;
        }


        float width = -1f;
        float height = -1f;

        paint.setTextSize(renderer.getLegendTextSize() * renderer.getZoomRate());
        int seriesCnt = Math.min(titles.length, renderer.getSeriesRendererCount());
        for (int i = 0; i < seriesCnt; i++) {
            String text = titles[i].replace("\n", " ");

            FontMetrics fm = paint.getFontMetrics();

            height = Math.max((float) (Math.ceil(fm.descent - fm.ascent)), height);

            width = Math.max((float) (paint.measureText(text)), width);
        }

        float textOffset = getLegendShapeWidth(0) * renderer.getZoomRate() * 2;
        int maxLegendHeight = getMaxLegendHeight(chartHeight);
        int maxLegendWidth = getMaxLegendWidth(chartWidth);
        float maxLegendTextWidth = maxLegendWidth - textOffset;
        int singleWidth = (int) Math.ceil(width + textOffset);
        int singleHeight = (int) Math.ceil(height);

        if (width > maxLegendTextWidth) {

            int lines = (int) Math.ceil(width / maxLegendTextWidth);
            legendArea = new Rectangle(maxLegendWidth,
                    Math.min(singleHeight * lines * seriesCnt, maxLegendHeight));
        } else {
            switch (legendPos) {
                case LegendPosition_Left:
                case LegendPosition_Right:
                    legendArea = new Rectangle(singleWidth,
                            Math.min(singleHeight * seriesCnt, maxLegendHeight));
                    break;
                case LegendPosition_Top:
                case LegendPosition_Bottom: {

                    int maxLineCnt = (int) (maxLegendWidth / (float) singleWidth);
                    if (seriesCnt > maxLineCnt) {


                        int lines = 2;
                        int lineCnt = (int) Math.ceil(seriesCnt / (float) lines);
                        while (lineCnt * singleWidth > maxLegendWidth) {
                            lines = lines + 1;
                            lineCnt = (int) Math.ceil(seriesCnt / (float) lines);
                        }


                        int lastCnt = seriesCnt - seriesCnt / lineCnt * lineCnt;
                        while (lastCnt < lineCnt - 1 && (int) Math.ceil(seriesCnt / (float) (lineCnt - 1)) == lines) {
                            lineCnt = lineCnt - 1;
                            lastCnt = lines - 1;
                        }
                        legendArea = new Rectangle(singleWidth * lineCnt, Math.min(singleHeight * lines, maxLegendHeight));
                    } else {

                        legendArea = new Rectangle(singleWidth * seriesCnt, singleHeight);
                    }
                    break;
                }
                default:
                    return null;
            }
        }

        return legendArea;
    }


    public Rectangle getSingleAutoLegendSize(DefaultRenderer renderer, String[] titles, Paint paint, int legendWidth) {
        float width = -1f;
        float height = -1f;

        paint.setTextSize(renderer.getLegendTextSize() * renderer.getZoomRate());
        int seriesCnt = Math.min(titles.length, renderer.getSeriesRendererCount());
        for (int i = 0; i < seriesCnt; i++) {
            String text = titles[i].replace("\n", " ");

            FontMetrics fm = paint.getFontMetrics();

            height = Math.max((float) (Math.ceil(fm.descent - fm.ascent)), height);

            width = Math.max((float) (paint.measureText(text)), width);
        }

        float maxLegendTextWidth = legendWidth - getLegendShapeWidth(0) * renderer.getZoomRate() * 2;
        if (width > maxLegendTextWidth) {

            int lines = (int) Math.ceil(width / maxLegendTextWidth);
            return new Rectangle(legendWidth, (int) Math.ceil(height) * lines);
        } else {
            return new Rectangle((int) Math.ceil(width + getLegendShapeWidth(0) * renderer.getZoomRate() * 2), (int) Math.ceil(height));
        }
    }


    private float getLegendTextOffset(DefaultRenderer renderer) {
        return getLegendShapeWidth(0) * 2 * renderer.getZoomRate();
    }



    protected int drawLegend(Canvas canvas, DefaultRenderer renderer, String[] titles, int left, int top, int width, int height, Paint paint, boolean calculate) {
        if (renderer.isShowLegend()) {
            Rectangle singleLegendSize = getSingleAutoLegendSize(renderer, titles, paint, width);

            float currentX = left;
            float currentY = top;
            float right = (left + width);

            paint.setTextAlign(Align.LEFT);
            paint.setTextSize(renderer.getLegendTextSize() * renderer.getZoomRate());
            FontMetrics fm = paint.getFontMetrics();
            int sLength = Math.min(titles.length, renderer.getSeriesRendererCount());

            for (int i = 0; i < sLength; i++) {
                final float shapeWidth = getLegendShapeWidth(i) * renderer.getZoomRate();
                String text = titles[i].replace("\n", " ");

                float sum = paint.measureText(text);
                float textOffset = getLegendTextOffset(renderer);
                float extraSize = textOffset + sum;
                switch (legendPos) {
                    case LegendPosition_Left:
                    case LegendPosition_Right: {

                        if (titles.length == renderer.getSeriesRendererCount()) {
                            paint.setColor(renderer.getSeriesRendererAt(i).getColor());
                        } else {
                            paint.setColor(Color.LTGRAY);
                        }
                        drawLegendShape(canvas, renderer.getSeriesRendererAt(i), currentX, currentY, i, paint);


                        paint.setColor(categoryAxisTextColor);

                        if (extraSize > width) {
                            float textWidth = width - textOffset;
                            float[] charWidth = new float[]{};
                            while (text.length() > 0) {
                                int cnt = paint.breakText(text, true, textWidth, charWidth);
                                if (cnt == 0) {

                                    cnt = 1;
                                }
                                String drawedText = text.substring(0, cnt);
                                text = text.substring(cnt, text.length());

                                canvas.drawText(drawedText, currentX + 2 * shapeWidth, currentY + fm.descent, paint);
                                currentY += Math.ceil(fm.descent - fm.ascent);
                            }
                        } else {
                            canvas.drawText(text, currentX + 2 * shapeWidth, currentY + fm.descent, paint);
                            currentY += singleLegendSize.height;
                        }
                        break;
                    }
                    case LegendPosition_Top:
                    case LegendPosition_Bottom: {
                        if (extraSize <= singleLegendSize.width) {
                            if (currentX + singleLegendSize.width > right) {

                                currentY += singleLegendSize.height;
                                currentX = left * renderer.getZoomRate();


                                if (titles.length == renderer.getSeriesRendererCount()) {
                                    paint.setColor(renderer.getSeriesRendererAt(i).getColor());
                                } else {
                                    paint.setColor(Color.LTGRAY);
                                }
                                drawLegendShape(canvas, renderer.getSeriesRendererAt(i), currentX, currentY, i, paint);


                                paint.setColor(categoryAxisTextColor);
                                canvas.drawText(text, currentX + 2 * shapeWidth, currentY + fm.descent, paint);

                                currentX += singleLegendSize.width;
                            } else {


                                if (titles.length == renderer.getSeriesRendererCount()) {
                                    paint.setColor(renderer.getSeriesRendererAt(i).getColor());
                                } else {
                                    paint.setColor(Color.LTGRAY);
                                }
                                drawLegendShape(canvas, renderer.getSeriesRendererAt(i), currentX, currentY, i, paint);


                                paint.setColor(categoryAxisTextColor);
                                canvas.drawText(text, currentX + 2 * shapeWidth, currentY + fm.descent, paint);
                                currentX += singleLegendSize.width;
                            }
                        } else {

                            currentY += singleLegendSize.height;
                            currentX = left;


                            if (titles.length == renderer.getSeriesRendererCount()) {
                                paint.setColor(renderer.getSeriesRendererAt(i).getColor());
                            } else {
                                paint.setColor(Color.LTGRAY);
                            }
                            drawLegendShape(canvas, renderer.getSeriesRendererAt(i), currentX, currentY, i, paint);


                            paint.setColor(categoryAxisTextColor);
                            float textWidth = width - textOffset;
                            float[] charWidth = new float[]{};
                            while (text.length() > 0) {
                                int cnt = paint.breakText(text, true, textWidth, charWidth);
                                if (cnt == 0) {

                                    cnt = 1;
                                }
                                String drawedText = text.substring(0, cnt);
                                text = text.substring(cnt, text.length());

                                canvas.drawText(drawedText, currentX + 2 * shapeWidth, currentY + fm.descent, paint);
                                currentY += Math.ceil(fm.descent - fm.ascent);
                            }
                        }
                        break;
                    }
                }
            }
        }
        return Math.round(renderer.getLegendTextSize() * renderer.getZoomRate());
    }


    protected boolean getExceed(float currentWidth, DefaultRenderer renderer, int right, int width) {
        boolean exceed = currentWidth > right;
        if (isVertical(renderer)) {
            exceed = currentWidth > width;
        }
        return exceed;
    }


    protected boolean isVertical(DefaultRenderer renderer) {
        return renderer instanceof XYMultipleSeriesRenderer
                && ((XYMultipleSeriesRenderer) renderer).getOrientation() == Orientation.VERTICAL;
    }


    protected void drawPath(Canvas canvas, float[] points, Paint paint, boolean circular) {
        Path path = new Path();
        path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            path.lineTo(points[i], points[i + 1]);
        }
        if (circular) {
            path.lineTo(points[0], points[1]);
        }
        canvas.drawPath(path, paint);
    }


    public abstract int getLegendShapeWidth(int seriesIndex);


    public abstract void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x,
                                         float y, int seriesIndex, Paint paint);


    private String getFitText(String text, float width, Paint paint) {
        String newText = text;
        int length = text.length();
        int diff = 0;
        while (paint.measureText(newText) > width && diff < length) {
            diff++;
            newText = text.substring(0, length - diff) + "...";
        }
        if (diff == length) {
            newText = "...";
        }
        return newText;
    }

    protected void drawLabel(Canvas canvas, String labelText, DefaultRenderer renderer,
                             List<RectF> prevLabelsBounds, int centerX, int centerY, float shortRadius, float longRadius,
                             float currentAngle, float angle, int left, int right, Paint paint) {
        if (renderer.isShowLabels()) {
            paint.setColor(renderer.getLabelsColor());
            double rAngle = Math.toRadians(90 - (currentAngle + angle / 2));
            double sinValue = Math.sin(rAngle);
            double cosValue = Math.cos(rAngle);
            int x1 = Math.round(centerX + (float) (shortRadius * sinValue));
            int y1 = Math.round(centerY + (float) (shortRadius * cosValue));
            int x2 = Math.round(centerX + (float) (longRadius * sinValue));
            int y2 = Math.round(centerY + (float) (longRadius * cosValue));

            float size = renderer.getLabelsTextSize();
            float extra = Math.max(size / 2, 10);
            paint.setTextAlign(Align.LEFT);
            if (x1 > x2) {
                extra = -extra;
                paint.setTextAlign(Align.RIGHT);
            }
            float xLabel = x2 + extra;
            float yLabel = y2;
            float width = right - xLabel;
            if (x1 > x2) {
                width = xLabel - left;
            }
            labelText = getFitText(labelText, width, paint);
            float widthLabel = paint.measureText(labelText);
            boolean okBounds = false;
            while (!okBounds) {
                boolean intersects = false;
                int length = prevLabelsBounds.size();
                for (int j = 0; j < length && !intersects; j++) {
                    RectF prevLabelBounds = prevLabelsBounds.get(j);
                    if (prevLabelBounds.intersects(xLabel, yLabel, xLabel + widthLabel, yLabel + size)) {
                        intersects = true;
                        yLabel = Math.max(yLabel, prevLabelBounds.bottom);
                    }
                }
                okBounds = !intersects;
            }

            y2 = (int) (yLabel - size / 2);
            canvas.drawLine(x1, y1, x2, y2, paint);
            canvas.drawLine(x2, y2, x2 + extra, y2, paint);
            canvas.drawText(labelText, xLabel, yLabel, paint);
            prevLabelsBounds.add(new RectF(xLabel, yLabel, xLabel + widthLabel, yLabel + size));
        }
    }

}
