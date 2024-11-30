
package com.document.render.office.ss.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.document.render.office.common.BackgroundDrawer;
import com.document.render.office.common.PaintKit;
import com.document.render.office.common.autoshape.AutoShapeKit;
import com.document.render.office.common.picture.Picture;
import com.document.render.office.common.picture.PictureKit;
import com.document.render.office.common.shape.AChart;
import com.document.render.office.common.shape.AbstractShape;
import com.document.render.office.common.shape.AutoShape;
import com.document.render.office.common.shape.GroupShape;
import com.document.render.office.common.shape.IShape;
import com.document.render.office.common.shape.PictureShape;
import com.document.render.office.common.shape.SmartArt;
import com.document.render.office.common.shape.TextBox;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.STDocument;
import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.simpletext.view.STRoot;
import com.document.render.office.system.IControl;


public class ShapeView {


    private SheetView sheetView;

    private Rect shapeRect;
    private Rect temRect;



    public ShapeView(SheetView sheetView) {
        this.sheetView = sheetView;


        shapeRect = new Rect();
        temRect = new Rect();
    }


    public void panzoomViewRect(Rectangle shapePostion, IShape parent) {
        float zoom = sheetView.getZoom();
        if (parent != null && parent instanceof SmartArt) {
            shapeRect.left = Math.round((shapePostion.x) * zoom);
            shapeRect.right = Math.round((shapePostion.x + shapePostion.width) * zoom);
            shapeRect.top = Math.round((shapePostion.y) * zoom);
            shapeRect.bottom = Math.round((shapePostion.y + shapePostion.height) * zoom);
        } else {
            int x = sheetView.getRowHeaderWidth();
            int y = sheetView.getColumnHeaderHeight();
            float scrollX = sheetView.getScrollX();
            float scrollY = sheetView.getScrollY();

            shapeRect.left = x + Math.round((shapePostion.x - scrollX) * zoom);
            shapeRect.right = x + Math.round((shapePostion.x + shapePostion.width - scrollX) * zoom);
            shapeRect.top = y + Math.round((shapePostion.y - scrollY) * zoom);
            shapeRect.bottom = y + Math.round((shapePostion.y + shapePostion.height - scrollY) * zoom);
        }



        temRect.set(shapeRect.left, shapeRect.top, shapeRect.right, shapeRect.bottom);

    }


    public void draw(Canvas canvas) {
        Rect clip = canvas.getClipBounds();
        clip.left = sheetView.getRowHeaderWidth();
        clip.top = sheetView.getColumnHeaderHeight();

        int cnt = sheetView.getCurrentSheet().getShapeCount();
        IControl control = sheetView.getSpreadsheet().getControl();
        for (int i = 0; i < cnt && !sheetView.getSpreadsheet().isAbortDrawing(); i++) {
            IShape shape = sheetView.getCurrentSheet().getShape(i);
            drawShape(canvas, clip, control, null, shape);
        }
    }


