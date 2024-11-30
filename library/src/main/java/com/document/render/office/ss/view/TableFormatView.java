package com.document.render.office.ss.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.document.render.office.common.PaintKit;
import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.style.BorderStyle;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.model.table.SSTable;
import com.document.render.office.ss.model.table.TableFormatManager;
import com.document.render.office.ss.util.ModelUtil;

public class TableFormatView {

    private SheetView sheetView;

    public TableFormatView(SheetView sheetView) {
        this.sheetView = sheetView;
    }

    public void draw(Canvas canvas) {
        Paint paint = PaintKit.instance().getPaint();

        int oldColor = paint.getColor();
        canvas.save();

        TableFormatManager formatMgr = sheetView.getCurrentSheet().getWorkbook().getTableFormatManager();
        SSTable[] tables = sheetView.getCurrentSheet().getTables();
        if (tables != null && formatMgr != null) {
            for (SSTable table : tables) {

                if (table.isHeaderRowShown() && (table.getHeaderRowDxfId() >= 0 || table.getHeaderRowBorderDxfId() >= 0)) {
                    drawHeaderRowFormat(canvas, formatMgr, table, paint);
                }


                if (table.isTotalRowShown() && (table.getTotalsRowDxfId() >= 0 || table.getTotalsRowBorderDxfId() >= 0)) {
                    drawTotalRowFormat(canvas, formatMgr, table, paint);
                }


                if (table.getTableBorderDxfId() >= 0) {
                    drawTableBorders(canvas, formatMgr, table, paint);
                }
            }
        }

        paint.setColor(oldColor);
        canvas.restore();
    }

    private void drawHeaderRowFormat(Canvas canvas, TableFormatManager formatMgr, SSTable table, Paint paint) {
        Workbook book = sheetView.getCurrentSheet().getWorkbook();

        CellRangeAddress ref = table.getTableReference();

        CellStyle headerRowDxf = formatMgr.getFormat(table.getHeaderRowDxfId());
        CellStyle headerRowBorderDxf = formatMgr.getFormat(table.getHeaderRowBorderDxfId());
        RectF rect = ModelUtil.instance().getCellAnchor(sheetView, ref.getFirstRow(), ref.getFirstColumn(), ref.getLastColumn());


        if (headerRowDxf != null) {
            drawFormatBorders(canvas, paint, book, headerRowDxf, rect);
        }

        if (headerRowBorderDxf != null) {
            drawFormatBorders(canvas, paint, book, headerRowBorderDxf, rect);
        }
    }

    private void drawTotalRowFormat(Canvas canvas, TableFormatManager formatMgr, SSTable table, Paint paint) {
        Workbook book = sheetView.getCurrentSheet().getWorkbook();

        CellRangeAddress ref = table.getTableReference();

        CellStyle totalsRowDxf = formatMgr.getFormat(table.getTotalsRowDxfId());
        CellStyle totalsRowBorderDxf = formatMgr.getFormat(table.getTotalsRowBorderDxfId());
        RectF rect = ModelUtil.instance().getCellAnchor(sheetView, ref.getLastRow(), ref.getFirstColumn(), ref.getLastColumn());


        if (totalsRowDxf != null) {
            drawFormatBorders(canvas, paint, book, totalsRowDxf, rect);
        }

        if (totalsRowBorderDxf != null) {
            drawFormatBorders(canvas, paint, book, totalsRowBorderDxf, rect);
        }
    }

    private void drawTableBorders(Canvas canvas, TableFormatManager formatMgr, SSTable table, Paint paint) {
        RectF rect = ModelUtil.instance().getCellRangeAddressAnchor(sheetView, table.getTableReference());

        drawFormatBorders(canvas, paint, sheetView.getCurrentSheet().getWorkbook(),
                formatMgr.getFormat(table.getTableBorderDxfId()), rect);
    }

    private void drawFormatBorders(Canvas canvas, Paint paint, Workbook book,
                                   CellStyle headerRowDxf, RectF rect) {

        if (rect.left > sheetView.getRowHeaderWidth() && headerRowDxf.getBorderLeft() != BorderStyle.BORDER_NONE) {
            paint.setColor(book.getColor(headerRowDxf.getBorderLeftColorIdx()));
            canvas.drawRect(rect.left, rect.top, rect.left + 1, rect.bottom, paint);
        }


        if (rect.top > sheetView.getColumnHeaderHeight() && headerRowDxf.getBorderTop() != BorderStyle.BORDER_NONE) {
            paint.setColor(book.getColor(headerRowDxf.getBorderTopColorIdx()));
            canvas.drawRect(rect.left, rect.top, rect.right, rect.top + 1, paint);
        }


        if (rect.right > sheetView.getRowHeaderWidth() && headerRowDxf.getBorderRight() != BorderStyle.BORDER_NONE) {
            paint.setColor(book.getColor(headerRowDxf.getBorderRightColorIdx()));
            canvas.drawRect(rect.right, rect.top, rect.right + 1, rect.bottom, paint);
        }


        if (rect.bottom > sheetView.getColumnHeaderHeight() && headerRowDxf.getBorderBottom() != BorderStyle.BORDER_NONE) {
            paint.setColor(book.getColor(headerRowDxf.getBorderBottomColorIdx()));
            canvas.drawRect(rect.left, rect.bottom, rect.right, rect.bottom + 1, paint);
        }
    }
}
