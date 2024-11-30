

package com.document.render.office.simpletext.control;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.view.IView;
import com.document.render.office.wp.view.WPViewKit;


public class Highlight implements IHighlight {

    private boolean isPaintHighlight = true;

    private long selectStart = 0;

    private long selectEnd = 0;

    private IWord word;

    private Paint paint;


    public Highlight(IWord word) {
        this.word = word;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xA0BBDDFF);

    }


    public void draw(Canvas canvas, IView line, int originX, int originY, long start, long end, float zoom) {
        if (!isSelectText() || end <= selectStart || start > selectEnd || !isPaintHighlight) {
            return;
        }
        start = Math.max(start, selectStart);
        IView leaf = line.getView(start, WPViewConstant.LEAF_VIEW, false);
        if (leaf == null) {
            return;
        }

        Rectangle sRect = new Rectangle();
        word.modelToView(start, sRect, false);

        long leafEnd = leaf.getEndOffset(null);
        long paintEnd = Math.min(end, selectEnd);

        int x = sRect.x;
        int y = originY;
        int w = leaf.getWidth();

        if (start == selectStart) {
            Rectangle leafRect = WPViewKit.instance().getAbsoluteCoordinate(leaf,
                    WPViewConstant.PAGE_ROOT, new Rectangle());
            if (word.getEditType() == MainConstant.APPLICATION_TYPE_PPT
                    && word.getTextBox() != null) {
                leafRect.x += word.getTextBox().getBounds().x;
                leafRect.y += word.getTextBox().getBounds().y;
            }
            w -= (sRect.x - leafRect.x);
        }

        int h = line.getLayoutSpan(WPViewConstant.Y_AXIS);
        IView parent = line.getParentView();
        if (parent != null) {

            if (line.getPreView() == null) {
                y -= parent.getTopIndent() * zoom;
                h += parent.getTopIndent();
            }

            if (line.getNextView() == null) {
                h += parent.getBottomIndent();
            }
        }

        while (leafEnd <= paintEnd) {
            canvas.drawRect(x * zoom, y, (x + w) * zoom, y + h * zoom, paint);
            x += w;
            leaf = leaf.getNextView();
            if (leaf == null) {
                break;
            }
            w = leaf.getWidth();
            leafEnd = leaf.getEndOffset(null);
        }


        if (end >= selectEnd) {
            Rectangle eRect = new Rectangle();
            word.modelToView(selectEnd, eRect, false);
            if (eRect.x > x) {
                canvas.drawRect(x * zoom, y, eRect.x * zoom, y + h * zoom, paint);
            }
        }
    }


    public String getSelectText() {
        if (isSelectText()) {
            return word.getDocument().getText(selectStart, selectEnd);
        }
        return "";

    }


    public boolean isSelectText() {
        return selectStart != selectEnd;
    }


    public void removeHighlight() {
        this.selectStart = 0;
        this.selectEnd = 0;
    }


    public void addHighlight(long start, long end) {
        this.selectStart = start;
        this.selectEnd = end;
    }


    public long getSelectStart() {
        return selectStart;
    }


    public void setSelectStart(long selectStart) {
        this.selectStart = selectStart;
    }


    public long getSelectEnd() {
        return selectEnd;
    }


    public void setSelectEnd(long selectEnd) {
        this.selectEnd = selectEnd;
    }


    public void setPaintHighlight(boolean isPaintHighlight) {
        this.isPaintHighlight = isPaintHighlight;
    }


    public void dispose() {
        word = null;
        paint = null;
    }

}
