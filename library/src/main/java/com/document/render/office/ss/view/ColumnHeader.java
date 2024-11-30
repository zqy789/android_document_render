
package com.document.render.office.ss.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;

import com.document.render.office.common.PaintKit;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.other.SheetScroller;
import com.document.render.office.ss.util.HeaderUtil;


public class ColumnHeader {

    private SheetView sheetview;

    private int columnHeaderHeight = SSConstant.DEFAULT_COLUMN_HEADER_HEIGHT;
    private float x;
    private Rect rect;


    public ColumnHeader(SheetView sheetView) {
        this.sheetview = sheetView;

        rect = new Rect();
    }

    public int getColumnRightBound(Canvas canvas, float zoom) {
        canvas.save();
        Rect clip = canvas.getClipBounds();
        Paint paint = PaintKit.instance().getPaint();
        paint.setTextSize(SSConstant.HEADER_TEXT_FONTSZIE * zoom);

        x = sheetview.getRowHeaderWidth();

        layoutColumnLine(canvas, 0, zoom, paint);

        canvas.restore();

        return Math.min((int) x, clip.right);
    }

    private void layoutColumnLine(Canvas canvas, int columnStart, float zoom, Paint paint) {

        float w = 0;
        Rect clip = canvas.getClipBounds();

        Sheet sheet = sheetview.getCurrentSheet();
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();
        int colIndex = minRowAndColumnInformation.getMinColumnIndex() > columnStart ? minRowAndColumnInformation.getMinColumnIndex() : columnStart;
        if (!minRowAndColumnInformation.isColumnAllVisible()) {
            colIndex += 1;
            x += (minRowAndColumnInformation.getVisibleColumnWidth() * zoom);
        }

        int maxSheetColumns = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXCOLUMN_03 : Workbook.MAXCOLUMN_07;
        while (x <= clip.right && colIndex < maxSheetColumns) {
            if (sheet.isColumnHidden(colIndex)) {
                colIndex++;
                continue;
            }

            w = (sheet.getColumnPixelWidth(colIndex) * zoom);
            x += w;
            colIndex++;
        }
    }


    public void draw(Canvas canvas, int bottomBound, float zoom) {
        canvas.save();
        Paint paint = PaintKit.instance().getPaint();


        int oldColor = paint.getColor();
        float oldTextSize = paint.getTextSize();

        paint.setTextSize(SSConstant.HEADER_TEXT_FONTSZIE * zoom);

        x = sheetview.getRowHeaderWidth();

        Rect clip = canvas.getClipBounds();

        drawColumnLine(canvas, bottomBound, 0, zoom, paint);


        paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
        canvas.drawRect(0, columnHeaderHeight, x, columnHeaderHeight + 1, paint);


        paint.setColor(oldColor);
        paint.setTextSize(oldTextSize);
        canvas.restore();
    }

    private void drawFirstVisibleColumn(Canvas canvas, float zoom, Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        float visibleColumnWidth = 0;
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();

        float columnWidht = (minRowAndColumnInformation.getColumnWidth() * zoom);
        visibleColumnWidth = (float) (minRowAndColumnInformation.getVisibleColumnWidth() * zoom);

        if (HeaderUtil.instance().isActiveColumn(sheetview.getCurrentSheet(), minRowAndColumnInformation.getMinColumnIndex())) {
            paint.setColor(SSConstant.ACTIVE_COLOR);
        } else {
            paint.setColor(SSConstant.HEADER_FILL_COLOR);
        }

        rect.set((int) x, 0, (int) (x + visibleColumnWidth), columnHeaderHeight);
        canvas.drawRect(rect, paint);


        paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
        canvas.drawRect(x, 0, x + 1, columnHeaderHeight, paint);


        canvas.save();
        canvas.clipRect(rect);
        paint.setColor(SSConstant.HEADER_TEXT_COLOR);

        String rowText = HeaderUtil.instance().getColumnHeaderTextByIndex(minRowAndColumnInformation.getMinColumnIndex());
        float textWidth = paint.measureText(rowText);
        float textX = (columnWidht - textWidth) / 2;
        float textY = (int) (columnHeaderHeight - Math.ceil(fm.descent - fm.ascent)) / 2;
        canvas.drawText(rowText, x + textX - (columnWidht - visibleColumnWidth), textY - fm.ascent, paint);

        canvas.restore();
    }


