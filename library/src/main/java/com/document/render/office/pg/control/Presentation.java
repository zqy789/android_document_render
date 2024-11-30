
package com.document.render.office.pg.control;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;

import com.document.render.office.common.IOfficeToPicture;
import com.document.render.office.common.ISlideShow;
import com.document.render.office.common.picture.PictureKit;
import com.document.render.office.constant.EventConstant;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.pg.animate.ShapeAnimation;
import com.document.render.office.pg.model.PGModel;
import com.document.render.office.pg.model.PGNotes;
import com.document.render.office.pg.model.PGSlide;
import com.document.render.office.pg.view.SlideDrawKit;
import com.document.render.office.pg.view.SlideShowView;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IFind;
import com.document.render.office.system.SysKit;
import com.document.render.office.system.beans.CalloutView.CalloutView;
import com.document.render.office.system.beans.CalloutView.IExportListener;

import java.util.List;


public class Presentation extends FrameLayout implements IFind, IExportListener {

    private boolean isConfigurationChanged;

    private boolean init;

    private int preShowSlideIndex = -1;

    private int currentIndex = -1;

    private int mWidth;

    private int mHeight;

    private float zoom = 1F;

    private PGFind pgFind;



    private PGEditor editor;

    private IControl control;

    private PGSlide currentSlide;

    private PGModel pgModel;

    private SlideShowView slideView;

    private PGEventManage eventManage;

    private boolean slideshow;
    private int slideIndex_SlideShow;
    private float fitZoom = 1f;
    private Rect slideSize = null;

    private PGPrintMode pgPrintMode;
    private CalloutView callouts;


    public Presentation(Activity activity, PGModel pgModel, IControl control) {
        super(activity);
        this.control = control;
        this.pgModel = pgModel;
        setLongClickable(true);
        pgFind = new PGFind(this);

        editor = new PGEditor(this);

        pgPrintMode = new PGPrintMode(activity, control, pgModel, editor);

        addView(pgPrintMode);
    }

    public void initCalloutView() {
        if (slideshow) {
            if (callouts == null) {
                callouts = new CalloutView(this.getContext(), control, this);
                callouts.setIndex(slideIndex_SlideShow);
                addView(callouts);
            }
        } else {
            pgPrintMode.getListView().getCurrentPageView().initCalloutView();
        }
    }

    @Override
    public void exportImage() {
        if (slideshow) {
            createPicture();
        } else {
            pgPrintMode.exportImage(pgPrintMode.getListView().getCurrentPageView(), null);
        }
    }


    public void init() {

        init = true;
        initSlidebar();
        pgPrintMode.init();
    }


