
package com.document.render.office.simpletext.view;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.system.IControl;

import java.util.List;


public abstract class AbstractView implements IView {

    protected IElement elem;

    protected int x;

    protected int y;

    protected int width;

    protected int height;

    protected int topIndent;

    protected int bottomIndent;

    protected int leftIndent;

    protected int rightIndent;

    protected long start;

    protected long end;

    protected IView parent;

    protected IView child;

    protected IView preView;

    protected IView nextView;


    public IElement getElement() {
        return this.elem;
    }


    public void setElement(IElement elem) {
        this.elem = elem;
    }


    public short getType() {
        return -1;
    }


    public int getX() {
        return x;
    }


    public void setX(int x) {
        this.x = x;
    }


    public int getY() {
        return y;
    }


    public void setY(int y) {
        this.y = y;
    }


    public int getWidth() {
        return width;
    }


    public void setWidth(int width) {
        this.width = width;
    }


    public int getHeight() {
        return height;
    }


    public void setHeight(int height) {
        this.height = height;
    }


    public long getStartOffset(IDocument doc) {
        return start;
    }


    public void setStartOffset(long start) {
        this.start = start;
    }


    public long getElemStart(IDocument doc) {
        return elem.getStartOffset();
    }


    public long getEndOffset(IDocument doc) {
        return end;
    }


    public long getElemEnd(IDocument doc) {
        return elem.getEndOffset();
    }


    public IView getParentView() {
        return parent;
    }


    public void setParentView(IView parent) {
        this.parent = parent;
    }


    public IView getChildView() {
        return child;
    }


    public void setChildView(IView child) {
        this.child = child;
    }


    public IView getPreView() {
        return preView;
    }


    public void setPreView(IView preView) {
        this.preView = preView;
    }


    public IView getNextView() {
        return nextView;
    }


