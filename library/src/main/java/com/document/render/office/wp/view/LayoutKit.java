
package com.document.render.office.wp.view;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.wp.AttrIDConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.simpletext.font.FontKit;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.DocAttr;
import com.document.render.office.simpletext.view.IView;
import com.document.render.office.simpletext.view.PageAttr;
import com.document.render.office.simpletext.view.ParaAttr;
import com.document.render.office.simpletext.view.ViewKit;
import com.document.render.office.system.IControl;
import com.document.render.office.wp.control.Word;


public class LayoutKit {
    private static final String TAG = "LayoutKit";
    private static LayoutKit kit = new LayoutKit();
    private int screenWidthPixels = 0;
    private int screenHeightPixels = 0;

    private LayoutKit() {
    }


    public static LayoutKit instance() {
        return kit;
    }


    public void layoutAllPage(PageRoot root, float zoom) {
        if (root == null || root.getChildView() == null) {
            return;
        }
        Word word = (Word) root.getContainer();
        if (word.getContext() != null && (screenWidthPixels == 0 || screenHeightPixels == 0)) {

            Resources resources = word.getContext().getResources();
            boolean isLandscape = resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

            DisplayMetrics displayMetrics = resources.getDisplayMetrics();

            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;
            if (isLandscape) {
                screenWidthPixels = Math.max(width, height);
                screenHeightPixels = Math.min(width, height);
            } else {
                screenWidthPixels = Math.min(width, height);
                screenHeightPixels = Math.max(width, height);
            }

            IView pv = root.getChildView();
            int pvWidth = pv.getWidth();
            float scale = 1;
            if (screenWidthPixels != 0 && pvWidth != 0) {
                scale = screenWidthPixels * 1f / pvWidth;
                if (zoom == 1f) {
                    word.setZoom(scale, 0, 0);
                }
            }
            Log.d(TAG, "layoutAllPage screenWidthPixels = " + screenWidthPixels + "; isLandscape " + isLandscape + ", pvWidth = " + pvWidth + "; scale = " + scale);
        }
        int dx = WPViewConstant.PAGE_SPACE;
        int dy = WPViewConstant.PAGE_SPACE;
        IView pv = root.getChildView();
        int width = pv.getWidth();





        while (pv != null) {
            pv.setLocation(dx, dy);
            dy += pv.getHeight() + WPViewConstant.PAGE_SPACE;
            pv = pv.getNextView();
        }
        root.setSize(width + WPViewConstant.PAGE_SPACE * 2, dy);
        ((Word) root.getContainer()).setSize(width + WPViewConstant.PAGE_SPACE * 2, dy);
    }