    public void initSlidebar() {













    }


    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        if (pgPrintMode != null) {
            pgPrintMode.setBackgroundColor(color);
        }
    }


    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
        if (pgPrintMode != null) {
            pgPrintMode.setBackgroundResource(resid);
        }
    }


    public void setBackgroundDrawable(Drawable d) {
        super.setBackgroundDrawable(d);
        if (pgPrintMode != null) {
            pgPrintMode.setBackgroundDrawable(d);
        }
    }

    public void setViewVisible(boolean visible) {
        pgPrintMode.setVisible(visible);
    }

    public boolean showLoadingSlide() {
        if (currentIndex < getRealSlideCount()) {
            post(new Runnable() {

                public void run() {
                    setViewVisible(true);
                }
            });
            pgPrintMode.showSlideForIndex(currentIndex);
            return true;
        }
        return false;
    }


    public void showSlide(int index, boolean find) {
        if (!find) {
            control.getMainFrame().setFindBackForwardState(false);
        }
        if (index >= pgModel.getSlideCount()) {
            return;
        }
        if (!slideshow) {
            currentIndex = index;
            if (index < getRealSlideCount()) {
                pgPrintMode.showSlideForIndex(index);
            } else {
                setViewVisible(false);
            }
        } else {
            int old = currentIndex;
            currentIndex = index;
            currentSlide = pgModel.getSlide(index);
            if (slideView == null) {
                slideView = new SlideShowView(this, currentSlide);
            }
            if (slideView != null) {
                slideView.changeSlide(currentSlide);
            }
            if (old != currentIndex) {
                control.actionEvent(EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS, null);


                SlideDrawKit.instance().disposeOldSlideView(pgModel, pgModel.getSlide(old));
            }
            postInvalidate();

            post(new Runnable() {

                @Override
                public void run() {
                    if (control != null) {
                        control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                    }
                }
            });
        }
    }


    protected void onDraw(Canvas canvas) {
        if (!init || !slideshow) {
            return;
        }
        try {
            slideView.drawSlide(canvas, fitZoom, callouts);

            if (control.isAutoTest()) {
                if (currentIndex < getRealSlideCount() - 1) {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                    showSlide(currentIndex + 1, false);
                } else {
                    control.actionEvent(EventConstant.SYS_AUTO_TEST_FINISH_ID, true);
                }
            }
            if (preShowSlideIndex != currentIndex) {
                control.getMainFrame().changePage();
                preShowSlideIndex = currentIndex;
            }
        } catch (NullPointerException ex) {
            control.getSysKit().getErrorKit().writerLog(ex);
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
        if (!init || !slideshow) {
            PGPageListItem item = (PGPageListItem) pgPrintMode.getListView().getCurrentPageView();
            item.addRepaintImageView(null);
        } else if (slideView.animationStoped()) {
            boolean b = PictureKit.instance().isDrawPictrue();
            PictureKit.instance().setDrawPictrue(true);

            float paintZoom = (slideshow ? fitZoom : zoom);
            Dimension d = getPageSize();
            int originBitmapW = Math.min((int) (d.width * paintZoom), getWidth());
            int originbitmapH = Math.min((int) (d.height * paintZoom), getHeight());
            Bitmap bitmap = otp.getBitmap(originBitmapW, originbitmapH);
            if (bitmap == null) {
                return;
            }
            Canvas picCanvas = new Canvas(bitmap);
            picCanvas.drawColor(Color.BLACK);
            slideView.drawSlideForToPicture(picCanvas, paintZoom, originBitmapW, originbitmapH);
            control.getSysKit().getCalloutManager().drawPath(picCanvas, getCurrentIndex(), paintZoom);
            otp.callBack(bitmap);
            PictureKit.instance().setDrawPictrue(b);
        }
    }


    public Bitmap getSnapshot(Bitmap destBitmap) {
        if (destBitmap == null) {
            return null;
        }
        if (!init || !slideshow) {
            return pgPrintMode.getSnapshot(destBitmap);
        } else {

            float paintZoom = (slideshow ? fitZoom : zoom);
            Dimension d = getPageSize();
            int originBitmapW = Math.min((int) (d.width * paintZoom), getWidth());
            int originbitmapH = Math.min((int) (d.height * paintZoom), getHeight());
            paintZoom *= Math.min((destBitmap.getWidth() / (float) originBitmapW), (destBitmap.getHeight() / (float) originbitmapH));
            Canvas picCanvas = new Canvas(destBitmap);
            picCanvas.drawColor(Color.BLACK);
            slideView.drawSlideForToPicture(picCanvas, paintZoom, destBitmap.getWidth(), destBitmap.getHeight());
        }
        return destBitmap;
    }


    public Bitmap slideToImage(int slideNumber) {
        if (slideNumber <= 0 || slideNumber > getRealSlideCount()) {
            return null;
        }
        return SlideDrawKit.instance().slideToImage(pgModel, editor, pgModel.getSlide(slideNumber - 1));
    }


    public Bitmap slideAreaToImage(int slideNumber, int srcLeft, int srcTop, int srcWidth, int srcHeight, int desWidth, int desHeight) {
        if (slideNumber <= 0 || slideNumber > getRealSlideCount() || !SysKit.isValidateRect((int) (getPageSize().getWidth()), (int) (getPageSize().getHeight()), srcLeft, srcTop, srcWidth, srcHeight)) {
            return null;
        }
        return SlideDrawKit.instance().slideAreaToImage(pgModel, editor, pgModel.getSlide(slideNumber - 1), srcLeft, srcTop, srcWidth, srcHeight, desWidth, desHeight);
    }


    public Bitmap getThumbnail(int slideNumber, float zoom) {
        if (slideNumber <= 0 || slideNumber > getRealSlideCount()) {
            return null;
        }
        return SlideDrawKit.instance().getThumbnail(pgModel, editor, pgModel.getSlide(slideNumber - 1), zoom);
    }


    public String getSldieNote(int slideNumber) {
        if (slideNumber <= 0 || slideNumber > getSlideCount()) {
            return null;
        }
        PGNotes note = pgModel.getSlide(slideNumber - 1).getNotes();
        return note == null ? "" : note.getNotes();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isConfigurationChanged = true;
    }


    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        processPageSize(w, h);
    }

    private void processPageSize(int width, int height) {
        mWidth = width;
        mHeight = height;

        if (isConfigurationChanged || slideshow) {
            if (isConfigurationChanged) {
                isConfigurationChanged = false;
            }


            fitZoom = getFitZoom();
            if (slideshow) {

                post(new Runnable() {
                    public void run() {
                        control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                    }
                });
            }
        }
    }


    public float getFitZoom() {
        if (slideshow) {
            Dimension pageSize = getPageSize();
            return Math.min((float) (mWidth) / pageSize.width, (float) (mHeight) / pageSize.height);
        }
        return pgPrintMode.getFitZoom();
    }


    public int getCurrentIndex() {
        return (slideshow ? slideIndex_SlideShow : pgPrintMode.getCurrentPageNumber() - 1);
    }


    public int getSlideCount() {
        return pgModel.getSlideCount();
    }


    public int getRealSlideCount() {
        return pgModel.getRealSlideCount();
    }


    public PGSlide getSlide(int index) {
        return pgModel.getSlide(index);
    }


    public IControl getControl() {
        return control;
    }


    public int getmWidth() {
        return mWidth;
    }


    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }


    public int getmHeight() {
        return mHeight;
    }


    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }


    public void setSize(int w, int h) {
        this.mWidth = w;
        this.mHeight = h;
    }


    public float getZoom() {
        return (slideshow ? fitZoom : pgPrintMode.getZoom());
    }


    public void setZoom(float zoom, int pointX, int pointY) {
        if (slideshow) {
            return;
        }
        pgPrintMode.setZoom(zoom, pointX, pointY);
    }


    public void setFitSize(int value) {
        if (slideshow) {
            return;
        }
        pgPrintMode.setFitSize(value);
    }


    public int getFitSizeState() {
        if (slideshow) {
            return 0;
        }
        return pgPrintMode.getFitSizeState();
    }


    public Dimension getPageSize() {
        return pgModel.getPageSize();
    }


    public IDocument getRenderersDoc() {
        return pgModel.getRenderersDoc();
    }


    public PGSlide getCurrentSlide() {
        if (slideshow) {
            return pgModel.getSlide(slideIndex_SlideShow);
        } else {
            return pgPrintMode.getCurrentPGSlide();
        }
    }


    public boolean find(String value) {
        if (!slideshow) {
            return pgFind.find(value);
        }
        return false;
    }


    public boolean findBackward() {
        if (!slideshow) {
            return pgFind.findBackward();
        }
        return false;
    }


    public boolean findForward() {
        if (!slideshow) {
            return pgFind.findForward();
        }
        return false;
    }


    public void resetSearchResult() {
    }


    public int getPageIndex() {
        return -1;
    }


    public String getSelectedText() {
        return editor.getHighlight().getSelectText();
    }


    public PGSlide getSlideMaster(int index) {
        return pgModel.getSlideMaster(index);
    }


    public PGEditor getEditor() {
        return editor;
    }


    public void setAnimationDuration(int duration) {
        if (slideView == null) {
            slideView = new SlideShowView(this, currentSlide);
        }
        if (slideView != null) {
            slideView.setAnimationDuration(duration);
        }
    }


    public void beginSlideShow(int slideIndex) {
        synchronized (this) {
            if (slideIndex <= 0 || slideIndex > pgModel.getSlideCount()) {
                return;
            }
            if (eventManage == null) {
                eventManage = new PGEventManage(this, control);
            }
            boolean isChangedSlide = false;
            if (getCurrentIndex() + 1 != slideIndex) {
                isChangedSlide = true;
            }
            setOnTouchListener(eventManage);
            control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
            pgPrintMode.setVisibility(View.GONE);
            slideshow = true;
            processPageSize(getWidth(), getHeight());
            slideIndex_SlideShow = slideIndex - 1;
            currentSlide = pgModel.getSlide(slideIndex_SlideShow);
            if (slideView == null) {
                slideView = new SlideShowView(this, currentSlide);
            }
            slideView.initSlideShow(currentSlide, true);
            setBackgroundColor(Color.BLACK);
            if (callouts == null) {
                if (!control.getSysKit().getCalloutManager().isPathEmpty()) {
                    initCalloutView();
                }
            } else {
                callouts.setIndex(slideIndex_SlideShow);
            }
            postInvalidate();
            if (isChangedSlide && getControl().getMainFrame() != null) {
                getControl().getMainFrame().changePage();
            }

            post(new Runnable() {

                @Override
                public void run() {
                    initSlidebar();
                    control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                }
            });
        }
    }


    public boolean hasNextSlide_Slideshow() {
        synchronized (this) {
            if (slideshow) {
                return slideIndex_SlideShow < pgModel.getSlideCount() - 1;
            }
            return false;
        }
    }


    public boolean hasPreviousSlide_Slideshow() {
        synchronized (this) {
            if (slideshow) {
                return slideIndex_SlideShow >= 1;
            }
            return false;
        }
    }


    public boolean hasNextAction_Slideshow() {
        synchronized (this) {
            if (slideshow) {
                if (!slideView.gotoNextSlide()
                        || slideIndex_SlideShow < pgModel.getSlideCount() - 1)
                {
                    return true;
                }
            }
            return false;
        }
    }


    public boolean hasPreviousAction_Slideshow() {
        synchronized (this) {
            if (slideshow && (slideIndex_SlideShow >= 1
                    || !slideView.gotopreviousSlide()))
            {
                return true;
            }
            return false;
        }
    }


    public void slideShow(byte type) {
        synchronized (this) {
            if (!slideshow || !slideView.animationStoped() || control.getSysKit().getCalloutManager().getDrawingMode() != MainConstant.DRAWMODE_NORMAL) {
                return;
            }
            if (type == ISlideShow.SlideShow_PreviousSlide && hasPreviousSlide_Slideshow()) {
                slideIndex_SlideShow = slideIndex_SlideShow - 1;
                if (slideIndex_SlideShow >= 0) {
                    slideView.initSlideShow(pgModel.getSlide(slideIndex_SlideShow), true);
                    if (getControl().getMainFrame() != null) {
                        getControl().getMainFrame().changePage();
                    }
                }






            } else {
                if (slideView.isExitSlideShow()) {
                    control.getMainFrame().fullScreen(false);
                    endSlideShow();
                    return;
                }
                switch (type) {
                    case ISlideShow.SlideShow_PreviousStep:
                        if (hasPreviousAction_Slideshow()) {
                            if (slideView.gotopreviousSlide()) {
                                PGSlide slide = pgModel.getSlide(--slideIndex_SlideShow);
                                if (slide != null) {
                                    slideView.initSlideShow(slide, true);
                                    slideView.gotoLastAction();
                                }






                                if (getControl().getMainFrame() != null) {
                                    getControl().getMainFrame().changePage();
                                }
                            } else {
                                slideView.previousActionSlideShow();
                            }
                        }
                        break;
                    case ISlideShow.SlideShow_NextStep:
                        if (hasNextAction_Slideshow()) {
                            if (slideView.gotoNextSlide()) {
                                slideView.initSlideShow(pgModel.getSlide(++slideIndex_SlideShow), true);
                                if (getControl().getMainFrame() != null) {
                                    getControl().getMainFrame().changePage();
                                }
                            } else {
                                slideView.nextActionSlideShow();
                            }
                        }
                        break;
                    case ISlideShow.SlideShow_NextSlide:
                        if (hasNextSlide_Slideshow()) {
                            slideView.initSlideShow(pgModel.getSlide(++slideIndex_SlideShow), true);
                            if (getControl().getMainFrame() != null) {
                                getControl().getMainFrame().changePage();
                            }
                        }
                        break;
                }
            }
            if (callouts != null) {
                callouts.setIndex(slideIndex_SlideShow);
            }
            postInvalidate();

            post(new Runnable() {

                @Override
                public void run() {
                    if (control != null) {
                        control.actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
                    }
                }
            });
        }
    }


    public void endSlideShow() {
        synchronized (this) {
            if (slideshow) {
                control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                setOnTouchListener(null);
                pgPrintMode.setVisibility(View.VISIBLE);
                Object bg = control.getMainFrame().getViewBackground();
                if (bg != null) {
                    if (bg instanceof Integer) {
                        setBackgroundColor((Integer) bg);
                    } else if (bg instanceof Drawable) {
                        setBackgroundDrawable((Drawable) bg);
                    }
                }
                currentIndex = slideIndex_SlideShow;
                slideshow = false;
                slideView.endSlideShow();
                showSlide(currentIndex, false);
                if (callouts != null) {
                    callouts.setVisibility(View.INVISIBLE);
                }

                post(new Runnable() {

                    @Override
                    public void run() {
                        ISlideShow iSlideshow = control.getSlideShow();
                        if (iSlideshow != null) {
                            iSlideshow.exit();
                        }
                        initSlidebar();
                    }
                });
            }
        }
    }


    public boolean isSlideShow() {
        return slideshow;
    }


    public PGFind getFind() {
        return this.pgFind;
    }


    public PGPrintMode getPrintMode() {
        return this.pgPrintMode;
    }

    public Rect getSlideDrawingRect() {
        if (slideshow) {
            if (slideSize == null) {
                slideSize = new Rect(slideView.getDrawingRect());
            } else {
                slideSize.set(slideView.getDrawingRect());
            }
            int w = slideSize.width();
            slideSize.set((mWidth - w) / 2, 0, (mWidth + w) / 2, mHeight);
            return slideSize;
        }
        return null;
    }

    public PGModel getPGModel() {
        return pgModel;
    }


    public int getSlideAnimationSteps(int slideIndex) {
        synchronized (this) {
            List<ShapeAnimation> shapeAnimLst = pgModel.getSlide(slideIndex - 1).getSlideShowAnimation();
            if (shapeAnimLst != null) {
                return shapeAnimLst.size() + 1;
            } else {
                return 1;
            }
        }
    }


    public Bitmap getSlideshowToImage(int slideIndex, int step) {
        synchronized (this) {
            if (slideView == null) {
                slideView = new SlideShowView(this, pgModel.getSlide(slideIndex - 1));
            }
            return slideView.getSlideshowToImage(pgModel.getSlide(slideIndex - 1), step);
        }
    }


    public void dispose() {
        control = null;
        currentSlide = null;
        if (slideView != null) {
            slideView.dispose();
            slideView = null;
        }
        if (eventManage != null) {
            eventManage.dispose();
            eventManage = null;
        }
        pgModel.dispose();
        pgModel = null;

        if (pgFind != null) {
            pgFind.dispose();
            pgFind = null;
        }
    }
}
