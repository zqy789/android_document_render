
package com.document.render.office.wp.view;

import android.graphics.Rect;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.AbstractView;
import com.document.render.office.simpletext.view.IView;


public class RowView extends AbstractView {


    private boolean isExactlyHeight;


    public RowView(IElement elem) {
        this.elem = elem;
    }


    public short getType() {
        return WPViewConstant.TABLE_ROW_VIEW;
    }


    public boolean intersection(Rect rect, int originX, int originY, float zoom) {
        return true;
    }


    public boolean isExactlyHeight() {
        return isExactlyHeight;
    }


    public void setExactlyHeight(boolean isExactlyHeight) {
        this.isExactlyHeight = isExactlyHeight;
    }


    public CellView getCellView(short index) {
        int t = 0;
        CellView cellView = (CellView) getChildView();
        while (cellView != null) {
            if (t == index) {
                break;
            }
            t++;
            cellView = (CellView) cellView.getNextView();
        }
        return cellView;
    }


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {
        IView view = getView(offset, WPViewConstant.TABLE_CELL_VIEW, isBack);
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
                if (y >= view.getY() && y < view.getY() + view.getLayoutSpan(WPViewConstant.Y_AXIS)
                        && x >= view.getX() && x <= view.getX() + view.getLayoutSpan(WPViewConstant.X_AXIS)) {
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


    public void dispose() {
        super.dispose();
    }

}