    public int layoutPara(IControl control, IDocument doc, DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, ParagraphView para, long startOffset, int x, int y, int w, int h, int flag) {


        int breakType = WPViewConstant.BREAK_NO;
        int dx = paraAttr.leftIndent;
        int dy = 0;
        int spanW = w - paraAttr.leftIndent - paraAttr.rightIndent;
        spanW = spanW < 0 ? w : spanW;
        int spanH = h;
        int paraHeight = 0;
        int maxWidth = ViewKit.instance().getBitValue(flag, WPViewConstant.LAYOUT_NOT_WRAP_LINE) ? 0 : w;
        boolean firstLine = true;
        IElement elem = para.getElement();
        long lineStart = startOffset;
        long elemEnd = elem.getEndOffset();

        IView prePara = para.getPreView();
        if (prePara == null)
        {
            spanH -= paraAttr.beforeSpace;
            para.setTopIndent(paraAttr.beforeSpace);
            para.setBottomIndent(paraAttr.afterSpace);
            para.setY(para.getY() + paraAttr.beforeSpace);
        } else {
            if (paraAttr.beforeSpace > 0) {
                int beforeSpace = paraAttr.beforeSpace - prePara.getBottomIndent();
                beforeSpace = Math.max(0, beforeSpace);
                spanH -= beforeSpace;
                para.setTopIndent(beforeSpace);
                para.setY(para.getY() + beforeSpace);
            }
            spanH -= paraAttr.afterSpace;
            para.setBottomIndent(paraAttr.afterSpace);
        }
        boolean keepOne = ViewKit.instance().getBitValue(flag, WPViewConstant.LAYOUT_FLAG_KEEPONE);
        if (spanH < 0 && !keepOne) {
            return WPViewConstant.BREAK_LIMIT;
        }
        LineView line = (LineView) ViewFactory.createView(control, elem, elem, WPViewConstant.LINE_VIEW);
        line.setStartOffset(lineStart);
        para.appendChlidView(line);
        flag = ViewKit.instance().setBitValue(flag, WPViewConstant.LAYOUT_FLAG_KEEPONE, true);
        boolean ss = ViewKit.instance().getBitValue(flag, WPViewConstant.LAYOUT_FLAG_DELLELINEVIEW);
        BNView bnView = null;
        int bnViewWidth = -1;
        while (spanH > 0 && lineStart < elemEnd && breakType != WPViewConstant.BREAK_PAGE) {

            if (firstLine && startOffset == elem.getStartOffset()) {
                bnView = createBNView(control, doc, docAttr, pageAttr, paraAttr, para, dx, dy, spanW, spanH, flag);
                if (bnView != null) {
                    bnViewWidth = bnView.getWidth();
                }
            }
            int lineIndent = getLineIndent(control, bnViewWidth, paraAttr, firstLine);
            if (bnView != null && lineIndent + paraAttr.leftIndent == paraAttr.tabClearPosition) {
                if ((AttrManage.instance().hasAttribute(elem.getAttribute(), AttrIDConstant.PARA_SPECIALINDENT_ID) && AttrManage.instance().getParaSpecialIndent(elem.getAttribute()) < 0) || AttrManage.instance().hasAttribute(elem.getAttribute(), AttrIDConstant.PARA_INDENT_LEFT_ID)) {
                    bnView.setX(0);
                    lineIndent = bnViewWidth;
                    dx = 0;
                }
            }
            line.setLeftIndent(lineIndent);
            line.setLocation(dx + lineIndent, dy);
            breakType = layoutLine(control, doc, docAttr, pageAttr, paraAttr, line, bnView, dx, dy, spanW - lineIndent, spanH, elemEnd, flag);
            int lineHeight = line.getLayoutSpan(WPViewConstant.Y_AXIS);
            if (!ss && !keepOne
                     && ((spanH - lineHeight < 0 || line.getChildView() == null) || spanW - lineIndent <= 0)) {
                breakType = WPViewConstant.BREAK_LIMIT;
                para.deleteView(line, true);
                break;
            }
            paraHeight += lineHeight;
            dy += lineHeight;
            spanH -= lineHeight;
            lineStart = line.getEndOffset(null);
            maxWidth = Math.max(maxWidth, line.getLayoutSpan(WPViewConstant.X_AXIS));
            if (lineStart < elemEnd && spanH > 0) {
                line = (LineView) ViewFactory.createView(control, elem, elem, WPViewConstant.LINE_VIEW);
                line.setStartOffset(lineStart);
                para.appendChlidView(line);
            }
            keepOne = false;

            firstLine = false;
            bnView = null;
        }
        para.setSize(maxWidth, paraHeight);
        para.setEndOffset(lineStart);


        return breakType;
    }


    public int buildLine(IDocument doc, ParagraphView para) {
        int breakType = WPViewConstant.BREAK_NO;
        return breakType;
    }



