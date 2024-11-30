
package com.document.render.office.system;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.util.List;


public interface IMainFrame {

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


    public Activity getActivity();


    public boolean doActionEvent(int actionID, Object obj);


    public void openFileFinish();

    public void openFileFailed();


    public void updateToolsbarStatus();


    public void setFindBackForwardState(boolean state);


    public int getBottomBarHeight();


    public int getTopBarHeight();


    public String getAppName();


    public File getTemporaryDirectory();


    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float xValue, float yValue, byte eventMethodType);



    public boolean isDrawPageNumber();


    public boolean isShowZoomingMsg();


    public boolean isPopUpErrorDlg();


    public boolean isShowPasswordDlg();


    public boolean isShowProgressBar();


    public boolean isShowFindDlg();


    public boolean isShowTXTEncodeDlg();


    public String getTXTDefaultEncode();



    public boolean isTouchZoom();


    public boolean isZoomAfterLayoutForWord();


    public byte getWordDefaultView();


    public String getLocalString(String resName);


    public void changeZoom();


    public void changePage();


    public void completeLayout();


    public void error(int errorCode);


    public void fullScreen(boolean fullscreen);


    public void showProgressBar(boolean visible);


    public void updateViewImages(List<Integer> viewList);



    public boolean isChangePage();





    public boolean isWriteLog();


    public void setWriteLog(boolean saveLog);


    public boolean isThumbnail();


    public void setThumbnail(boolean isThumbnail);


    public Object getViewBackground();


    public boolean isIgnoreOriginalSize();


    public void setIgnoreOriginalSize(boolean ignoreOriginalSize);


    public int getPageListViewMovingPosition();


    public void dispose();
}
