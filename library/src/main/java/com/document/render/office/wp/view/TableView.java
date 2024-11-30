
package com.document.render.office.wp.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.IView;


public class TableView extends ParagraphView {

    private boolean isBreakPages;


    public TableView(IElement elem) {
        super(elem);
    }


    public void draw(Canvas canvas, int originX, int originY, float zoom) {


        float tX = (x * zoom) + originX;
        float tY = (y * zoom) + originY;
        RowView row = (RowView) getChildView();
        Rect clip = canvas.getClipBounds();
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        while (row != null) {
            float rX = 0;
            float rY = 0;
            boolean isFirstRow = true;
            float rowHeight = 0;
            if (row.intersection(clip, (int) tX, (int) tY, zoom)) {
                rX = tX + row.getX() * zoom;
                if (isFirstRow) {
                    rY = tY + row.getY() * zoom;
                    isFirstRow = false;
                } else {
                    rY += rowHeight;
                }
                rowHeight = row.getHeight() * zoom;
                CellView cell = (CellView) row.getChildView();
                float cX = 0;
                float cY = 0;
                float cW = 0;
                float cH = 0;
                float cRight = 0;
                boolean isFirstCell = true;
                while (cell != null) {
                    if (cell.intersection(clip, (int) rX, (int) rY, zoom)) {
                        if (cell.isMergedCell() && !cell.isFirstMergedCell()) {
                            cell = (CellView) cell.getNextView();
                            isFirstCell = true;
                            continue;
                        }
                        cY = rY + cell.getY() * zoom;
                        if (isFirstCell) {
                            cX = rX + cell.getX() * zoom;
                            isFirstCell = false;
                        } else {
                            cX += cW;
                        }
                        cW = cell.getLayoutSpan(WPViewConstant.X_AXIS) * zoom;
                        cH = Math.max(cell.getHeight() * zoom, rowHeight);
                        cRight = cX + cW;

                        if (cell.isValidLastCell()) {
                            if (Math.abs(cRight - (tX + getWidth() * zoom)) <= 10) {
                                cRight = tX + getWidth() * zoom;
                            }
                        }

                        if (cell.getBackground() != -1) {
                            int old = paint.getColor();
                            paint.setColor(cell.getBackground());
                            paint.setStyle(Style.FILL);
                            canvas.drawRect(cX, cY, cRight, cY + cH, paint);
                            paint.setColor(old);
                        }

                        paint.setStyle(Style.STROKE);
                        canvas.drawRect(cX, cY, cRight, cY + cH, paint);

                        canvas.save();
                        canvas.clipRect(cX, cY, cRight, cY + cH);
                        cell.draw(canvas, (int) rX, (int) rY, zoom);
                        canvas.restore();
                    }
                    cell = (CellView) cell.getNextView();
                }
            }
            row = (RowView) row.getNextView();
        }
    }


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {
        IView view = getView(offset, WPViewConstant.TABLE_ROW_VIEW, isBack);
        if (view != null) {
            view.modelToView(offset, rect, isBack);
        }
        rect.x += getX();
        rect.y += getY();
        return rect;
    }


    public long viewToModel(int x, int y, boolean isBack) {
        x -= getX();
        y -= getY();

        IView view = getChildView();
        if (view != null && y > view.getY()) {
            while (view != null) {
                if (y >= view.getY() && y < view.getY() + view.getLayoutSpan(WPViewConstant.Y_AXIS)) {
                    break;
                }
                view = view.getNextView();
            }
        }
        view = view == null ? getChildView() : view;
        if (view != null) {
            return view.viewToModel(x, y, isBack);
        }
        return -1;
    }


    public short getType() {
        return WPViewConstant.TABLE_VIEW;
    }


    public void dispose() {
        super.dispose();
    }


    public boolean isBreakPages() {
        return isBreakPages;
    }


    public void setBreakPages(boolean isBreakPages) {
        this.isBreakPages = isBreakPages;
    }

}