    public int layoutLine(IControl control, IDocument doc, DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, LineView line, BNView bnView, int x, int y, int w, int h, long maxEnd, int flag) {
        int breakType = WPViewConstant.BREAK_NO;
        int dx = 0;
        int dy = 0;
        int spanW = w;
        long start = line.getStartOffset(null);
        long pos = start;
        IElement elem = line.getElement();
        LeafView leaf = null;
        IElement run;
        int lineWidth = 0;
        int lineHeigth = 0;
        int lineHeigthExceptShape = 0;
        boolean keepOne = ViewKit.instance().getBitValue(flag, WPViewConstant.LAYOUT_FLAG_KEEPONE);
        while (spanW > 0 && pos < maxEnd || keepOne) {
            run = doc.getLeaf(pos);
            if (run == null) {
                break;
            }
            leaf = (LeafView) ViewFactory.createView(control, run, elem, WPViewConstant.LEAF_VIEW);
            line.appendChlidView(leaf);
            leaf.setStartOffset(pos);
            leaf.setLocation(dx, dy);
            breakType = leaf.doLayout(docAttr, pageAttr, paraAttr, dx, dy, spanW, h, maxEnd, flag);
            if ((leaf.getType() == WPViewConstant.OBJ_VIEW || leaf.getType() == WPViewConstant.SHAPE_VIEW) && breakType == WPViewConstant.BREAK_LIMIT) {
                line.deleteView(leaf, true);
                breakType = WPViewConstant.BREAK_NO;
                break;
            }
            pos = leaf.getEndOffset(null);
            line.setEndOffset(pos);
            int leafWidth = leaf.getLayoutSpan(WPViewConstant.X_AXIS);
            lineWidth += leafWidth;
            dx += leafWidth;
            lineHeigth = Math.max(lineHeigth, leaf.getLayoutSpan(WPViewConstant.Y_AXIS));
            if (leaf.getType() != WPViewConstant.OBJ_VIEW && leaf.getType() != WPViewConstant.SHAPE_VIEW) {
                lineHeigthExceptShape = Math.max(lineHeigthExceptShape, leaf.getLayoutSpan(WPViewConstant.Y_AXIS));
            }
            spanW -= leafWidth;
            if (breakType == WPViewConstant.BREAK_LIMIT || breakType == WPViewConstant.BREAK_ENTER || breakType == WPViewConstant.BREAK_PAGE) {
                break;
            }
            flag = ViewKit.instance().setBitValue(flag, WPViewConstant.LAYOUT_FLAG_KEEPONE, false);
            keepOne = false;
        }
        line.setSize(lineWidth, lineHeigth);
        line.setHeightExceptShape(lineHeigthExceptShape);

        if (breakType == WPViewConstant.BREAK_LIMIT) {
            String str = elem.getText(doc);
            long paraStart = elem.getStartOffset();
            str = str.substring((int) (start - paraStart));
            long newPos = FontKit.instance().findBreakOffset(str, (int) (pos - start)) + start;
            adjustLine(line, newPos);
        }
        line.layoutAlignment(docAttr, pageAttr, paraAttr, bnView, w, flag);
        return breakType;
    }


    private BNView createBNView(IControl control, IDocument doc, DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, ParagraphView para, int x, int y, int w, int h, int flag) {
        if (paraAttr.listID >= 0 && paraAttr.listLevel >= 0 || paraAttr.pgBulletID >= 0) {
            BNView bnView = (BNView) ViewFactory.createView(control, null, null, WPViewConstant.BN_VIEW);
            bnView.doLayout(doc, docAttr, pageAttr, paraAttr, para, x, y, w, h, flag);
            para.setBNView(bnView);
            return bnView;
        }
        return null;
    }


    private void adjustLine(LineView line, long newPos) {
        IView view = line.getLastView();
        IView temp;
        int lineWidth = line.getWidth();
        while (view != null && view.getStartOffset(null) >= newPos) {
            temp = view.getPreView();
            lineWidth -= view.getWidth();
            line.deleteView(view, true);
            view = temp;
        }

        int leafWidth = 0;
        if (view != null && view.getEndOffset(null) > newPos) {
            view.setEndOffset(newPos);
            lineWidth -= view.getWidth();
            leafWidth = (int) ((LeafView) view).getTextWidth();

            view.setWidth(leafWidth);
            lineWidth += leafWidth;
        }
        line.setEndOffset(newPos);
        line.setWidth(lineWidth);
    }


    private int getLineIndent(IControl control, int bnViewWidth, ParaAttr paraAttr, boolean firstLine) {

        if (firstLine) {
            int bnWidth = bnViewWidth <= 0 ? 0 : bnViewWidth;
            if (paraAttr.specialIndentValue > 0) {
                return paraAttr.specialIndentValue + bnWidth;
            } else {
                return bnWidth;
            }
        }

        else if (!firstLine && paraAttr.specialIndentValue < 0) {
            if (bnViewWidth > 0 && control.getApplicationType() == MainConstant.APPLICATION_TYPE_PPT) {
                return bnViewWidth;
            }

            return -paraAttr.specialIndentValue;
        }
        return 0;
    }






}
