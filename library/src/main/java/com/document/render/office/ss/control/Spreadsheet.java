

package com.document.render.office.ss.control;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Message;
import android.widget.LinearLayout;

import com.document.render.office.common.IOfficeToPicture;
import com.document.render.office.common.hyperlink.Hyperlink;
import com.document.render.office.common.picture.PictureKit;
import com.document.render.office.constant.EventConstant;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.interfacePart.IReaderListener;
import com.document.render.office.ss.util.ModelUtil;
import com.document.render.office.ss.view.SheetView;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IFind;
import com.document.render.office.system.ReaderHandler;
import com.document.render.office.system.beans.AEventManage;
import com.document.render.office.system.beans.CalloutView.CalloutView;
import com.document.render.office.system.beans.CalloutView.IExportListener;

import java.io.File;


public class Spreadsheet extends LinearLayout implements IFind, IReaderListener, IExportListener {

    private ExcelView parent;

    private boolean isConfigurationChanged;

    private boolean isDefaultSheetBar = true;

    private boolean abortDrawing;

    private boolean initFinish;

    private int preShowSheetIndex = -1;

    private int currentSheetIndex;
    private String currentSheetName;

    private int sheetbarHeight;

    private String fileName;

    private IControl control;

    private Workbook workbook;

    private SheetView sheetview;

    private SSEventManage eventManage;

    private SSEditor editor;


    private CalloutView callouts;


    public Spreadsheet(Context context, String filepath, Workbook book, IControl control, ExcelView parent) {
        super(context);
        this.parent = parent;
        fileName = filepath;
        setBackgroundColor(Color.WHITE);
        this.workbook = book;
        this.control = control;
        eventManage = new SSEventManage(this, control);
        this.editor = new SSEditor(this);
        setOnTouchListener(eventManage);
        setLongClickable(true);
    }

    public CalloutView getCalloutView() {
        return callouts;
    }

    public void initCalloutView() {
        if (callouts == null) {
            callouts = new CalloutView(this.getContext(), control, this);
            callouts.setIndex(currentSheetIndex);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.leftMargin = SSConstant.DEFAULT_ROW_HEADER_WIDTH;
            params.topMargin = SSConstant.DEFAULT_COLUMN_HEADER_HEIGHT;
            addView(callouts, params);
        }
    }

    @Override
    public void exportImage() {
        control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
    }


    public void init() {


        int index = fileName.lastIndexOf(File.separator);
        if (index > 0) {
            fileName = fileName.substring(index + 1);
        }

        control.actionEvent(EventConstant.SS_SHEET_CHANGE,
                fileName + " : " + workbook.getSheet(0).getSheetName());
        if (sheetview == null) {
            sheetview = new SheetView(this, workbook.getSheet(0));
        }

        initFinish = true;
        short state = workbook.getSheet(0).getState();
        if (state != Sheet.State_Accomplished) {
            workbook.getSheet(0).setReaderListener(this);
            control.actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, true);
        }

