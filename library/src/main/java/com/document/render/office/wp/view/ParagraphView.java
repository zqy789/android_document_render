
package com.document.render.office.wp.view;

import android.graphics.Canvas;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.objectpool.IMemObj;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.AbstractView;
import com.document.render.office.simpletext.view.IView;



public class ParagraphView extends AbstractView implements IMemObj {


    private BNView bnView;


    public ParagraphView(IElement elem) {
        this.elem = elem;
    }

    public String getText() {
        return elem.getText(null);
    }


    public short getType() {
        return WPViewConstant.PARAGRAPH_VIEW;
    }


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {
        if (getChildView() == null) {
            buildLine();
        }
        IView view = getView(offset, WPViewConstant.LINE_VIEW, isBack);
        if (view != null) {
            view.modelToView(offset, rect, isBack);
        }
        rect.x += getX();
        rect.y += getY();
        return rect;
    }


    public long viewToModel(int x, int y, boolean isBack) {
        if (getChildView() == null) {
            buildLine();
        }
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


    public void draw(Canvas canvas, int originX, int originY, float zoom) {
        if (getChildView() == null) {
            buildLine();
        }
        int dX = (int) (x * zoom) + originX;
        int dY = (int) (y * zoom) + originY;
        if (bnView != null) {
            bnView.draw(canvas, dX, dY, zoom);
        }
        super.draw(canvas, originX, originY, zoom);
    }


    public BNView getBNView() {
        return this.bnView;
    }


    public void setBNView(BNView bnView) {
        this.bnView = bnView;
    }


    private void buildLine() {
        IDocument doc = getDocument();
        if (doc != null) {
            LayoutKit.instance().buildLine(doc, this);
        }
    }


    public void free() {

    }


    public IMemObj getCopy() {
        return null;
    }


    public void dispose() {
        super.dispose();
        if (bnView != null) {
            bnView.dispose();
            bnView = null;
        }
    }
}