    private void drawColumnLine(Canvas canvas, int bottomBound, int columnStart, float zoom, Paint paint) {
        FontMetrics fm = paint.getFontMetrics();

        float w = 0;
        Rect clip = canvas.getClipBounds();

        Sheet sheet = sheetview.getCurrentSheet();
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();
        int colIndex = minRowAndColumnInformation.getMinColumnIndex() > columnStart ? minRowAndColumnInformation.getMinColumnIndex() : columnStart;
        if (!minRowAndColumnInformation.isColumnAllVisible()) {
            drawFirstVisibleColumn(canvas, zoom, paint);
            colIndex += 1;
            x += (minRowAndColumnInformation.getVisibleColumnWidth() * zoom);
        }

        int maxSheetColumns = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXCOLUMN_03 : Workbook.MAXCOLUMN_07;
        while (x <= clip.right && colIndex < maxSheetColumns) {
            if (sheet.isColumnHidden(colIndex)) {

                paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
                canvas.drawRect(x - 1, 0, x + 1, columnHeaderHeight, paint);

                colIndex++;
                continue;
            }

            w = (sheet.getColumnPixelWidth(colIndex) * zoom);

            if (HeaderUtil.instance().isActiveColumn(sheetview.getCurrentSheet(), colIndex)) {
                paint.setColor(SSConstant.ACTIVE_COLOR);
            } else {
                paint.setColor(SSConstant.HEADER_FILL_COLOR);
            }

            rect.set((int) x, 0, (int) (x + w), columnHeaderHeight);
            canvas.drawRect(rect, paint);

            if (colIndex != minRowAndColumnInformation.getMinColumnIndex()) {

                paint.setColor(SSConstant.GRIDLINE_COLOR);
                canvas.drawRect(x, 0, x + 1, bottomBound, paint);
            }

            paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
            canvas.drawRect(x, 0, x + 1, columnHeaderHeight, paint);


            canvas.save();
            canvas.clipRect(rect);
            paint.setColor(SSConstant.HEADER_TEXT_COLOR);

            String colText = HeaderUtil.instance().getColumnHeaderTextByIndex(colIndex);
            int textWidth = (int) paint.measureText(colText);
            float textX = (w - textWidth) / 2;
            float textY = (int) (columnHeaderHeight - Math.ceil(fm.descent - fm.ascent)) / 2;
            canvas.drawText(colText, x + textX, textY - fm.ascent, paint);

            canvas.restore();
            x += w;
            colIndex++;
        }



        paint.setColor(SSConstant.GRIDLINE_COLOR);
        canvas.drawRect(x, 0, x + 1, bottomBound, paint);

        paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
        canvas.drawRect(x, 0, x + 1, columnHeaderHeight, paint);


        if (x < clip.right) {
            paint.setColor(SSConstant.HEADER_FILL_COLOR);
            rect.set((int) x + 1, 0, clip.right, clip.bottom);
            canvas.drawRect(rect, paint);
        }
    }


    public int getColumnHeaderHeight() {
        return columnHeaderHeight;
    }


    public void setColumnHeaderHeight(int columnHeaderHeight) {
        this.columnHeaderHeight = columnHeaderHeight;
    }


    public void calculateColumnHeaderHeight(float zoom) {
        columnHeaderHeight = Math.round(SSConstant.DEFAULT_COLUMN_HEADER_HEIGHT * zoom);
    }


    public void dispose() {
        sheetview = null;
        rect = null;
    }
}
