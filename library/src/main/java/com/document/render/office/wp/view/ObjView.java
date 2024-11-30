
package com.document.render.office.wp.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.document.render.office.common.BackgroundDrawer;
import com.document.render.office.common.picture.PictureKit;
import com.document.render.office.common.shape.PictureShape;
import com.document.render.office.common.shape.WPAutoShape;
import com.document.render.office.common.shape.WPPictureShape;
import com.document.render.office.common.shape.WatermarkShape;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.DocAttr;
import com.document.render.office.simpletext.view.PageAttr;
import com.document.render.office.simpletext.view.ParaAttr;
import com.document.render.office.simpletext.view.ViewKit;
import com.document.render.office.system.IControl;


public class ObjView extends LeafView {
    private PageAttr pageAttr;
    
    private WPAutoShape picShape;
    
    private Rect rect = new Rect();
    
    private boolean isInline;

    
    public ObjView() {

    }

    
    public ObjView(IElement paraElem, IElement elem, WPAutoShape shape) {
        super(paraElem, elem);
        this.picShape = shape;
    }

    
    public short getType() {
        return WPViewConstant.OBJ_VIEW;
    }

    
    public void initProperty(IElement elem, IElement paraElem) {
        this.elem = elem;
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(20);
    }

    
    public int doLayout(DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, int x, int y, int w, int h, long maxEnd, int flag) {
        this.pageAttr = pageAttr;

        isInline = docAttr.rootType == WPViewConstant.NORMAL_ROOT
                || (picShape.getWrap() != WPAutoShape.WRAP_TOP && picShape.getWrap() != WPAutoShape.WRAP_BOTTOM);

        if (picShape.isWatermarkShape()) {
            isInline = false;
        } else if (WPViewKit.instance().getArea(start + 1) == WPModelConstant.HEADER
                || WPViewKit.instance().getArea(start + 1) == WPModelConstant.FOOTER) {
            isInline = true;
        }
        int width = 0;
        Rectangle r = picShape.getBounds();
        if (isInline) {
            width = r.width;
            setSize(width, r.height);
        } else if (!picShape.isWatermarkShape()) {
            PositionLayoutKit.instance().processShapePosition(this, picShape, pageAttr);
        }
        setEndOffset(start + 1);
        boolean keepOne = ViewKit.instance().getBitValue(flag, WPViewConstant.LAYOUT_FLAG_KEEPONE);
        int breakType = WPViewConstant.BREAK_NO;
        if (keepOne) {
            return breakType;
        }
        if (width > w) {
            breakType = WPViewConstant.BREAK_LIMIT;
        }
        return breakType;
    }

    
    public float getTextWidth() {
        if (picShape.isWatermarkShape()) {
            return picShape.getBounds().width;
        } else {
            return isInline ? (int) ((WPPictureShape) picShape).getPictureShape().getBounds().getWidth() : 0;
        }
    }

    
    public synchronized void draw(Canvas canvas, int originX, int originY, float zoom) {
        if (isInline) {
            IControl control = getControl();
            int left = Math.round((x * zoom) + originX);
            int top = Math.round((y * zoom) + originY);
            int right = Math.round((x * zoom) + originX + getWidth() * zoom);
            int bottom = Math.round((y * zoom) + originY + getHeight() * zoom);

            rect.set(left, top, right, bottom);

            if (!picShape.isWatermarkShape()) {
                BackgroundDrawer.drawLineAndFill(canvas, control, getPageNumber(), ((WPPictureShape) picShape).getPictureShape(), rect, zoom);

                PictureKit.instance().drawPicture(canvas, control, getPageNumber(), ((WPPictureShape) picShape).getPictureShape().getPicture(getControl()),
                        left, top, zoom, getWidth() * zoom, getHeight() * zoom, ((WPPictureShape) picShape).getPictureShape().getPictureEffectInfor());
            }
        }

    }

    
    public synchronized void drawForWrap(Canvas canvas, int originX, int originY, float zoom) {
        int dX = (int) (x * zoom) + originX;
        int dY = (int) (y * zoom) + originY;
        Rectangle r = picShape.getBounds();
        IControl control = getControl();

        int left = Math.round((x * zoom) + originX);
        int top = Math.round((y * zoom) + originY);
        int right = (int) Math.round((x * zoom) + originX + r.getWidth() * zoom);
        int bottom = (int) Math.round((y * zoom) + originY + r.getHeight() * zoom);

        rect.set(left, top, right, bottom);

        if (picShape.isWatermarkShape()) {
            int mainBodyWidth = pageAttr.pageWidth - pageAttr.leftMargin - pageAttr.rightMargin;
            int mainBodyHeight = pageAttr.pageHeight - pageAttr.topMargin - pageAttr.bottomMargin;

            float centerX = originX + (pageAttr.leftMargin + mainBodyWidth / 2f) * zoom;
            float centerY = originY + (pageAttr.topMargin + mainBodyHeight / 2f) * zoom;

            left = Math.round(centerX - r.width * zoom / 2f);
            top = Math.round(centerY - r.height * zoom / 2f);
            PictureKit.instance().drawPicture(canvas, control, getPageNumber(),
                    PictureShape.getPicture(control, ((WatermarkShape) picShape).getPictureIndex()),
                    left,
                    top,
                    zoom,
                    Math.round(r.getWidth() * zoom),
                    Math.round(r.getHeight() * zoom),
                    ((WatermarkShape) picShape).getEffectInfor());
        } else {
            BackgroundDrawer.drawLineAndFill(canvas, control, getPageNumber(), ((WPPictureShape) picShape).getPictureShape(), rect, zoom);

            PictureKit.instance().drawPicture(canvas, control, getPageNumber(),
                    ((WPPictureShape) picShape).getPictureShape().getPicture(getControl()),
                    left,
                    top,
                    zoom,
                    Math.round(r.getWidth() * zoom),
                    Math.round(r.getHeight() * zoom),
                    ((WPPictureShape) picShape).getPictureShape().getPictureEffectInfor());
        }
    }

    
    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {
        rect.x += getX();
        rect.y += getY();
        return rect;
    }

    
    public long viewToModel(int x, int y, boolean isBack) {
        return start;
    }

    public boolean isBehindDoc() {
        return picShape.getWrap() == WPAutoShape.WRAP_BOTTOM;
    }

    
    public int getBaseline() {
        if (!picShape.isWatermarkShape()) {
            return isInline ? (int) ((WPPictureShape) picShape).getPictureShape().getBounds().getHeight() : 0;
        }

        return 0;
    }

    
    public boolean isInline() {
        return isInline;
    }

    
    public void free() {
        
    }

    
    public void dispose() {
        super.dispose();
        picShape = null;
    }
}
