

package com.document.render.office.system.beans.pagelist;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Scroller;
import android.widget.Toast;

import com.document.render.office.constant.MainConstant;

import java.util.NoSuchElementException;


public class APageListEventManage implements
        ScaleGestureDetector.OnScaleGestureListener, OnGestureListener, Runnable,
        OnTouchListener, OnDoubleTapListener, OnClickListener {

    private static final int MOVING_DIAGONALLY = 0;
    private static final int MOVING_LEFT = 1;
    private static final int MOVING_RIGHT = 2;
    private static final int MOVING_UP = 3;
    private static final int MOVING_DOWN = 4;

    private static final float MAX_ZOOM = 3.0f;

    protected GestureDetector gesture;

    protected Scroller mScroller;


    protected Toast toast = null;
    private boolean isOnFling;

    private boolean isOnScroll;

    private boolean isDoubleTap;

    private boolean isProcessOnScroll = true;

    private boolean isTouchEventIn;

    private boolean isScaling;

    private int mScrollerLastX;

    private int mScrollerLastY;

    private int mXScroll;

    private int mYScroll;

    private int eventPointerCount;

    private APageListView listView;

    private ScaleGestureDetector mScaleGestureDetector;


    public APageListEventManage(APageListView listView) {
        this.listView = listView;
        gesture = new GestureDetector(listView.getContext(), this);
        mScroller = new Scroller(listView.getContext());
        mScaleGestureDetector = new ScaleGestureDetector(listView.getContext(), this);
        toast = Toast.makeText(listView.getContext(), "", Toast.LENGTH_SHORT);
    }


    protected boolean zoom(MotionEvent event) {
        return false;
    }


    protected boolean processOnTouch(MotionEvent event) {
        eventPointerCount = event.getPointerCount();
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            isOnFling = false;
            isTouchEventIn = true;
        }
        if (mScaleGestureDetector != null) {
            mScaleGestureDetector.onTouchEvent(event);
        }
        if (!isScaling && gesture != null) {
            gesture.onTouchEvent(event);
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            isProcessOnScroll = true;
            isTouchEventIn = false;
            APageListItem pageView = listView.getCurrentPageView();
            if (pageView != null) {
                if (mScroller.isFinished()) {



                    if (!isDoubleTap) {
                        slideViewOntoScreen(pageView);
                    }
                }
                if (mScroller.isFinished() && isOnScroll) {


                    listView.getPageListViewListener().setDrawPictrue(true);
                    listView.postRepaint(pageView);
                }
            }
            isDoubleTap = false;
            isOnScroll = false;
            toast.cancel();

        }

        return true;
    }


    public boolean onTouch(View v, MotionEvent event) {
        listView.getPageListViewListener().onEventMethod(v, event, null, -1.0f, -1.0f, IPageListViewListener.ON_TOUCH);
        return false;
    }


    public boolean onDown(MotionEvent e) {
        mScroller.forceFinished(true);
        listView.getPageListViewListener().onEventMethod(listView, e, null, -1.0f, -1.0f, IPageListViewListener.ON_DOWN);
        return true;
    }


    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        listView.getPageListViewListener().onEventMethod(listView, e1, e2, velocityX, velocityY, IPageListViewListener.ON_FLING);
        if (!isProcessOnScroll || isDoubleTap) {
            return true;
        }
        View pageView = listView.getCurrentPageView();
        if (pageView != null) {
            Rect bounds = listView.getScrollBounds(pageView);
            if (listView.getPageListViewListener().getPageListViewMovingPosition() == IPageListViewListener.Moving_Horizontal) {
                if (pageView.getWidth() <= listView.getWidth()
                        || listView.getPageListViewListener().isChangePage()) {
                    switch (directionOfTravel(velocityX, velocityY)) {
                        case MOVING_LEFT:
                            if (bounds.left >= 0) {
                                isOnFling = true;
                                listView.nextPageView();
                                return true;
                            }
                            break;
                        case MOVING_RIGHT:
                            if (bounds.right <= 0) {
                                isOnFling = true;
                                listView.previousPageview();
                                return true;
                            }
                            break;
                    }
                }
            } else {
                if (pageView.getHeight() <= listView.getHeight()
                        || listView.getPageListViewListener().isChangePage()) {
                    switch (directionOfTravel(velocityX, velocityY)) {
                        case MOVING_UP:
                            if (bounds.top >= 0) {
                                isOnFling = true;
                                listView.nextPageView();
                                return true;
                            }
                            break;
                        case MOVING_DOWN:
                            if (bounds.bottom <= 0) {
                                isOnFling = true;
                                listView.previousPageview();
                                return true;
                            }
                            break;
                    }
                }
            }

            mScrollerLastX = mScrollerLastY = 0;









            Rect expandedBounds = new Rect(bounds);
            expandedBounds.inset(-100, -100);

            if (withinBoundsInDirectionOfTravel(bounds, velocityX, velocityY)
                    && expandedBounds.contains(0, 0)) {
                mScroller.fling(0, 0, (int) velocityX, (int) velocityY, bounds.left, bounds.right,
                        bounds.top, bounds.bottom);
                listView.post(this);
            }
        }
        return true;
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        listView.getPageListViewListener().onEventMethod(listView, e1, e2, distanceX, distanceY, IPageListViewListener.ON_SCROLL);
        if (isProcessOnScroll && !isDoubleTap) {
            listView.getPageListViewListener().setDrawPictrue(false);
            isOnScroll = true;
            mXScroll -= distanceX;
            mYScroll -= distanceY;
            if (!listView.getPageListViewListener().isChangePage()) {
                APageListItem item = listView.getCurrentPageView();
                if (item != null && item.getWidth() > listView.getWidth()) {
                    if (distanceX > 0) {
                        if (listView.getWidth() - mXScroll - item.getLeft() > item.getWidth()
                                && item.getPageIndex() < listView.getPageCount() - 1) {
                            mXScroll = -(item.getWidth() - listView.getWidth() + item.getLeft());
                        }
                    } else if (distanceX < 0) {
                        if (mXScroll + item.getLeft() > 0
                                && item.getPageIndex() != 0) {
                            mXScroll = 0;
                        }
                    }
                }
            }
            listView.requestLayout();
        }
        return true;
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (eventPointerCount <= 1 || !listView.getPageListViewListener().isTouchZoom()) {
            return true;
        }

        isTouchEventIn = true;
        float previousScale = listView.getZoom();
        float zoom = Math.min(Math.max(listView.getZoom() * detector.getScaleFactor(), listView.getFitZoom()), MAX_ZOOM);
        if ((int) (zoom * MainConstant.ZOOM_ROUND) != (int) (previousScale * MainConstant.ZOOM_ROUND)) {
            isOnScroll = true;
            float factor = zoom / previousScale;
            listView.setZoom(zoom, false);
            APageListItem v = listView.getCurrentPageView();
            if (v != null) {

                int viewFocusX = (int) detector.getFocusX() - (v.getLeft() + mXScroll);
                int viewFocusY = (int) detector.getFocusY() - (v.getTop() + mYScroll);

                mXScroll += viewFocusX - viewFocusX * factor;
                mYScroll += viewFocusY - viewFocusY * factor;
                listView.requestLayout();
            }
        }

        if (listView.getPageListViewListener().isShowZoomingMsg()) {
            toast.setText((int) Math.round(zoom * 100) + "%");
            toast.show();
        }
        return true;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (eventPointerCount <= 1 || !listView.getPageListViewListener().isTouchZoom()) {
            return true;
        }
        isScaling = true;
        mXScroll = mYScroll = 0;
        isProcessOnScroll = false;
        return true;
    }


    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (eventPointerCount <= 1 || !listView.getPageListViewListener().isTouchZoom()) {
            return;
        }
        isScaling = false;
    }


    public void onShowPress(MotionEvent e) {
        listView.getPageListViewListener().onEventMethod(listView, e, null, -1.0f, -1.0f, IPageListViewListener.ON_SHOW_PRESS);
    }


    public boolean onSingleTapUp(MotionEvent e) {
        listView.getPageListViewListener().onEventMethod(listView, e, null, -1.0f, -1.0f, IPageListViewListener.ON_SINGLE_TAP_UP);
        return false;
    }


    public void onLongPress(MotionEvent e) {
        listView.getPageListViewListener().onEventMethod(listView, e, null, -1.0f, -1.0f, IPageListViewListener.ON_LONG_PRESS);
    }


    public void onClick(View v) {
        listView.getPageListViewListener().onEventMethod(listView, null, null, -1.0f, -1.0f, IPageListViewListener.ON_CLICK);
    }


    public boolean onSingleTapConfirmed(MotionEvent e) {
        listView.getPageListViewListener().onEventMethod(listView, e, null, -1.0f, -1.0f, IPageListViewListener.ON_SINGLE_TAP_CONFIRMED);
        return false;
    }


    public boolean onDoubleTap(MotionEvent e) {
        isProcessOnScroll = true;
        isTouchEventIn = false;
        isDoubleTap = true;
        listView.getPageListViewListener().onEventMethod(listView, e, null, -1.0f, -1.0f, IPageListViewListener.ON_DOUBLE_TAP);
        return false;
    }


    public boolean onDoubleTapEvent(MotionEvent e) {
        isTouchEventIn = false;
        isDoubleTap = true;
        listView.getPageListViewListener().onEventMethod(listView, e, null, -1.0f, -1.0f, IPageListViewListener.ON_DOUBLE_TAP_EVENT);
        return false;
    }


    public void run() {
        if (!mScroller.isFinished()) {
            listView.getPageListViewListener().setDrawPictrue(false);
            mScroller.computeScrollOffset();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            mXScroll += x - mScrollerLastX;
            mYScroll += y - mScrollerLastY;
            mScrollerLastX = x;
            mScrollerLastY = y;
            listView.requestLayout();
            listView.post(this);
        } else if (!isTouchEventIn) {


            listView.postRepaint(listView.getCurrentPageView());
            listView.getPageListViewListener().updateStutus(null);
            listView.getPageListViewListener().setDrawPictrue(true);
        }
    }


    protected void slideViewOntoScreen(APageListItem pageItem) {
        Point corr = listView.getCorrection(listView.getScrollBounds(pageItem));
        if (corr.x != 0 || corr.y != 0) {
            mScrollerLastX = mScrollerLastY = 0;
            mScroller.startScroll(0, 0, corr.x, corr.y, 400);
            listView.post(this);
        }
        listView.getPageListViewListener().resetSearchResult(pageItem);
    }


    protected int getScrollX() {
        return mXScroll;
    }


    protected int getScrollY() {
        return mYScroll;
    }


    protected void setScrollAxisValue(int x, int y) {
        mXScroll = x;
        mYScroll = y;
    }


    protected boolean isTouchEventIn() {
        return this.isTouchEventIn;
    }


    protected boolean isScrollerFinished() {
        return mScroller.isFinished();
    }


    protected boolean isOnFling() {
        return this.isOnFling;
    }


    protected int directionOfTravel(float vx, float vy) {
        if (Math.abs(vx) > 2 * Math.abs(vy)) {
            return (vx > 0) ? MOVING_RIGHT : MOVING_LEFT;
        } else if (Math.abs(vy) > 2 * Math.abs(vx)) {
            return (vy > 0) ? MOVING_DOWN : MOVING_UP;
        } else {
            return MOVING_DIAGONALLY;
        }
    }


    protected boolean withinBoundsInDirectionOfTravel(Rect bounds, float vx, float vy) {
        switch (directionOfTravel(vx, vy)) {
            case MOVING_DIAGONALLY:
                return bounds.contains(0, 0);
            case MOVING_LEFT:
                return bounds.left <= 0;
            case MOVING_RIGHT:
                return bounds.right >= 0;
            case MOVING_UP:
                return bounds.top <= 0;
            case MOVING_DOWN:
                return bounds.bottom >= 0;
            default:
                throw new NoSuchElementException();
        }
    }


    public void dispose() {

    }
}