    private void drawShape(Canvas canvas, Rect clip, IControl control, IShape parent, IShape shape) {
        canvas.save();

        Rectangle bounds = shape.getBounds();


        if (bounds == null && shape.getType() == AbstractShape.SHAPE_CHART) {
            DisplayMetrics display = sheetView.getSpreadsheet().getControl().getMainFrame()
                    .getActivity().getResources().getDisplayMetrics();
            int width = Math.max(display.widthPixels, display.heightPixels);
            int height = Math.min(display.widthPixels, display.heightPixels);
            bounds = new Rectangle(0, 0, (int) Math.round(width), (int) Math.round(height));
            shape.setBounds(bounds);
        }


        panzoomViewRect(bounds, parent);

        if (!temRect.intersect(clip) && parent == null) {
            return;
        }
        if (shape instanceof GroupShape) {

            if (shape.getFlipVertical()) {
                canvas.translate(shapeRect.left, shapeRect.bottom);
                canvas.scale(1, -1);
                canvas.translate(-shapeRect.left, -shapeRect.top);
            }

            if (shape.getFlipHorizontal()) {
                canvas.translate(shapeRect.right, shapeRect.top);
                canvas.scale(-1, 1);
                canvas.translate(-shapeRect.left, -shapeRect.top);
            }

            IShape[] shapes = ((GroupShape) shape).getShapes();
            for (int i = 0; i < shapes.length; i++) {
                IShape childShape = shapes[i];
                if (shape.isHidden()) {
                    continue;
                }
                drawShape(canvas, clip, control, shape, childShape);
            }
        } else {
            switch (shape.getType()) {
                case AbstractShape.SHAPE_PICTURE:
                    PictureShape pictureShape = (PictureShape) shape;
                    processRotation(canvas, pictureShape, shapeRect);

                    BackgroundDrawer.drawLineAndFill(canvas, control, sheetView.getSheetIndex(), pictureShape, shapeRect, sheetView.getZoom());

                    Picture pic = control.getSysKit().getPictureManage().getPicture(((PictureShape) shape).getPictureIndex());
                    PictureKit.instance().drawPicture(canvas, sheetView.getSpreadsheet().getControl(), sheetView.getSheetIndex(), pic, shapeRect.left, shapeRect.top,
                            sheetView.getZoom(), shapeRect.width(), shapeRect.height(), ((PictureShape) shape).getPictureEffectInfor());
                    break;

                case AbstractShape.SHAPE_TEXTBOX:
                    drawTextbox(canvas, shapeRect, (TextBox) shape);
                    break;

                case AbstractShape.SHAPE_CHART:
                    AChart achart = (AChart) shape;
                    if (achart.getAChart() != null) {
                        processRotation(canvas, shape, shapeRect);
                        achart.getAChart().setZoomRate(sheetView.getZoom());
                        achart.getAChart().draw(canvas, control, shapeRect.left, shapeRect.top, shapeRect.width(), shapeRect.height(), PaintKit.instance().getPaint());



                    }
                    break;

                case AbstractShape.SHAPE_LINE:
                case AbstractShape.SHAPE_AUTOSHAPE:
                    AutoShapeKit.instance().drawAutoShape(canvas, control, sheetView.getSheetIndex(), (AutoShape) shape, shapeRect, sheetView.getZoom());
                    break;

                case AbstractShape.SHAPE_SMARTART:
                    SmartArt smartArt = (SmartArt) shape;
                    BackgroundDrawer.drawLineAndFill(canvas, control, sheetView.getSheetIndex(), smartArt, shapeRect, sheetView.getZoom());

                    canvas.translate(shapeRect.left, shapeRect.top);


                    IShape[] shapes = smartArt.getShapes();
                    for (IShape item : shapes) {
                        drawShape(canvas, clip, control, smartArt, item);
                    }

                    break;
            }
        }

        canvas.restore();
    }


    private void drawTextbox(Canvas canvas, Rect shapeRect, TextBox textbox) {
        SectionElement elem = textbox.getElement();
        if (elem.getEndOffset() - elem.getStartOffset() == 0) {
            return;
        }
        if (textbox.isEditor()) {

            return;
        }

        processRotation(canvas, textbox, shapeRect);
        STRoot root = textbox.getRootView();
        if (root == null) {
            IDocument doc = new STDocument();
            doc.appendSection(elem);

            IAttributeSet attr = elem.getAttribute();

            AttrManage.instance().setPageWidth(attr, (int) Math.round(textbox.getBounds().getWidth() * MainConstant.PIXEL_TO_TWIPS));

            AttrManage.instance().setPageHeight(attr, (int) Math.round(textbox.getBounds().getHeight() * MainConstant.PIXEL_TO_TWIPS));

            root = new STRoot(sheetView.getSpreadsheet().getEditor(), doc);
            root.setWrapLine(textbox.isWrapLine());
            root.doLayout();

            textbox.setRootView(root);
        }

        if (root != null) {
            root.draw(canvas, shapeRect.left, shapeRect.top, sheetView.getZoom());
        }
    }


    private void processRotation(Canvas canvas, IShape shape, Rect shapeRect) {
        float angle = shape.getRotation();

        if (shape.getFlipVertical()) {
            angle += 180;
        }


        if (angle != 0) {
            canvas.rotate(angle, shapeRect.centerX(), shapeRect.centerY());
        }
    }


    public void dispose() {
        sheetView = null;
        shapeRect = null;
        temRect = null;
    }
}
