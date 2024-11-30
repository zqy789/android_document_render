
package com.document.render.office.wp.view;

import android.graphics.Canvas;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.AbstractView;
import com.document.render.office.simpletext.view.IView;


public class CellView extends AbstractView {


    private boolean isFirstMergedCell;

    private boolean isMergedCell;

    private short column;

    private int background = -1;



    public CellView(IElement elem) {
        this.elem = elem;
    }


    public void draw(Canvas canvas, int originX, int originY, float zoom) {
























        super.draw(canvas, originX, originY, zoom);
    }


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {
        IView view = getView(offset, WPViewConstant.LINE_VIEW, isBack);
        if (view != null) {
            view.modelToView(offset, rect, isBack);
        }
        rect.x += getX() + getLeftIndent();
        rect.y += getY() + getTopIndent();
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
        return WPViewConstant.TABLE_CELL_VIEW;
    }


    public boolean isFirstMergedCell() {
        return isFirstMergedCell;
    }


    public void setFirstMergedCell(boolean isFirstMergedCell) {
        this.isFirstMergedCell = isFirstMergedCell;
    }


    public boolean isMergedCell() {
        return isMergedCell;
    }


    public void setMergedCell(boolean isMergedCell) {
        this.isMergedCell = isMergedCell;
    }


    public boolean isValidLastCell() {
        if (getNextView() == null) {
            return true;
        }
        CellView nextCell = ((CellView) getNextView());
        if (isMergedCell()) {
            return nextCell.isValidLastCell();
        }
        if (nextCell.getStartOffset(null) == 0 && nextCell.getEndOffset(null) == 0) {
            return nextCell.isValidLastCell();
        }

        return false;
    }


    public short getColumn() {
        return column;
    }


    public void setColumn(short column) {
        this.column = column;
    }

    protected int getBackground() {
        return this.background;
    }

    public void setBackground(int color) {
        this.background = color;
    }


    public void dispose() {
        super.dispose();
    }
}
