
package com.document.render.office.wp.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.wp.WPAttrConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.AbstractView;
import com.document.render.office.simpletext.view.CharAttr;
import com.document.render.office.simpletext.view.DocAttr;
import com.document.render.office.simpletext.view.IView;
import com.document.render.office.simpletext.view.PageAttr;
import com.document.render.office.simpletext.view.ParaAttr;
import com.document.render.office.simpletext.view.ViewKit;


public class LineView extends AbstractView {

    private int heightExceptShape;


    public LineView() {
    }


    public LineView(IElement elem) {
        this.elem = elem;
    }


    public short getType() {
        return WPViewConstant.LINE_VIEW;
    }


    public void layoutAlignment(DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, BNView bnView, int span, int flag) {
        layoutAlignment(docAttr, pageAttr, paraAttr, bnView, span, flag, true);
    }

    public int getHeightExceptShape() {
        return heightExceptShape;
    }

    public void setHeightExceptShape(int height) {
        this.heightExceptShape = height;
    }


    public void layoutAlignment(DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, BNView bnView, int span, int flag, boolean isLayoutVertical) {

        if (!ViewKit.instance().getBitValue(flag, WPViewConstant.LAYOUT_NOT_WRAP_LINE)) {
            layoutHorizontal(docAttr, pageAttr, paraAttr, bnView, span, flag);
        }

        if (isLayoutVertical) {
            layoutVertical(docAttr, pageAttr, paraAttr, bnView, span, flag);
        }
    }


    public void layoutHorizontal(DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, BNView bnView, int span, int flag) {

        switch (paraAttr.horizontalAlignment) {
            case WPAttrConstant.PARA_HOR_ALIGN_CENTER:
                x += (span - width) / 2;
                break;
            case WPAttrConstant.PARA_HOR_ALIGN_RIGHT:
                x += (span - width);
                break;

            default:
                break;
        }
    }


    private void layoutVertical(DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, BNView bnView, int span, int flag) {
        LeafView leaf = (LeafView) getChildView();
        if (leaf == null) {
            return;
        }
        int maxBaseline = bnView == null ? 0 : bnView.getBaseline();
        while (leaf != null) {
            maxBaseline = Math.max(maxBaseline, leaf.getBaseline());
            leaf = (LeafView) leaf.getNextView();
        }

        leaf = (LeafView) getChildView();
        while (leaf != null) {
            if (leaf.getType() == WPViewConstant.SHAPE_VIEW) {
                if (!((ShapeView) leaf).isInline()) {
                    leaf = (LeafView) leaf.getNextView();
                    continue;
                }
            } else if (leaf.getType() == WPViewConstant.OBJ_VIEW) {
                if (!((ObjView) leaf).isInline()) {
                    leaf = (LeafView) leaf.getNextView();
                    continue;
                }
            }

            int baseline = maxBaseline - leaf.getBaseline();
            leaf.setTopIndent(baseline);
            leaf.setY(leaf.getY() + baseline);
            leaf = (LeafView) leaf.getNextView();
        }
        float value = 0;
        boolean processline = false;

        switch (paraAttr.lineSpaceType) {
            case WPAttrConstant.LINE_SPACE_SINGLE:
            case WPAttrConstant.LINE_SPACE_ONE_HALF:
            case WPAttrConstant.LINE_SPACE_DOUBLE:
            case WPAttrConstant.LINE_SAPCE_MULTIPLE:
                processline = true;
                if (pageAttr.pageLinePitch > 0) {
                    if (heightExceptShape > pageAttr.pageLinePitch * paraAttr.lineSpaceValue) {
                        value = (float) (Math.ceil(heightExceptShape / pageAttr.pageLinePitch) * pageAttr.pageLinePitch);
                    } else {
                        value = pageAttr.pageLinePitch * paraAttr.lineSpaceValue;
                    }
                } else {
                    value = paraAttr.lineSpaceValue * heightExceptShape;
                }
                break;

            case WPAttrConstant.LINE_SAPCE_LEAST:
                processline = true;
                if (paraAttr.lineSpaceValue > heightExceptShape) {
                    processline = true;
                    if (pageAttr.pageLinePitch > 0) {
                        value = Math.max(paraAttr.lineSpaceValue, pageAttr.pageLinePitch);
                    } else {
                        value = paraAttr.lineSpaceValue;
                    }
                } else {
                    if (pageAttr.pageLinePitch > 0) {
                        processline = true;
                        value = (float) (Math.ceil(heightExceptShape / pageAttr.pageLinePitch) * pageAttr.pageLinePitch);
                    } else {
                        value = heightExceptShape;
                    }
                }
                break;
                


            default:
                break;
        }

        if (processline) {
            value = (value - heightExceptShape) / 2;
            setTopIndent((int) value);
            setBottomIndent((int) value);
            setY(getY() + (int) value);
            if (bnView != null) {
                bnView.setTopIndent((int) value);
                bnView.setBottomIndent((int) value);
                bnView.setY((int) value);
            }
        }
    }


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {
        IView view = getView(offset, WPViewConstant.LEAF_VIEW, isBack);
        if (view != null) {
            view.modelToView(offset, rect, isBack);
        }
        rect.x += getX();
        rect.y += getY();
        return rect;
    }


