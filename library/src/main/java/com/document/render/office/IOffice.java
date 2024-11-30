package com.document.render.office;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.document.render.office.common.IOfficeToPicture;
import com.document.render.office.constant.EventConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.res.ResKit;
import com.document.render.office.system.IMainFrame;
import com.document.render.office.system.MainControl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;



public abstract class IOffice implements IMainFrame {
    private MainControl control;
    private boolean writeLog = true;

    private Object bg = Color.LTGRAY;
    private String tempFilePath;

    public IOffice() {
        initControl();
    }

    private void initControl() {
        control = new MainControl(this);
        control.setOffictToPicture(new IOfficeToPicture() {
            private Bitmap bitmap;

            public Bitmap getBitmap(int componentWidth, int componentHeight) {
                if (componentWidth == 0 || componentHeight == 0) {
                    return null;
                }
                if (bitmap == null
                        || bitmap.getWidth() != componentWidth
                        || bitmap.getHeight() != componentHeight) {

                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    bitmap = Bitmap.createBitmap((int) (componentWidth), (int) (componentHeight), Bitmap.Config.ARGB_8888);
                }
                return bitmap;
            }

            public void callBack(Bitmap bitmap) {
                saveBitmapToFile(bitmap);
            }

            @Override
            public byte getModeType() {
                return VIEW_CHANGE_END;
            }

            @Override
            public void setModeType(byte modeType) {
            }

            @Override
            public boolean isZoom() {
                return false;
            }

            @Override
            public void dispose() {
            }
        });
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        if (tempFilePath == null) {





            tempFilePath = "/data/data/" + AppUtils.getAppPackageName() + "/cache";
            File file = new File(tempFilePath + File.separatorChar + "tempPic");
            if (!file.exists()) {
                file.mkdir();
            }
            tempFilePath = file.getAbsolutePath();
        }
        File file = new File(tempFilePath + File.separatorChar + "export_image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public View getView() {
        return control.getView();
    }

    public void openFile(String filepath, int docSourceType, String fileType) {
        getControl().openFile(filepath, docSourceType, fileType);
    }


    public boolean isShowZoomingMsg() {
        return true;
    }


    public boolean isPopUpErrorDlg() {
        return true;
    }

    @Override
    public abstract Activity getActivity();


    public boolean doActionEvent(int actionID, Object obj) {
        try {
            switch (actionID) {
                case EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS:
                    updateToolsbarStatus();
                    break;
                case EventConstant.APP_FINDING:
                    String content = ((String) obj).trim();
                    if (content.length() > 0 && control.getFind().find(content)) {
                        setFindBackForwardState(true);
                    }
                    break;
                case EventConstant.SS_CHANGE_SHEET:
                    break;
                default:
                    return false;
            }
        } catch (Exception e) {
            control.getSysKit().getErrorKit().writerLog(e);
        }
        return true;
    }

    @Override
    public void updateToolsbarStatus() {
    }

    @Override
    public void setFindBackForwardState(boolean state) {
    }


    public int getBottomBarHeight() {
        return 0;
    }


    public int getTopBarHeight() {
        return 0;
    }

    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float xValue,
                                 float yValue, byte eventMethodType) {
        return false;
    }

    public void changePage() {
    }

    public MainControl getControl() {
        return this.control;
    }


    public abstract String getAppName();

    public abstract int getMovingOrientation();


    public boolean isDrawPageNumber() {
        return true;
    }


    public boolean isTouchZoom() {
        return true;
    }


    public byte getWordDefaultView() {
        return WPViewConstant.PAGE_ROOT;
    }


    public boolean isZoomAfterLayoutForWord() {
        return true;
    }



    public void changeZoom() {
    }


    public void error(int errorCode) {
    }

    @Override
    public void showProgressBar(boolean visible) {
    }

    @Override
    public void updateViewImages(List<Integer> viewList) {
    }


    public void destroyEngine() {
    }


    public String getLocalString(String resName) {
        return ResKit.instance().getLocalString(resName);
    }

    @Override
    public boolean isShowPasswordDlg() {
        return true;
    }

    @Override
    public boolean isShowProgressBar() {
        return true;
    }

    @Override
    public boolean isShowFindDlg() {
        return true;
    }

    @Override
    public boolean isShowTXTEncodeDlg() {
        return true;
    }


    public String getTXTDefaultEncode() {
        return "GBK";
    }

    @Override
    public void completeLayout() {
    }

    @Override
    public boolean isChangePage() {

        return true;
    }


    public boolean isWriteLog() {
        return writeLog;
    }


    public void setWriteLog(boolean saveLog) {
        this.writeLog = saveLog;
    }

    @Override
    public boolean isThumbnail() {
        return false;
    }

    @Override
    public void setThumbnail(boolean isThumbnail) {
    }


    public Object getViewBackground() {
        return bg;
    }


    public boolean isIgnoreOriginalSize() {
        return false;
    }


    public void setIgnoreOriginalSize(boolean ignoreOriginalSize) {
    }

    public int getPageListViewMovingPosition() {
        return getMovingOrientation();
    }


    public abstract File getTemporaryDirectory();


    public void dispose() {
        if (control != null) {
            control.dispose();
            control = null;
        }
    }
}