
package com.document.render.office.ss.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;

import com.document.render.office.common.PaintKit;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.other.SheetScroller;
import com.document.render.office.ss.util.HeaderUtil;


public class RowHeader {
    private static final int EXTEDES_WIDTH = 10;

    private SheetView sheetview;

    private int rowHeaderWidth = SSConstant.DEFAULT_ROW_HEADER_WIDTH;

    private float y;
    private Rect rect;


    public RowHeader(SheetView sheetView) {
        this.sheetview = sheetView;
    }


    public int getRowBottomBound(Canvas canvas, float zoom) {
        canvas.save();
        Rect clip = canvas.getClipBounds();

        Paint paint = PaintKit.instance().getPaint();
        paint.setTextSize(SSConstant.HEADER_TEXT_FONTSZIE * zoom);

        y = (SSConstant.DEFAULT_COLUMN_HEADER_HEIGHT * zoom);


        layoutRowLine(canvas, 0, zoom, paint);

        canvas.restore();

        return Math.min((int) y, clip.bottom);
    }

    private void layoutRowLine(Canvas canvas, int rowStart, float zoom, Paint paint) {
        Rect clip = canvas.getClipBounds();

        float h = 0;
        Sheet sheet = sheetview.getCurrentSheet();
        Row row;
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();
        int rowIndex = minRowAndColumnInformation.getMinRowIndex() > rowStart ? minRowAndColumnInformation.getMinRowIndex() : rowStart;
        if (!minRowAndColumnInformation.isRowAllVisible()) {
            rowIndex += 1;
            y += (minRowAndColumnInformation.getVisibleRowHeight() * zoom);
        }

        int maxSheetRows = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXROW_03 : Workbook.MAXROW_07;
        while (y <= clip.bottom && rowIndex < maxSheetRows) {
            row = sheet.getRow(rowIndex);
            if (row != null && row.isZeroHeight()) {
                rowIndex++;
                continue;
            }

            h = row == null ? sheetview.getCurrentSheet().getDefaultRowHeight() : row.getRowPixelHeight();
            h = (h * zoom);
            y += h;
            rowIndex++;
        }
    }


    public void draw(Canvas canvas, int rightBound, float zoom) {
        canvas.save();
        Paint paint = PaintKit.instance().getPaint();


        int oldColor = paint.getColor();
        float oldTextSize = paint.getTextSize();

        paint.setTextSize(SSConstant.HEADER_TEXT_FONTSZIE * zoom);

        y = (SSConstant.DEFAULT_COLUMN_HEADER_HEIGHT * zoom);

        rect = canvas.getClipBounds();
        rect.set(0, 0, rowHeaderWidth, rect.bottom);

        paint.setColor(SSConstant.HEADER_FILL_COLOR);
        canvas.drawRect(rect, paint);


        drawRowLine(canvas, rightBound, 0, zoom, paint);


        paint.setColor(oldColor);
        paint.setTextSize(oldTextSize);

        canvas.restore();
    }


    private void drawFirstVisibleHeader(Canvas canvas, float rightBound, float zoom, Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        Rect clip = canvas.getClipBounds();
        float visibleRowHeight = 0;
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();
        float rowHeight = (minRowAndColumnInformation.getRowHeight() * zoom);
        visibleRowHeight = (float) (minRowAndColumnInformation.getVisibleRowHeight() * zoom);



        if (HeaderUtil.instance().isActiveRow(sheetview.getCurrentSheet(), minRowAndColumnInformation.getMinRowIndex())) {
            paint.setColor(SSConstant.ACTIVE_COLOR);
        } else {
            paint.setColor(SSConstant.HEADER_FILL_COLOR);
        }

        rect.set(0, (int) y, rowHeaderWidth, (int) (y + visibleRowHeight));
        canvas.drawRect(rect, paint);


        paint.setColor(SSConstant.GRIDLINE_COLOR);
        canvas.drawRect(0, y, rightBound, y + 1, paint);

        paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
        canvas.drawRect(0, y, rowHeaderWidth, y + 1, paint);

        canvas.save();
        canvas.clipRect(rect);
        paint.setColor(SSConstant.HEADER_TEXT_COLOR);

        String rowText = String.valueOf(minRowAndColumnInformation.getMinRowIndex() + 1);
        int textWidth = (int) paint.measureText(rowText);
        int textX = (rowHeaderWidth - textWidth) / 2;
        int textY = (int) (rowHeight - Math.ceil(fm.descent - fm.ascent));
        canvas.drawText(rowText, textX, y + textY - fm.ascent - (rowHeight - visibleRowHeight), paint);

        canvas.restore();
    }