        {

            post(new Runnable() {
                @Override
                public void run() {
                    control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                }
            });
        }
    }


    private void initSheetbar() {

    }


    public void removeSheetBar() {
        isDefaultSheetBar = false;

    }


    public int getSheetCount() {
        return workbook.getSheetCount();
    }


    public void showSheet(String sheetName) {
        if (currentSheetName != null && currentSheetName.equals(sheetName)) {
            return;
        }
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return;
        }
        currentSheetName = sheetName;
        currentSheetIndex = workbook.getSheetIndex(sheet);


        showSheet(sheet);
    }


    public void showSheet(int sheetIndex) {
        if (currentSheetIndex == sheetIndex
                || sheetIndex >= getSheetCount()) {
            return;
        }
        Sheet sheet = workbook.getSheet(sheetIndex);
        currentSheetIndex = sheetIndex;
        currentSheetName = sheet.getSheetName();


        control.actionEvent(EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS, null);
        if (callouts != null) {
            callouts.setIndex(currentSheetIndex);
        }
        showSheet(sheet);
    }


    private void showSheet(Sheet sheet) {
        try {
            eventManage.stopFling();
            control.getMainFrame().setFindBackForwardState(false);
            control.actionEvent(EventConstant.SS_SHEET_CHANGE,
                    fileName + " : " + sheet.getSheetName());
            sheetview.changeSheet(sheet);
            postInvalidate();
            if (sheet.getState() != Sheet.State_Accomplished) {

                sheet.setReaderListener(this);
                control.actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, true);
                control.actionEvent(EventConstant.APP_ABORTREADING, null);
            } else {
                control.actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, false);
            }

            ReaderHandler readerHandler = workbook.getReaderHandler();
            if (readerHandler != null) {
                Message msg = new Message();
                msg.what = MainConstant.HANDLER_MESSAGE_SUCCESS;
                msg.obj = currentSheetIndex;
                readerHandler.handleMessage(msg);
            }
        } catch (Exception e) {
            control.getSysKit().getErrorKit().writerLog(e);
        }
    }


    protected void onDraw(Canvas canvas) {
        if (!initFinish) {
            return;
        }
        try {
            sheetview.drawSheet(canvas);

            if (control.isAutoTest()) {
                if (currentSheetIndex < workbook.getSheetCount() - 1) {
                    try {
                        while (sheetview.getCurrentSheet().getState() != Sheet.State_Accomplished) {
                            Thread.sleep(50);
                        }
                    } catch (Exception e) {
                    }
                    showSheet(currentSheetIndex + 1);
                } else {
                    control.actionEvent(EventConstant.SYS_AUTO_TEST_FINISH_ID, true);
                }
            } else {
                IOfficeToPicture otp = control.getOfficeToPicture();
                if (otp != null && otp.getModeType() == IOfficeToPicture.VIEW_CHANGING) {
                    toPicture(otp);
                }
            }
            if (sheetview.getCurrentSheet().getState() != Sheet.State_Accomplished) {
                invalidate();
            }
            if (preShowSheetIndex != currentSheetIndex) {
                control.getMainFrame().changePage();
                preShowSheetIndex = currentSheetIndex;
            }
        } catch (Exception e) {
            control.getSysKit().getErrorKit().writerLog(e);
        }
    }


    public void createPicture() {
        IOfficeToPicture otp = control.getOfficeToPicture();
        if (otp != null && otp.getModeType() == IOfficeToPicture.VIEW_CHANGE_END) {
            try {
                toPicture(otp);
            } catch (Exception e) {
            }
        }
    }


    private void toPicture(IOfficeToPicture otp) {
        boolean b = PictureKit.instance().isDrawPictrue();
        PictureKit.instance().setDrawPictrue(true);

        Bitmap bitmap = otp.getBitmap(getWidth(), getHeight());
        if (bitmap == null) {
            return;
        }
        Canvas picCanvas = new Canvas(bitmap);
        float oldPaintZoom = sheetview.getZoom();
        if (bitmap.getWidth() != getWidth() || bitmap.getHeight() != getHeight()) {
            float zoom = Math.min((float) bitmap.getWidth() / getWidth(), (float) bitmap.getHeight() / getHeight()) * oldPaintZoom;
            sheetview.setZoom(zoom, true);
        }
        picCanvas.drawColor(Color.WHITE);
        sheetview.drawSheet(picCanvas);
        control.getSysKit().getCalloutManager().drawPath(picCanvas, currentSheetIndex, oldPaintZoom);
        otp.callBack(bitmap);
        sheetview.setZoom(oldPaintZoom, true);

        PictureKit.instance().setDrawPictrue(b);
    }


    public Bitmap getSnapshot(Bitmap destBitmap) {
        if (destBitmap == null) {
            return null;
        }
        synchronized (sheetview) {
            Canvas picCanvas = new Canvas(destBitmap);
            float oldPaintZoom = sheetview.getZoom();
            if (destBitmap.getWidth() != getWidth() || destBitmap.getHeight() != getHeight()) {
                float zoom = Math.min((float) destBitmap.getWidth() / getWidth(), (float) destBitmap.getHeight() / getHeight()) * oldPaintZoom;
                sheetview.setZoom(zoom, true);
            }
            picCanvas.drawColor(Color.WHITE);
            sheetview.drawSheet(picCanvas);
            sheetview.setZoom(oldPaintZoom, true);
            return destBitmap;
        }
    }
















    public Bitmap getThumbnail(int width, int height, float zoomValue) {
        Sheet sheet = workbook.getSheet(0);
        if (sheet == null || sheet.getState() != Sheet.State_Accomplished) {
            return null;
        }
        if (sheetview == null) {
            sheetview = new SheetView(this, workbook.getSheet(0));
        }
        return sheetview.getThumbnail(sheet, width, height, zoomValue);
    }


    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isConfigurationChanged = true;
    }


    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (isConfigurationChanged) {
            isConfigurationChanged = false;

            post(new Runnable() {
                public void run() {
                    control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                }
            });
        }
    }


    public void computeScroll() {
        eventManage.computeScroll();
    }


    public IControl getControl() {
        return control;
    }


    public SheetView getSheetView() {
        return sheetview;
    }


    public Workbook getWorkbook() {
        return workbook;
    }


    public String getActiveCellContent() {
        if (sheetview.getCurrentSheet().getActiveCell() != null) {
            return ModelUtil.instance().getFormatContents(
                    workbook, sheetview.getCurrentSheet().getActiveCell());
        }
        return "";
    }


    public Hyperlink getActiveCellHyperlink() {
        Cell cell = sheetview.getCurrentSheet().getActiveCell();
        if (cell != null && cell.getHyperLink() != null) {
            return cell.getHyperLink();
        }
        return null;
    }


    public boolean find(String value) {
        return sheetview.find(value);
    }

    public boolean findBackward() {
        return sheetview.findBackward();
    }

    public boolean findForward() {
        return sheetview.findForward();
    }


    public void resetSearchResult() {
    }


    public int getPageIndex() {
        return -1;
    }


    public float getZoom() {
        if (sheetview == null) {
            sheetview = new SheetView(this, workbook.getSheet(0));
        }
        return sheetview.getZoom();
    }


    public void setZoom(float zoom) {
        if (sheetview == null) {
            sheetview = new SheetView(this, workbook.getSheet(0));
        }
        sheetview.setZoom(zoom);
    }


    public float getFitZoom() {
        return 0.5f;
    }


    public AEventManage getEventManage() {
        return this.eventManage;
    }


    public void OnReadingFinished() {
        if (control != null && control.getMainFrame().getActivity() != null) {
            post(new Runnable() {
                public void run() {
                    Sheet sheet = workbook.getSheet(currentSheetIndex);
                    control.actionEvent(EventConstant.SS_SHEET_CHANGE,
                            fileName + " : " + sheet.getSheetName());
                    control.actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, false);
                    control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                    postInvalidate();
                }
            });
        }
    }

    public String getFileName() {
        return fileName;
    }


    public void abortDrawing() {
        abortDrawing = true;
    }


    public void startDrawing() {
        abortDrawing = false;
    }


    public boolean isAbortDrawing() {
        return abortDrawing;
    }


    public int getCurrentSheetNumber() {
        return this.currentSheetIndex + 1;
    }


    public int getBottomBarHeight() {
        return parent.getBottomBarHeight();
    }


    public IWord getEditor() {
        return this.editor;
    }


    public void dispose() {
        parent = null;
        fileName = null;
        control = null;
        workbook = null;
        if (sheetview != null) {
            sheetview.dispose();
            sheetview = null;
        }
        if (eventManage != null) {
            eventManage.dispose();
            eventManage = null;
        }
        if (editor != null) {
            editor.dispose();
            editor = null;
        }

    }



}
