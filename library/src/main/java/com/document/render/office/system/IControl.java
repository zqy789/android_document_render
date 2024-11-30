
package com.document.render.office.system;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import com.document.render.office.common.ICustomDialog;
import com.document.render.office.common.IOfficeToPicture;
import com.document.render.office.common.ISlideShow;

import org.jetbrains.annotations.Nullable;

public interface IControl {

    public void layoutView(int x, int y, int w, int h);


    public void actionEvent(int actionID, @Nullable Object obj);


    public @Nullable Object getActionValue(int actionID, @Nullable Object obj);


    public int getCurrentViewIndex();


    public View getView();


    public Dialog getDialog(Activity activity, int id);


    public IMainFrame getMainFrame();


    public Activity getActivity();


    public IFind getFind();


    public boolean isAutoTest();


    public IOfficeToPicture getOfficeToPicture();


    public ICustomDialog getCustomDialog();


    public boolean isSlideShow();


    public ISlideShow getSlideShow();


    public IReader getReader();


    public boolean openFile(String filePath);


    public int getApplicationType();


    public SysKit getSysKit();


    public void dispose();
}