    private void drawRowLine(Canvas canvas, int rightBound, int rowStart, float zoom, Paint paint) {
        FontMetrics fm = paint.getFontMetrics();
        Rect clip = canvas.getClipBounds();


        float h = 0;
        Sheet sheet = sheetview.getCurrentSheet();
        Row row;
        SheetScroller minRowAndColumnInformation = sheetview.getMinRowAndColumnInformation();
        int rowIndex = minRowAndColumnInformation.getMinRowIndex() > rowStart ? minRowAndColumnInformation.getMinRowIndex() : rowStart;
        if (!minRowAndColumnInformation.isRowAllVisible()) {

            drawFirstVisibleHeader(canvas, rightBound, zoom, paint);
            rowIndex += 1;
            y += (minRowAndColumnInformation.getVisibleRowHeight() * zoom);
        }

        int maxSheetRows = sheet.getWorkbook().isBefore07Version() ? Workbook.MAXROW_03 : Workbook.MAXROW_07;
        while (y <= clip.bottom && rowIndex < maxSheetRows) {
            row = sheet.getRow(rowIndex);

            if (row != null && row.isZeroHeight()) {

                paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
                canvas.drawRect(0, y - 1, rowHeaderWidth, y + 1, paint);
                rowIndex++;
                continue;
            }

            h = row == null ? sheetview.getCurrentSheet().getDefaultRowHeight() : row.getRowPixelHeight();
            h = (h * zoom);



            if (HeaderUtil.instance().isActiveRow(sheetview.getCurrentSheet(), rowIndex)) {
                paint.setColor(SSConstant.ACTIVE_COLOR);
            } else {
                paint.setColor(SSConstant.HEADER_FILL_COLOR);
            }

            rect.set(0, (int) y, rowHeaderWidth, (int) (y + h));
            canvas.drawRect(rect, paint);


            paint.setColor(SSConstant.GRIDLINE_COLOR);
            canvas.drawRect(0, y, rightBound, y + 1, paint);

            paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
            canvas.drawRect(0, y, rowHeaderWidth, y + 1, paint);

            canvas.save();
            canvas.clipRect(rect);
            paint.setColor(SSConstant.HEADER_TEXT_COLOR);

            String rowText = String.valueOf(rowIndex + 1);
            int textWidth = (int) paint.measureText(rowText);
            int textX = (rowHeaderWidth - textWidth) / 2;
            int textY = (int) (h - Math.ceil(fm.descent - fm.ascent));
            canvas.drawText(rowText, textX, y + textY - fm.ascent, paint);

            canvas.restore();

            y += h;
            rowIndex++;
        }


        paint.setColor(SSConstant.GRIDLINE_COLOR);
        canvas.drawRect(0, y, rightBound, y + 1, paint);

        paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
        canvas.drawRect(0, y, rowHeaderWidth, y + 1, paint);


        if (y < clip.bottom) {
            paint.setColor(SSConstant.HEADER_FILL_COLOR);
            rect.set(0, (int) (y + 1), clip.right, clip.bottom);
            canvas.drawRect(rect, paint);
        }

        paint.setColor(SSConstant.HEADER_GRIDLINE_COLOR);
        canvas.drawRect(rowHeaderWidth, 0, rowHeaderWidth + 1, y, paint);
    }


    public void calculateRowHeaderWidth(float zoom) {
        Paint paint = PaintKit.instance().getPaint();
        paint.setTextSize(SSConstant.HEADER_TEXT_FONTSZIE);
        rowHeaderWidth = Math.round(paint.measureText(String.valueOf(sheetview.getCurrentMinRow()))) + EXTEDES_WIDTH;
        rowHeaderWidth = Math.round(Math.max(rowHeaderWidth, SSConstant.DEFAULT_ROW_HEADER_WIDTH) * zoom);
    }


    public int getRowHeaderWidth() {
        return rowHeaderWidth;
    }


    public void setRowHeaderWidth(int rowHeaderWidth) {
        this.rowHeaderWidth = rowHeaderWidth;
    }


    public void dispose() {
        sheetview = null;
        rect = null;
    }
}
