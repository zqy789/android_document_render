

package com.document.render.office.wp.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import com.document.render.office.constant.EventConstant;
import com.document.render.office.constant.wp.AttrIDConstant;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.AbstractView;
import com.document.render.office.simpletext.view.DocAttr;
import com.document.render.office.simpletext.view.IRoot;
import com.document.render.office.simpletext.view.IView;
import com.document.render.office.simpletext.view.PageAttr;
import com.document.render.office.simpletext.view.ParaAttr;
import com.document.render.office.simpletext.view.ViewContainer;
import com.document.render.office.simpletext.view.ViewKit;
import com.document.render.office.system.IControl;
import com.document.render.office.wp.control.Word;
import com.document.render.office.wp.model.WPDocument;


public class NormalRoot extends AbstractView implements IRoot {

    private static final int LAYOUT_PARA = 20;

    private boolean relayout = true;

    private IDocument doc;

    private Word word;

    private LayoutThread layoutThread;

    private DocAttr docAttr;

    private PageAttr pageAttr;

    private ParaAttr paraAttr;

    private ParagraphView prePara;

    private ViewContainer viewContainer;



    private long currentLayoutOffset;

    private int maxParaWidth;

    private boolean canBackLayout;

    private TableLayoutKit tableLayout;


    public NormalRoot(Word word) {
        this.word = word;
        this.doc = word.getDocument();
        layoutThread = new LayoutThread(this);
        canBackLayout = true;
        docAttr = new DocAttr();
        docAttr.rootType = WPViewConstant.NORMAL_ROOT;
        pageAttr = new PageAttr();
        paraAttr = new ParaAttr();
        viewContainer = new ViewContainer();
        tableLayout = new TableLayoutKit();
    }


    public short getType() {
        return WPViewConstant.NORMAL_ROOT;
    }


