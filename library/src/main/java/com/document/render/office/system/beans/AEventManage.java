
package com.document.render.office.system.beans;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import android.widget.Toast;

import com.document.render.office.constant.EventConstant;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IMainFrame;


public abstract class AEventManage implements OnTouchListener,
        OnGestureListener, OnDoubleTapListener, OnClickListener {

    protected boolean isFling;

    protected boolean isScroll;

    protected boolean singleTabup = false;

    protected boolean zoomChange;

    protected int mMinimumVelocity;

    protected int mMaximumVelocity;

    protected int midXDoublePoint;

    protected int midYDoublePoint;

    protected int mActivePointerId = -1;

    protected float distance = 0;

    protected IControl control;

    protected GestureDetector gesture;

    protected VelocityTracker mVelocityTracker;

    protected Scroller mScroller;

    protected Toast toast = null;


    public AEventManage(Context context, IControl control) {
        this.control = control;
        gesture = new GestureDetector(context, this, null, true);
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }


    public boolean onTouch(View v, MotionEvent event) {
        boolean ret = false;
        try {
            if (gesture == null) {
                return false;
            }
            control.getMainFrame().onEventMethod(v, event, null, -1.0f, -1.0f, IMainFrame.ON_TOUCH);
            if (event.getPointerCount() == 2) {

                return zoom(event);
            }


            gesture.onTouchEvent(event);
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
            mVelocityTracker.addMovement(event);
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    stopFling();
                    mActivePointerId = event.getPointerId(0);
                    break;

                case MotionEvent.ACTION_MOVE:
                    break;

                case MotionEvent.ACTION_UP:
                    if (!singleTabup) {
                        final VelocityTracker velocityTracker = mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        int initialYVelocity = (int) velocityTracker.getYVelocity(mActivePointerId);
                        int initialXVelocity = (int) velocityTracker.getXVelocity(mActivePointerId);
                        if (Math.abs(initialYVelocity) > mMinimumVelocity
                                || Math.abs(initialXVelocity) > mMinimumVelocity) {
                            if (!isScroll) {
                                isScroll = control.getApplicationType() == MainConstant.APPLICATION_TYPE_PPT;
                            }

                            if (!zoomChange) {
                                fling(-initialXVelocity, -initialYVelocity);
                            }
                            ret = true;
                        }
                        midXDoublePoint = -1;
                        midYDoublePoint = -1;
                        mActivePointerId = -1;
                        if (mVelocityTracker != null) {
                            mVelocityTracker.recycle();
                            mVelocityTracker = null;
                        }
                        toast.cancel();

                        if (isScroll) {
                            isScroll = false;
                            if (control.getApplicationType() == MainConstant.APPLICATION_TYPE_WP && zoomChange) {
                                if (!control.getMainFrame().isZoomAfterLayoutForWord()) {
                                    control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                                }
                            }
                            if (control.getApplicationType() == MainConstant.APPLICATION_TYPE_PPT) {
                                if (!control.isSlideShow()) {
                                    control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                                }
                            } else {
                                control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                            }

                            control.actionEvent(EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS, null);
                        }
                        if (control.getApplicationType() != MainConstant.APPLICATION_TYPE_WP) {
                            zoomChange = false;
                        }
                    }

                    singleTabup = false;

                    break;
                case MotionEvent.ACTION_CANCEL:
                    mActivePointerId = -1;
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                default:
                    break;
            }
        } catch (Exception e) {
            control.getSysKit().getErrorKit().writerLog(e);
        }
        return ret;
    }


    protected boolean zoom(MotionEvent event) {
        if (!control.getMainFrame().isTouchZoom()) {
            return true;
        }
        float zoom = ((Float) control.getActionValue(EventConstant.APP_ZOOM_ID, null)).floatValue();
        float fitZoom = ((Float) control.getActionValue(EventConstant.APP_FIT_ZOOM_ID, null)).floatValue();
        boolean isMinZoom = (int) (zoom * MainConstant.STANDARD_RATE) == (int) (fitZoom * MainConstant.STANDARD_RATE);
        boolean zoomRateChanged = false;
        float dist = distance;
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_POINTER_1_DOWN:
                float x1 = event.getX(0);
                float y1 = event.getY(0);
                float x2 = event.getX(1);
                float y2 = event.getY(1);

                float min = Math.min(x1, x2);
                midXDoublePoint = (int) (min + Math.abs(x1 - x2) / 2);
                min = Math.min(y1, y2);
                midYDoublePoint = (int) (min + Math.abs(y1 - y2) / 2);

                distance = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)) / 2);
                break;
            case MotionEvent.ACTION_POINTER_1_UP:

                break;

            case MotionEvent.ACTION_MOVE:
                float tx1 = event.getX(0);
                float ty1 = event.getY(0);
                float tx2 = event.getX(1);
                float ty2 = event.getY(1);
                dist = (float) (Math.sqrt((tx1 - tx2) * (tx1 - tx2) + (ty1 - ty2) * (ty1 - ty2)) / 2);
                if (Math.abs(distance - dist) > 8) {
                    boolean increased = dist > distance;
                    if (Math.abs(zoom - fitZoom) < 0.01 && !increased && isMinZoom) {

                        zoomRateChanged = false;
                    } else if (Math.abs(zoom - 3.0f) < 0.001 && increased) {

                        zoomRateChanged = false;
                    } else {
                        zoom = increased ? zoom + 0.1f : zoom - 0.1f;

                        zoomRateChanged = true;
                        if (zoom > 3.0f) {
                            zoom = 3.0f;
                        } else if (zoom < fitZoom) {
                            zoom = fitZoom;
                            zoomRateChanged = false;
                        }


                        if (increased && isMinZoom) {
                            zoom = ((int) (zoom * 10)) / 10.f;
                        }

                    }
                    distance = zoomRateChanged ? dist : distance;
                }
                break;
            default:
                break;
        }


        if (zoomRateChanged) {
            isScroll = true;
            zoomChange = true;
            control.actionEvent(EventConstant.APP_ZOOM_ID, new int[]{(int) (zoom * MainConstant.STANDARD_RATE), midXDoublePoint, midYDoublePoint});
            control.getView().postInvalidate();

            if (control.getMainFrame().isShowZoomingMsg()) {
                if (control.getApplicationType() == MainConstant.APPLICATION_TYPE_PPT
                        && control.isSlideShow()) {
                    return true;
                }

                toast.setText((int) Math.round(zoom * 100) + "%");
                toast.show();
            }
        }
        return true;
    }


    public boolean onDown(MotionEvent e) {
        return control.getMainFrame().onEventMethod(control.getView(), e, null, -1.0f, -1.0f, IMainFrame.ON_DOWN);
    }


    public void onShowPress(MotionEvent e) {
        control.getMainFrame().onEventMethod(control.getView(), e, null, -1.0f, -1.0f, IMainFrame.ON_SHOW_PRESS);
    }


    public boolean onSingleTapUp(MotionEvent e) {
        if (!isScroll) {
            singleTabup = true;
        }
        return control.getMainFrame().onEventMethod(control.getView(), e, null, -1.0f, -1.0f, IMainFrame.ON_SINGLE_TAP_UP);
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        isScroll = true;
        return control.getMainFrame().onEventMethod(control.getView(), e1, e2, distanceX, distanceY, IMainFrame.ON_SCROLL);
    }


    public void onLongPress(MotionEvent e) {
        control.getMainFrame().onEventMethod(control.getView(), e, null, -1.0f, -1.0f, IMainFrame.ON_LONG_PRESS);
    }


    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return control.getMainFrame().onEventMethod(control.getView(), e1, e2, velocityX, velocityY, IMainFrame.ON_FLING);
    }


    public boolean onSingleTapConfirmed(MotionEvent e) {
        return control.getMainFrame().onEventMethod(control.getView(), e, null, -1.0f, -1.0f, IMainFrame.ON_SINGLE_TAP_CONFIRMED);
    }


    public boolean onDoubleTap(MotionEvent e) {
        return control.getMainFrame().onEventMethod(control.getView(), e, null, -1.0f, -1.0f, IMainFrame.ON_DOUBLE_TAP);
    }


    public boolean onDoubleTapEvent(MotionEvent e) {
        return control.getMainFrame().onEventMethod(control.getView(), e, null, -1.0f, -1.0f, IMainFrame.ON_DOUBLE_TAP_EVENT);
    }


    public void onClick(View v) {
        control.getMainFrame().onEventMethod(control.getView(), null, null, -1.0f, -1.0f, IMainFrame.ON_CLICK);
    }


    public void computeScroll() {
        if (isFling && mScroller.isFinished()) {
            isFling = false;
            control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
            control.actionEvent(EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS, null);
        }
    }


    public void fling(int velocityX, int velocityY) {
    }


    public void stopFling() {
        if (mScroller != null && !mScroller.isFinished()) {
            isFling = true;
            mScroller.abortAnimation();
        }
    }


    public int getMiddleXOfDoublePoint() {
        return this.midXDoublePoint;
    }


    public int getMiddleYOfDoublePoint() {
        return this.midYDoublePoint;
    }


    public void dispose() {
        control = null;
        gesture = null;
        mVelocityTracker = null;
        toast = null;
        if (mScroller != null && !mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
        mScroller = null;
    }

}
