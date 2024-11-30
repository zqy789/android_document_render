

package com.document.render.office.wp.control;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.document.render.office.common.hyperlink.Hyperlink;
import com.document.render.office.common.picture.PictureKit;
import com.document.render.office.constant.EventConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.system.IControl;
import com.document.render.office.system.beans.AEventManage;


public class WPEventManage extends AEventManage {
    private static final String TAG = "WPEventManage";

    protected Word word;

    private int oldX;

    private int oldY;


    public WPEventManage(Word word, IControl control) {
        super(word.getContext(), control);
        this.word = word;
    }


    public boolean onTouch(View v, MotionEvent event) {
        try {
            super.onTouch(v, event);
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    PictureKit.instance().setDrawPictrue(true);
                    processDown(v, event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (zoomChange) {
                        zoomChange = false;
                        if (word.getCurrentRootType() == WPViewConstant.PAGE_ROOT) {
                            control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                        }
                        if (control.getMainFrame().isZoomAfterLayoutForWord()) {
                            control.actionEvent(EventConstant.WP_LAYOUT_NORMAL_VIEW, null);
                        }
                    }
                    word.getControl().actionEvent(EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS, null);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {

            Log.d(TAG, "onTouch: " + e.getMessage());
            control.getSysKit().getErrorKit().writerLog(e);
        }
        return false;
    }


    protected void processDown(View v, MotionEvent event) {
        int x = convertCoorForX(event.getX());
        int y = convertCoorForY(event.getY());
        long offset = word.viewToModel(x, y, false);
        if (word.getHighlight().isSelectText()) {
            word.getHighlight().removeHighlight();
            word.getStatus().setPressOffset(offset);
            word.postInvalidate();
        }
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (word.getStatus().isSelectTextStatus()) {
            return true;
        }
        super.onScroll(e1, e2, distanceX, distanceY);

        boolean change = false;
        boolean isScrollX = Math.abs(distanceX) > Math.abs(distanceY);
        Rectangle r = word.getVisibleRect();
        int sX = r.x;
        int sY = r.y;
        float zoom = word.getZoom();
        int wW = 0;
        if (word.getCurrentRootType() == WPViewConstant.NORMAL_ROOT
                && control.getMainFrame().isZoomAfterLayoutForWord()) {
            if (word.getWidth() == word.getWordWidth()) {
                wW = word.getWidth();
            } else {
                wW = (int) (word.getWordWidth() * zoom);
            }
        } else {
            wW = (int) (word.getWordWidth() * zoom);
        }
        int wH = (int) (word.getWordHeight() * zoom);

        if (isScrollX) {

            if (distanceX > 0 && sX + r.width < wW) {
                sX += distanceX;
                if (sX + r.width > wW) {
                    sX = wW - r.width;
                }
                change = true;
            }

            else if (distanceX < 0 && sX > 0) {
                sX += distanceX;
                if (sX < 0) {
                    sX = 0;
                }
                change = true;
            }
        }

        else {

            if (distanceY > 0 && sY + r.height < wH) {
                sY += distanceY;
                if (sY + r.height > wH) {
                    sY = wH - r.height;
                }
                change = true;
            }

            else if (distanceY < 0 && sY > 0) {
                sY += distanceY;
                if (sY < 0) {
                    sY = 0;
                }
                change = true;
            }
        }
        if (change) {
            isScroll = true;
            word.scrollTo(sX, sY);
        }
        return true;
    }


    public void fling(int velocityX, int velocityY) {
        super.fling(velocityX, velocityY);
        Rectangle r = ((Word) word).getVisibleRect();
        float zoom = word.getZoom();

        oldY = 0;
        oldX = 0;
        int wW = 0;
        if (word.getCurrentRootType() == WPViewConstant.NORMAL_ROOT
                && control.getMainFrame().isZoomAfterLayoutForWord()) {
            if (word.getWidth() == word.getWordWidth()) {
                wW = word.getWidth();
            } else {
                wW = (int) (word.getWordWidth() * zoom) + 5;
            }
        } else {
            wW = (int) (word.getWordWidth() * zoom);
        }
        if (Math.abs(velocityY) > Math.abs(velocityX)) {
            oldY = r.y;
            mScroller.fling(r.x, r.y, 0, velocityY, 0, r.x, 0, (int) (word.getWordHeight() * zoom) - r.height);
        }

        else {
            oldX = r.x;
            mScroller.fling(r.x, r.y, velocityX, 0, 0, wW - r.width, r.y, 0);
        }
        word.postInvalidate();

    }


    public boolean onDoubleTapEvent(MotionEvent e) {

        super.onDoubleTapEvent(e);
        return true;
    }


    public boolean onSingleTapUp(MotionEvent e) {
        super.onSingleTapUp(e);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            int x = convertCoorForX(e.getX());
            int y = convertCoorForY(e.getY());
            long offset = word.viewToModel(x, y, false);
            if (offset >= 0) {
                IElement leaf = word.getDocument().getLeaf(offset);
                if (leaf != null) {
                    int hyID = AttrManage.instance().getHperlinkID(leaf.getAttribute());
                    if (hyID >= 0) {
                        Hyperlink hylink = control.getSysKit().getHyperlinkManage().getHyperlink(hyID);
                        if (hylink != null) {
                            control.actionEvent(EventConstant.APP_HYPERLINK, hylink);
                        }
                    }
                }
            }
        }
        return true;
    }


    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            isFling = true;
            PictureKit.instance().setDrawPictrue(false);
            int sX = mScroller.getCurrX();
            int sY = mScroller.getCurrY();
            if ((oldX == sX && oldY == sY)
                    || (sX == word.getScrollX() && sY == word.getScrollY())) {
                PictureKit.instance().setDrawPictrue(true);
                mScroller.abortAnimation();
                word.postInvalidate();
                return;
            }
            oldX = sX;
            oldY = sY;
            word.scrollTo(sX, sY);
        } else {
            if (!PictureKit.instance().isDrawPictrue()) {
                PictureKit.instance().setDrawPictrue(true);
                word.postInvalidate();
            }
        }
    }


    protected int convertCoorForX(float x) {
        return (int) ((x + word.getScrollX()) / word.getZoom());
    }


    protected int convertCoorForY(float y) {
        return (int) ((y + word.getScrollY()) / word.getZoom());
    }


    public void dispose() {
        super.dispose();
        word = null;
    }
}
