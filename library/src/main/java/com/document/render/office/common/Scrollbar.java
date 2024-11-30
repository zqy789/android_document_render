
package com.document.render.office.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.document.render.office.java.awt.Dimension;


public class Scrollbar {
    private final int SCROLLBAR_SIZE = 5;
    private final int SCROLLBAR_OFFBORDER = 2;

    private final int SCROLLBAR_COLOR_ALPHA = 125;

    private final int SCROLLBAR_COLOR = 0x8f444444;

    private Dimension pageSize = new Dimension();

    private RectF rect = new RectF();

    public void setPageSize(int width, int height) {
        this.pageSize.setSize(width, height);
    }


    public void draw(Canvas canvas, int currentX, int currentY, Paint paint) {
        Rect clip = canvas.getClipBounds();

        int oldColor = paint.getColor();
        int alpha = paint.getAlpha();

        paint.setColor(SCROLLBAR_COLOR);
        paint.setAlpha(SCROLLBAR_COLOR_ALPHA);

        if (pageSize.width > clip.right) {
            drawHorizontalScrollBar(canvas, currentX, paint);
        }

        if (pageSize.height > clip.bottom) {
            drawVerticalScrollBar(canvas, currentY, paint);
        }

        paint.setColor(oldColor);
        paint.setAlpha(alpha);
    }


    private void drawHorizontalScrollBar(Canvas canvas, int currentX, Paint paint) {
        Rect clip = canvas.getClipBounds();


        float length = clip.right * clip.right / pageSize.width;
        float pixelSteps = pageSize.width / (float) clip.right;


        float left = (pageSize.width / 2 - currentX) / pixelSteps - length / 2;
        rect.set(left, clip.bottom - SCROLLBAR_SIZE - SCROLLBAR_OFFBORDER, left + length, clip.bottom - SCROLLBAR_OFFBORDER);
        canvas.drawRoundRect(rect, SCROLLBAR_SIZE / 2, SCROLLBAR_SIZE / 2, paint);
    }


    private void drawVerticalScrollBar(Canvas canvas, int currentY, Paint paint) {
        Rect clip = canvas.getClipBounds();


        float length = clip.bottom * clip.bottom / pageSize.height;
        float pixelSteps = pageSize.height / (float) clip.bottom;

        float top = (pageSize.height / 2 - currentY) / pixelSteps - length / 2;

        rect.set(clip.right - SCROLLBAR_SIZE - SCROLLBAR_OFFBORDER, top, clip.right - SCROLLBAR_OFFBORDER, top + length);
        canvas.drawRoundRect(rect, SCROLLBAR_SIZE / 2, SCROLLBAR_SIZE / 2, paint);
    }
}
