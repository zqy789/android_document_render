
package com.document.render.office.system.beans.pagelist;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.document.render.office.macro.TouchEventListener;

public interface IPageListViewListener {

    public final static byte ON_TOUCH = 0;

    public final static byte ON_DOWN = 1;

    public final static byte ON_SHOW_PRESS = 2;

    public final static byte ON_SINGLE_TAP_UP = 3;

    public final static byte ON_SCROLL = 4;

    public final static byte ON_LONG_PRESS = 5;

    public final static byte ON_FLING = 6;

    public final static byte ON_SINGLE_TAP_CONFIRMED = 7;

    public final static byte ON_DOUBLE_TAP = 8;

    public final static byte ON_DOUBLE_TAP_EVENT = 9;

    public final static byte ON_CLICK = 10;


    public final static byte Moving_Horizontal = 0;
    public final static byte Moving_Vertical = 1;


    public Object getModel();


    public APageListItem getPageListItem(int position, View convertView, ViewGroup parent);


    public void exportImage(final APageListItem view, final Bitmap srcBitmap);


    public int getPageCount();


    public Rect getPageSize(int pageIndex);


    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY, byte eventMethodType);


    public void updateStutus(Object obj);


    public void resetSearchResult(APageListItem pageItem);



    public boolean isTouchZoom();


    public boolean isShowZoomingMsg();


    public void changeZoom();


    public boolean isChangePage();



    public void setDrawPictrue(boolean isDrawPictrue);


    public boolean isInit();


    public boolean isIgnoreOriginalSize();


    public int getPageListViewMovingPosition();
}
