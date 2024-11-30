
package com.document.render.office.common;

import android.graphics.Bitmap;


public interface IOfficeToPicture {

    public static final String INSTANCE_CLASS_PATH = "com.document.render.office.officereader.OfficeToPicture";

    public static final byte VIEW_CHANGING = 0;

    public static final byte VIEW_CHANGE_END = VIEW_CHANGING + 1;


    public byte getModeType();


    public void setModeType(byte modeType);


    public Bitmap getBitmap(int visibleWidth, int visibleHeight);


    public void callBack(Bitmap bitmap);


    public boolean isZoom();


    public void dispose();
}
