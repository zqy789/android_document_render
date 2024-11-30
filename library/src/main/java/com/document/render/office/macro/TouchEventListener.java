package com.document.render.office.macro;

import android.view.MotionEvent;
import android.view.View;

import com.document.render.office.system.IMainFrame;

public interface TouchEventListener {

    public final static byte EVENT_TOUCH = IMainFrame.ON_TOUCH;

    public final static byte EVENT_DOWN = IMainFrame.ON_DOWN;

    public final static byte EVENT_SHOW_PRESS = IMainFrame.ON_SHOW_PRESS;

    public final static byte EVENT_SINGLE_TAP_UP = IMainFrame.ON_SINGLE_TAP_UP;

    public final static byte EVENT_SCROLL = IMainFrame.ON_SCROLL;

    public final static byte EVENT_LONG_PRESS = IMainFrame.ON_LONG_PRESS;

    public final static byte EVENT_FLING = IMainFrame.ON_FLING;

    public final static byte EVENT_SINGLE_TAP_CONFIRMED = IMainFrame.ON_SINGLE_TAP_CONFIRMED;

    public final static byte EVENT_DOUBLE_TAP = IMainFrame.ON_DOUBLE_TAP;

    public final static byte EVENT_DOUBLE_TAP_EVENT = IMainFrame.ON_DOUBLE_TAP_EVENT;

    public final static byte EVENT_CLICK = IMainFrame.ON_CLICK;


    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY, byte eventMethodType);
}
