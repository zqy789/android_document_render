
package com.document.render.office.pg.control;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.View;

import com.document.render.office.common.ICustomDialog;
import com.document.render.office.common.IOfficeToPicture;
import com.document.render.office.common.ISlideShow;
import com.document.render.office.common.hyperlink.Hyperlink;
import com.document.render.office.constant.DialogConstant;
import com.document.render.office.constant.EventConstant;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.pg.model.PGModel;
import com.document.render.office.system.AbstractControl;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IFind;
import com.document.render.office.system.IMainFrame;
import com.document.render.office.system.SysKit;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Vector;


public class PGControl extends AbstractControl {

    
    private boolean isDispose;
    
    private Presentation pgView;
    
    private IControl mainControl;
    
    private boolean isShowingProgressDlg;
    
    private ProgressDialog progressDialog;

    
    public PGControl(IControl mainControl, PGModel pgModel, String filePath) {
        this.mainControl = mainControl;
        pgView = new Presentation(getMainFrame().getActivity(), pgModel, this);
    }

    
    public void layoutView(int x, int y, int w, int h) {
    }

    
    public void actionEvent(int actionID, final @Nullable Object obj) {
        switch (actionID) {
            case EventConstant.SYS_SET_PROGRESS_BAR_ID:
                if (pgView.getParent() != null) {
                    pgView.post(new Runnable() {
                        
                        public void run() {
                            if (!isDispose) {
                                
                                mainControl.getMainFrame().showProgressBar((Boolean) obj);
                            }
                        }
                    });
                }
                break;

            case EventConstant.SYS_VECTORGRAPH_PROGRESS:
                if (pgView.getParent() != null) {
                    pgView.post(new Runnable() {
                        
                        public void run() {
                            if (!isDispose) {
                                mainControl.getMainFrame().updateViewImages((List<Integer>) obj);
                            }
                        }
                    });
                } else {
                    new Thread() {
                        
                        public void run() {
                            if (!isDispose) {
                                mainControl.getMainFrame().updateViewImages((List<Integer>) obj);
                            }
                        }
                    }.start();
                }
                break;

            case EventConstant.SYS_INIT_ID:
                pgView.init();
                break;

            case EventConstant.TEST_REPAINT_ID:
            case EventConstant.PG_REPAINT_ID:
                pgView.postInvalidate();
                break;

            case EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS:   
                pgView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDispose) {
                            if (getMainFrame() != null) {
                                getMainFrame().updateToolsbarStatus();
                            }
                        }
                    }
                });
                break;

            case EventConstant.APP_PAGE_UP_ID:
                if (pgView.isSlideShow()) {
                    pgView.slideShow(ISlideShow.SlideShow_PreviousSlide);
                } else {
                    if (pgView.getCurrentIndex() > 0) {
                        pgView.showSlide(pgView.getCurrentIndex() - 1, false);
                    }
                }
                break;

            case EventConstant.APP_PAGE_DOWN_ID:
                if (pgView.isSlideShow()) {
                    pgView.slideShow(ISlideShow.SlideShow_NextSlide);
                } else {
                    if (pgView.getCurrentIndex() < pgView.getRealSlideCount() - 1) {
                        pgView.showSlide(pgView.getCurrentIndex() + 1, false);
                    }
                }
                break;

            case EventConstant.PG_SHOW_SLIDE_ID:
                if (!pgView.isSlideShow()) {
                    showSlide((Integer) obj);
                }
                break;

            case EventConstant.APP_ZOOM_ID:
                if (!pgView.isSlideShow()) {
                    int[] params = (int[]) obj;
                    pgView.setZoom(params[0] / (float) MainConstant.STANDARD_RATE, params[1], params[2]);
                    pgView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isDispose) {
                                getMainFrame().changeZoom();
                            }
                        }
                    });
                }
                break;

            case EventConstant.PG_NOTE_ID:                          
                String text = pgView.getCurrentSlide().getNotes().getNotes();
                Vector<Object> vector = new Vector<Object>();
                vector.add(text);
                new com.document.render.office.pg.dialog.NotesDialog(this, getMainFrame().getActivity(), null, vector,
                        DialogConstant.SHOW_PG_NOTE_ID).show();
                break;

            case EventConstant.FILE_COPY_ID:                      
                ClipboardManager clip = (ClipboardManager) getMainFrame().getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clip.setText(pgView.getSelectedText());
                break;

            case EventConstant.APP_HYPERLINK: 
                String addr = ((Hyperlink) obj).getAddress();
                if (addr != null) {
                    try {
                        Intent hyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(addr));
                        getMainFrame().getActivity().startActivity(hyIntent);
                    } catch (Exception e) {

                    }
                }
                break;

            case EventConstant.SYS_AUTO_TEST_FINISH_ID: 
                if (isAutoTest()) {
                    getMainFrame().getActivity().onBackPressed();
                }
                break;

            case EventConstant.APP_GENERATED_PICTURE_ID:
                pgView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isDispose) {
                            pgView.createPicture();
                        }
                    }
                });
                break;
            case EventConstant.PG_SLIDESHOW_DURATION:
                pgView.setAnimationDuration((Integer) obj);
                break;

            case EventConstant.PG_SLIDESHOW_GEGIN:
                
                getMainFrame().fullScreen(true);
                
                this.pgView.beginSlideShow(obj == null ? pgView.getCurrentIndex() + 1 : (Integer) obj);
                break;
            case EventConstant.PG_SLIDESHOW_END:
                pgView.endSlideShow();
                break;

            case EventConstant.PG_SLIDESHOW_PREVIOUS:
                pgView.slideShow(ISlideShow.SlideShow_PreviousStep);
                break;

            case EventConstant.PG_SLIDESHOW_NEXT:
                pgView.slideShow(ISlideShow.SlideShow_NextStep);
                break;

            case EventConstant.APP_COUNT_PAGES_CHANGE_ID:
                pagesCountChanged();
                break;

            case EventConstant.APP_SET_FIT_SIZE_ID:
                pgView.setFitSize((Integer) obj);
                break;

            case EventConstant.APP_INIT_CALLOUTVIEW_ID:
                pgView.initCalloutView();
                break;

            default:
                break;
        }
    }

    private void pagesCountChanged() {
        if (isShowingProgressDlg && pgView.showLoadingSlide()) {
            isShowingProgressDlg = false;

            pgView.post(new Runnable() {
                
                public void run() {
                    if (getMainFrame().isShowProgressBar()) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    } else {
                        ICustomDialog dlgListener = mainControl.getCustomDialog();
                        if (dlgListener != null) {
                            dlgListener.dismissDialog(ICustomDialog.DIALOGTYPE_LOADING);
                        }
                    }
                }
            });

        }
    }

    
    private void showSlide(final int slideIndex) {
        if (slideIndex < 0 || slideIndex >= pgView.getSlideCount()) {
            return;
        }

        isShowingProgressDlg = false;
        if (slideIndex >= pgView.getRealSlideCount()) {
            isShowingProgressDlg = true;

            if (Objects.requireNonNull(getMainFrame()).isShowProgressBar()) {
                final PGControl own = this;
                pgView.postDelayed(() -> {
                    if (isShowingProgressDlg) {
                        progressDialog = ProgressDialog.show(getActivity(),
                                own.getMainFrame().getAppName(), own.getMainFrame().getLocalString("DIALOG_LOADING"),
                                false, false, null);
                        progressDialog.show();
                    }
                }, 200);
            } else {
                ICustomDialog dlgListener = mainControl.getCustomDialog();
                if (dlgListener != null) {
                    dlgListener.showDialog(ICustomDialog.DIALOGTYPE_LOADING);
                }
            }
        }
        
        {
            pgView.showSlide(slideIndex, false);
        }
    }

    
    public @Nullable Object getActionValue(int actionID, @Nullable Object obj) {
        switch (actionID) {
            case EventConstant.APP_ZOOM_ID:
                return pgView.getZoom();

            case EventConstant.APP_COUNT_PAGES_ID:
                return pgView.getSlideCount();

            case EventConstant.APP_GET_REAL_PAGE_COUNT_ID:
                return pgView.getRealSlideCount();

            case EventConstant.APP_CURRENT_PAGE_NUMBER_ID:
                return pgView.getCurrentIndex() + 1;

            case EventConstant.APP_FIT_ZOOM_ID:
                return pgView.getFitZoom();

            case EventConstant.PG_SLIDE_TO_IMAGE:
                return pgView.slideToImage((Integer) obj);

            case EventConstant.APP_PAGEAREA_TO_IMAGE:
                if (obj instanceof int[]) {
                    int[] paraArr = (int[]) obj;
                    if (paraArr != null && paraArr.length == 7) {
                        return pgView.slideAreaToImage(paraArr[0], paraArr[1], paraArr[2], paraArr[3], paraArr[4], paraArr[5], paraArr[6]);
                    }
                }
                break;

            case EventConstant.PG_GET_SLIDE_NOTE:
                return pgView.getSldieNote((Integer) obj);

            case EventConstant.PG_GET_SLIDE_SIZE:
                int index = (Integer) obj;
                if (index <= 0 || index > pgView.getSlideCount()) {
                    return null;
                }
                Dimension d = pgView.getPageSize();
                return new Rectangle(0, 0, (int) d.width, (int) d.height);

            case EventConstant.PG_SLIDESHOW:
                return pgView.isSlideShow();

            case EventConstant.APP_PAGE_UP_ID:
                return pgView.hasPreviousSlide_Slideshow();

            case EventConstant.APP_PAGE_DOWN_ID:
                return pgView.hasNextSlide_Slideshow();

            case EventConstant.PG_SLIDESHOW_HASPREVIOUSACTION:
                return pgView.hasPreviousAction_Slideshow();

            case EventConstant.PG_SLIDESHOW_HASNEXTACTION:
                return pgView.hasNextAction_Slideshow();

            case EventConstant.APP_THUMBNAIL_ID:
                if (obj instanceof int[]) {
                    int[] a = (int[]) obj;
                    if (a.length < 2 || a[1] <= 0) {
                        return null;
                    }
                    return pgView.getThumbnail(a[0], a[1] / (float) MainConstant.STANDARD_RATE);
                }
                break;

            case EventConstant.PG_SLIDESHOW_SLIDEEXIST:
                return ((Integer) obj).intValue() <= pgView.getRealSlideCount();

            case EventConstant.PG_SLIDESHOW_ANIMATIONSTEPS:
                return pgView.getSlideAnimationSteps((Integer) obj);

            case EventConstant.PG_SLIDESHOW_SLIDESHOWTOIMAGE:
                if (obj instanceof int[]) {
                    int[] a = (int[]) obj;
                    if (a.length < 2 || a[1] <= 0) {
                        return null;
                    }
                    return pgView.getSlideshowToImage(a[0], a[1]);
                }
                break;

            case EventConstant.APP_GET_FIT_SIZE_STATE_ID:
                if (pgView != null) {
                    return pgView.getFitSizeState();
                }
                break;

            case EventConstant.APP_GET_SNAPSHOT_ID:
                if (pgView != null) {
                    return pgView.getSnapshot((Bitmap) obj);
                }
                break;

            default:
                break;
        }
        return null;
    }

    
    public int getCurrentViewIndex() {
        return pgView.getCurrentIndex() + 1;
    }

    
    public View getView() {
        return pgView;
    }

    
    public Dialog getDialog(Activity activity, int id) {
        return null;
    }

    
    public IMainFrame getMainFrame() {
        return mainControl.getMainFrame();
    }

    
    public Activity getActivity() {
        return mainControl.getMainFrame().getActivity();
    }

    
    public IFind getFind() {
        return pgView.getFind();
    }

    
    public boolean isAutoTest() {
        return mainControl.isAutoTest();
    }

    
    public IOfficeToPicture getOfficeToPicture() {
        return mainControl.getOfficeToPicture();
    }

    
    public ICustomDialog getCustomDialog() {
        return mainControl.getCustomDialog();
    }

    
    public boolean isSlideShow() {
        return pgView.isSlideShow();
    }

    
    public ISlideShow getSlideShow() {
        return mainControl.getSlideShow();
    }

    
    public int getApplicationType() {
        return MainConstant.APPLICATION_TYPE_PPT;
    }

    
    public SysKit getSysKit() {
        return mainControl.getSysKit();
    }

    
    public void dispose() {
        isDispose = true;
        pgView.dispose();
        pgView = null;
        mainControl = null;
    }
}