    public long viewToModel(int x, int y, boolean isBack) {
        x -= getX();
        y -= getY();
        IView view = getView(x, y, WPViewConstant.LEAF_VIEW, isBack);
        if (view == null) {
            if (x > getWidth()) {
                view = getLastView();
            } else {
                view = getChildView();
            }
        }
        if (view != null) {
            return view.viewToModel(x, y, isBack);
        }
        return -1;
    }


    public void draw(Canvas canvas, int originX, int originY, float zoom) {
        canvas.save();
        IWord word = (IWord) getContainer();
        int dX = (int) (x * zoom) + originX;
        int dY = (int) (y * zoom) + originY;
        IView view = getChildView();
        Rect clip = canvas.getClipBounds();
        if (getTopIndent() < 0
                && word != null && word.getEditType() == MainConstant.APPLICATION_TYPE_WP) {
            canvas.clipRect(dX, dY - getTopIndent() * zoom, dX + getLayoutSpan(WPViewConstant.X_AXIS) * zoom,
                    dY - getTopIndent() * zoom + getLayoutSpan(WPViewConstant.Y_AXIS) * zoom);
        }
        while (view != null) {
            if (view.intersection(clip, dX, dY, zoom)) {
                view.draw(canvas, dX, dY, zoom);
            }
            view = view.getNextView();
        }
        canvas.restore();

        drawUnderline(canvas, originX, originY, zoom);

        if (word != null && word.getHighlight() != null) {
            word.getHighlight().draw(canvas, this, dX, dY, getStartOffset(null), getEndOffset(null), zoom);
        }
    }


    private void drawUnderline(Canvas canvas, int originX, int originY, float zoom) {
        Paint paint = new Paint();
        int dX = (int) (x * zoom) + originX;

        int dY = (int) (y * zoom + originY + getTopIndent() * zoom);
        LeafView leaf = (LeafView) getChildView();
        int w = 0;
        int color = Integer.MAX_VALUE;
        int baseline = 0;
        while (leaf != null) {
            CharAttr charAttr = leaf.getCharAttr();
            if (charAttr == null) {
                leaf = (LeafView) leaf.getNextView();
                continue;
            }
            if (charAttr.underlineType > 0) {

                if (color != Integer.MAX_VALUE && color != charAttr.underlineColor) {

                    paint.setColor(color);
                    canvas.drawRect(dX, dY + baseline + 1, dX + w, dY + baseline + 2, paint);
                    dX += w;


                    color = charAttr.underlineColor;
                    w = 0;
                    baseline = 0;
                } else if (color == Integer.MAX_VALUE) {
                    color = charAttr.underlineColor;
                }
                w += (int) (leaf.getWidth() * zoom);
                baseline = Math.max(baseline, (int) (leaf.getUnderlinePosition() * zoom));
            } else {
                if (color != Integer.MAX_VALUE) {

                    paint.setColor(color);
                    canvas.drawRect(dX, dY + baseline + 1, dX + w, dY + baseline + 2, paint);

                    dX += w;
                    w = 0;
                    baseline = 0;
                }
                dX += (int) (leaf.getWidth() * zoom);
                color = Integer.MAX_VALUE;
            }
            leaf = (LeafView) leaf.getNextView();
        }
        if (color != Integer.MAX_VALUE) {
            paint.setColor(color);
            canvas.drawRect(dX, dY + baseline + 1, dX + w, dY + baseline + 2, paint);
        }

    }


    public void free() {

    }


    public void dispose() {
        super.dispose();

    }

}