    public synchronized int layoutAll() {
        super.dispose();
        tableLayout.clearBreakPages();
        word.getControl().getSysKit().getListManage().resetForNormalView();
        viewContainer.clear();
        maxParaWidth = 0;
        prePara = null;
        currentLayoutOffset = 0;
        layoutPara();
        if (currentLayoutOffset < doc.getAreaEnd(WPModelConstant.MAIN)) {
            canBackLayout = true;
            if (layoutThread.getState() == Thread.State.NEW) {
                layoutThread.start();
            }
            word.getControl().actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, true);
        }
        layoutRoot();
        if (word.isExportImageAfterZoom()) {
            if (getHeight() * word.getZoom() >= word.getScrollY() + word.getHeight()
                    || currentLayoutOffset >= doc.getAreaEnd(WPModelConstant.MAIN)) {
                word.setExportImageAfterZoom(false);
                word.getControl().actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
            }
        }
        return WPViewConstant.BREAK_NO;
    }


    public int doLayout(int x, int y, int w, int h, int maxEnd, int flag) {
        IDocument doc = getDocument();
        viewContainer.clear();

        layoutPara();
        if (currentLayoutOffset < doc.getAreaEnd(WPModelConstant.MAIN)) {
            if (layoutThread.getState() == Thread.State.NEW) {
                layoutThread.start();
            }
            word.getControl().actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, true);

        }
        layoutRoot();
        return WPViewConstant.BREAK_NO;
    }


    private int layoutPara() {
        relayout = true;
        int dx = WPViewConstant.PAGE_SPACE;
        int dy = prePara == null ? WPViewConstant.PAGE_SPACE : prePara.getY() + prePara.getHeight();
        int spanW = 0;
        if (word.getControl().getMainFrame().isZoomAfterLayoutForWord()) {
            spanW = (int) (word.getResources().getDisplayMetrics().widthPixels / word.getZoom()) - WPViewConstant.PAGE_SPACE * 2;
        } else {
            spanW = word.getResources().getDisplayMetrics().widthPixels - WPViewConstant.PAGE_SPACE * 2;
        }
        int spanH = Integer.MAX_VALUE;
        int flag = ViewKit.instance().setBitValue(0, WPViewConstant.LAYOUT_FLAG_KEEPONE, true);
        int count = 0;
        long areaEnd = doc.getAreaEnd(WPModelConstant.MAIN);
        long start;
        IElement elem;
        IDocument doc = word.getDocument();
        while (count < LAYOUT_PARA && currentLayoutOffset < areaEnd && relayout) {
            elem = doc.getParagraph(currentLayoutOffset);
            ParagraphView para = null;
            if (AttrManage.instance().hasAttribute(elem.getAttribute(), AttrIDConstant.PARA_LEVEL_ID)) {
                elem = ((WPDocument) doc).getParagraph0(currentLayoutOffset);
                para = (ParagraphView) ViewFactory.createView(word.getControl(), elem, null, WPViewConstant.TABLE_VIEW);
                if (prePara != null && elem != prePara.getElement()) {
                    tableLayout.clearBreakPages();
                }
            } else {
                para = (ParagraphView) ViewFactory.createView(word.getControl(), elem, null, WPViewConstant.PARAGRAPH_VIEW);
            }
            para.setParentView(this);
            start = elem.getStartOffset();
            para.setStartOffset(start);
            para.setEndOffset(elem.getEndOffset());
            if (prePara == null) {
                setChildView(para);
            } else {
                prePara.setNextView(para);
                para.setPreView(prePara);
            }
            para.setLocation(dx, dy);

            if (para.getType() == WPViewConstant.TABLE_VIEW) {
                tableLayout.layoutTable(word.getControl(), doc, this, docAttr, pageAttr, paraAttr,
                        (TableView) para, currentLayoutOffset, dx, dy, spanW, spanH, flag, false);
            } else {
                tableLayout.clearBreakPages();
                AttrManage.instance().fillParaAttr(word.getControl(), paraAttr, elem.getAttribute());
                filteParaAttr(paraAttr);

                LayoutKit.instance().layoutPara(word.getControl(), doc, docAttr, pageAttr, paraAttr,
                        para, currentLayoutOffset, dx, dy, spanW, spanH, flag);
            }
            int paraHeight = para.getLayoutSpan(WPViewConstant.Y_AXIS);
            maxParaWidth = Math.max(para.getLayoutSpan(WPViewConstant.X_AXIS) + WPViewConstant.PAGE_SPACE, maxParaWidth);
            dy += paraHeight;
            spanH -= paraHeight;
            currentLayoutOffset = para.getEndOffset(null);
            count++;
            prePara = para;

            viewContainer.add(para);
        }

        return WPViewConstant.BREAK_NO;
    }


    private void filteParaAttr(ParaAttr paraAttr) {
        paraAttr.rightIndent = paraAttr.rightIndent < 0 ? 0 : paraAttr.rightIndent;
        paraAttr.leftIndent = paraAttr.leftIndent < 0 ? 0 : paraAttr.leftIndent;

    }


    public void draw(Canvas canvas, int originX, int originY, float zoom) {
        canvas.drawColor(Color.WHITE);

        int dX = (int) (x * zoom) + originX;
        int dY = (int) (y * zoom) + originY;
        IView view = getChildView();
        Rect clip = canvas.getClipBounds();
        boolean b = false;
        while (view != null) {
            if (view.intersection(clip, dX, dY, zoom)) {
                view.draw(canvas, dX, dY, zoom);
                b = true;
            } else if (b) {
                break;
            }
            view = view.getNextView();
        }
    }


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {

        IView view = viewContainer.getParagraph(offset, isBack);
        if (view != null) {
            view.modelToView(offset, rect, isBack);

            IView p = view.getParentView();
            while (p != null && p.getType() != WPViewConstant.NORMAL_ROOT) {

                {
                    rect.x += p.getX();
                    rect.y += p.getY();
                }
                p = p.getParentView();
            }
        }
        rect.x += getX();
        rect.y += getY();
        return rect;

    }


    public long viewToModel(int x, int y, boolean isBack) {
        x -= getX();
        y -= getY();
        IView view = getChildView();
        if (view == null) {
            return -1;
        }
        if (y > view.getY()) {
            while (view != null) {
                if (y >= view.getY() && y < view.getY() + view.getLayoutSpan(WPViewConstant.Y_AXIS)) {
                    break;
                }
                view = view.getNextView();
            }
        }
        view = view == null ? getChildView() : view;
        if (view != null) {
            return view.viewToModel(x, y, isBack);
        }
        return -1;
    }


    public IDocument getDocument() {
        return word.getDocument();
    }


    public IWord getContainer() {
        return word;
    }


    public IControl getControl() {
        return word.getControl();
    }


    public boolean canBackLayout() {
        return canBackLayout && currentLayoutOffset < doc.getAreaEnd(WPModelConstant.MAIN);
    }


    public synchronized void backLayout() {
        layoutPara();
        layoutRoot();
        if (currentLayoutOffset >= doc.getAreaEnd(WPModelConstant.MAIN)) {
            word.getControl().actionEvent(EventConstant.SYS_AUTO_TEST_FINISH_ID, true);
            word.getControl().actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, false);

            Rectangle r = word.getVisibleRect();
            int sX = r.x;
            int sY = r.y;
            int wW = (int) (getWidth() * word.getZoom());
            int wH = (int) (getHeight() * word.getZoom());
            if (r.x + r.width > wW) {
                sX = wW - r.width;
            }
            if (r.y + r.height > wH) {
                sY = wH - r.height;
            }
            final int sTx = sX;
            final int sTy = sY;
            if (sX != r.x || sY != r.y) {
                word.post(new Runnable() {

                    @Override
                    public void run() {
                        word.scrollTo(Math.max(0, sTx), Math.max(0, sTy));
                    }
                });
            }
        }
        word.postInvalidate();
        if (word.isExportImageAfterZoom()) {
            if (getHeight() * word.getZoom() >= word.getScrollY() + word.getHeight()
                    || currentLayoutOffset >= doc.getAreaEnd(WPModelConstant.MAIN)) {
                word.setExportImageAfterZoom(false);
                word.getControl().actionEvent(EventConstant.APP_GENERATED_PICTURE_ID, null);
            }
        }
    }


    public void layoutRoot() {
        if (prePara != null) {
            setSize(Math.max(word.getWidth(), maxParaWidth), prePara.getY() + prePara.getHeight());
        }
    }


    public void stopBackLayout() {
        canBackLayout = false;
        relayout = false;
    }


    public ViewContainer getViewContainer() {
        return this.viewContainer;
    }


    public synchronized void dispose() {
        super.dispose();
        canBackLayout = false;
        layoutThread.dispose();
        layoutThread = null;
        word = null;
        docAttr.dispose();
        docAttr = null;
        pageAttr.dispose();
        pageAttr = null;
        paraAttr.dispose();
        paraAttr = null;
        prePara = null;
        doc = null;
        tableLayout = null;
    }
}