    public void setNextView(IView nextView) {
        this.nextView = nextView;
    }


    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
    }


    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public void setBound(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = y;

    }


    public IWord getContainer() {
        IView parent = getParentView();
        if (parent != null) {
            return parent.getContainer();
        }
        return null;
    }


    public IControl getControl() {
        IView parent = getParentView();
        if (parent != null) {
            return parent.getControl();
        }
        return null;
    }


    public IDocument getDocument() {
        IView parent = getParentView();
        if (parent != null) {
            return parent.getDocument();
        }
        return null;
    }


    public IView getLastView() {
        IView temp = getChildView();
        if (temp == null) {
            return null;
        }
        while (temp.getNextView() != null) {
            temp = temp.getNextView();
        }
        return temp;
    }


    public int getChildCount() {
        int count = 0;
        IView temp = getChildView();
        while (temp != null) {
            count++;
            temp = temp.getNextView();
        }
        return count;
    }


    public void appendChlidView(IView view) {
        view.setParentView(this);
        if (child == null) {
            child = view;
            return;
        }
        IView lastView = getLastView();
        view.setPreView(lastView);
        lastView.setNextView(view);
    }


    public void insertView(IView view, IView newView) {
        newView.setParentView(this);

        if (view == null) {
            if (child == null) {
                child = newView;
            } else {
                newView.setNextView(child);
                child.setPreView(newView);
                child = newView;
            }
        }

    }


    public void deleteView(IView view, boolean isDeleteChild) {
        view.setParentView(null);
        if (view == child) {
            child = null;
        } else {
            IView pre = view.getPreView();
            IView next = view.getNextView();
            pre.setNextView(next);
            if (next != null) {
                next.setPreView(pre);
            }
        }
        if (isDeleteChild) {
            view.dispose();
        }
    }


    public void setEndOffset(long end) {
        this.end = end;

    }


    public Rect getViewRect(int originX, int originY, float zoom) {
        int tw = (int) (getLayoutSpan(WPViewConstant.X_AXIS) * zoom);
        int th = (int) (getLayoutSpan(WPViewConstant.Y_AXIS) * zoom);

        int tx = (int) (x * zoom) + originX;
        int ty = (int) (y * zoom) + originY;
        return new Rect(tx, ty, tx + tw, ty + th);
    }


    public boolean intersection(Rect rect, int originX, int originY, float zoom) {
        int tw = (int) (getLayoutSpan(WPViewConstant.X_AXIS) * zoom);
        int th = (int) (getLayoutSpan(WPViewConstant.Y_AXIS) * zoom);
        int rw = rect.right - rect.left;
        int rh = rect.bottom - rect.top;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = (int) (x * zoom) + originX;
        int ty = (int) (y * zoom) + originY;
        int rx = rect.left;
        int ry = rect.top;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;

        return ((rw < rx || rw > tx)
                && (rh < ry || rh > ty)
                && (tw < tx || tw > rx)
                && (th < ty || th > ry));
    }


    public boolean contains(long offset, boolean isBack) {
        IDocument doc = getDocument();
        long start = getStartOffset(doc);
        long end = getEndOffset(doc);
        return offset >= start && (offset < end || (offset == end && isBack));
    }


    public boolean contains(int x, int y, boolean isBack) {
        return x >= this.x && x < this.x + this.width
                && y >= this.y && y < this.y + getHeight();
    }


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {
        return null;
    }


    public long viewToModel(int x, int y, boolean isBack) {
        return 0;
    }


    public long getNextForCoordinate(long offset, int dir, int x, int y) {
        return 0;
    }


    public long getNextForOffset(long offset, int dir, int x, int y) {
        return 0;
    }


    public int doLayout(int x, int y, int w, int h, long maxEnd, int flag) {
        return WPViewConstant.BREAK_NO;
    }


    public void draw(Canvas canvas, int originX, int originY, float zoom) {
        int dX = (int) (x * zoom) + originX;
        int dY = (int) (y * zoom) + originY;
        IView view = getChildView();
        Rect clip = canvas.getClipBounds();
        while (view != null) {
            if (view.intersection(clip, dX, dY, zoom)) {
                view.draw(canvas, dX, dY, zoom);
            }
            view = view.getNextView();
        }

    }

    public void getLineHeight(List<Integer> linesHeight) {
        IView view = getChildView();
        if (view != null) {
            while (view != null) {
                linesHeight.add(view.getHeight());
                view = view.getNextView();
            }
        }
    }


    public int getTopIndent() {
        return topIndent;
    }


    public void setTopIndent(int topIndent) {
        this.topIndent = topIndent;
    }


    public int getBottomIndent() {
        return bottomIndent;
    }


    public void setBottomIndent(int bottomIndent) {
        this.bottomIndent = bottomIndent;
    }


    public int getLeftIndent() {
        return leftIndent;
    }


    public void setLeftIndent(int leftIndent) {
        this.leftIndent = leftIndent;
    }


    public int getRightIndent() {
        return rightIndent;
    }


    public void setRightIndent(int rightIndent) {
        this.rightIndent = rightIndent;
    }


    public int getLayoutSpan(byte axis) {

        if (axis == WPViewConstant.X_AXIS) {
            return rightIndent + width + leftIndent;
        }

        else {
            return topIndent + getHeight() + bottomIndent;
        }
    }


    public void setIndent(int left, int top, int right, int bottom) {
        this.leftIndent = left;
        this.topIndent = top;
        this.rightIndent = right;
        this.bottomIndent = bottom;
    }


    public IView getView(long offset, int type, boolean isBack) {
        IView view = child;
        while (view != null && !view.contains(offset, isBack)) {
            view = view.getNextView();
        }
        if (view != null && view.getType() != type) {
            return view.getView(offset, type, isBack);
        }
        return view;
    }


    public IView getView(int x, int y, int type, boolean isBack) {
        IView view = child;
        while (view != null && !view.contains(x, y, isBack)) {
            view = view.getNextView();
        }
        if (view != null && view.getType() != type) {
            x -= this.x;
            y -= this.y;
            return view.getView(x, y, type, isBack);
        }
        return view;
    }


    public void dispose() {
        this.parent = null;
        elem = null;
        IView temp = child;
        IView next;
        while (temp != null) {
            next = temp.getNextView();
            temp.dispose();
            temp = next;
        }
        this.preView = null;
        this.nextView = null;
        this.child = null;
    }


    public void free() {

    }

}
