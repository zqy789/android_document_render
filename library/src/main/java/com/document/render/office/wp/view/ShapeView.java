
package com.document.render.office.wp.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.document.render.office.common.BackgroundDrawer;
import com.document.render.office.common.PaintKit;
import com.document.render.office.common.autoshape.AutoShapeKit;
import com.document.render.office.common.picture.PictureKit;
import com.document.render.office.common.shape.AbstractShape;
import com.document.render.office.common.shape.AutoShape;
import com.document.render.office.common.shape.GroupShape;
import com.document.render.office.common.shape.IShape;
import com.document.render.office.common.shape.PictureShape;
import com.document.render.office.common.shape.WPAutoShape;
import com.document.render.office.common.shape.WPChartShape;
import com.document.render.office.common.shape.WPGroupShape;
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
import com.document.render.office.thirdpart.achartengine.chart.AbstractChart;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;


public class ShapeView extends LeafView {

    private static final int GAP = 100;
    private PageAttr pageAttr;

    private WPAutoShape wpShape;

    private Rect rect = new Rect();

    private boolean isInline;

    private Map<Integer, WPSTRoot> roots;


    public ShapeView() {

    }


    public ShapeView(IElement paraElem, IElement elem, AutoShape shape) {
        super(paraElem, elem);
        wpShape = (WPAutoShape) shape;
        roots = new Hashtable<Integer, WPSTRoot>();
    }


    public short getType() {
        return WPViewConstant.SHAPE_VIEW;
    }


    public int doLayout(DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr, int x, int y, int w, int h, long maxEnd, int flag) {
        this.pageAttr = pageAttr;

        isInline = docAttr.rootType == WPViewConstant.NORMAL_ROOT
                || (wpShape.getWrap() != WPAutoShape.WRAP_TOP && wpShape.getWrap() != WPAutoShape.WRAP_BOTTOM);

        if (wpShape.isWatermarkShape()) {
            isInline = false;
        } else if ((WPViewKit.instance().getArea(start + 1) == WPModelConstant.HEADER
                || WPViewKit.instance().getArea(start + 1) == WPModelConstant.FOOTER)) {
            isInline = true;
        }

        int width = 0;
        Rectangle r = wpShape.getBounds();
        if (isInline) {
            width = r.width;
            setSize(width, r.height);
        } else {
            if (wpShape.isWatermarkShape()) {
                WatermarkShape watermark = (WatermarkShape) wpShape;

                paint = new Paint();
                paint.setAntiAlias(true);
                String str = watermark.getWatermartString();
                if (str != null && str.length() > 0) {
                    int len = str.length();
                    int span = pageAttr.pageWidth - pageAttr.leftMargin - pageAttr.rightMargin;

                    if (watermark.isAutoFontSize()) {
                        int fontSize = span / len;
                        paint.setTextSize(fontSize);
                        paint.getTextBounds(str, 0, len, rect);
                        int preFontSize = fontSize;
                        if (rect.width() < span) {
                            while (rect.width() < span) {
                                preFontSize = fontSize;
                                fontSize++;
                                paint.setTextSize(fontSize);
                                paint.getTextBounds(str, 0, len, rect);
                            }
                        } else if (rect.width() > span) {
                            while (rect.width() > span) {
                                preFontSize = fontSize;
                                fontSize--;
                                paint.setTextSize(fontSize);
                                paint.getTextBounds(str, 0, len, rect);
                            }
                        }

                        watermark.setFontSize(preFontSize);

                        paint.setTextSize(preFontSize);
                    } else {
                        paint.setTextSize(watermark.getFontSize());
                    }

                    paint.setColor(watermark.getFontColor());
                    int alpha = Math.round(255 * watermark.getOpacity());
                    paint.setAlpha(alpha);

                    paint.getTextBounds(str, 0, len, rect);
                    setX((pageAttr.pageWidth - rect.width()) / 2);
                    setY((pageAttr.pageHeight - rect.height()) / 2);
                }
            } else {
                PositionLayoutKit.instance().processShapePosition(this, wpShape, pageAttr);
            }

        }
        setEndOffset(start + 1);
        boolean keepOne = ViewKit.instance().getBitValue(flag, WPViewConstant.LAYOUT_FLAG_KEEPONE);
        int breakType = WPViewConstant.BREAK_NO;
        if (!keepOne && width > w) {
            breakType = WPViewConstant.BREAK_LIMIT;
        } else {
            layoutTextbox(wpShape, wpShape.getGroupShape());
        }
        return breakType;
    }


    private void layoutTextbox(WPAutoShape wpShape, WPGroupShape wpGroup) {
        if (wpGroup != null) {
            IShape[] shapes = wpGroup.getShapes();
            if (shapes != null) {
                for (IShape shape : shapes) {
                    if (shape.getType() == AbstractShape.SHAPE_GROUP) {
                        layoutTextbox(null, (WPGroupShape) shape);
                    } else if (shape instanceof WPAutoShape) {
                        layoutTextbox((WPAutoShape) shape, ((WPAutoShape) shape).getGroupShape());
                    }
                }
            }
        } else if (wpShape.getElementIndex() >= 0) {
            WPSTRoot stRoot = new WPSTRoot(getContainer(), getDocument(), wpShape.getElementIndex());
            stRoot.setWrapLine(wpShape.isTextWrapLine());
            stRoot.doLayout();
            stRoot.setParentView(this);
            roots.put(wpShape.getElementIndex(), stRoot);

            if (!wpShape.isTextWrapLine()) {

                wpShape.getBounds().width = stRoot.getAdjustTextboxWidth();
            }
        }
    }


    public float getTextWidth() {
        return isInline ? wpShape.getBounds().width : 0;
    }


    public synchronized void draw(Canvas canvas, int originX, int originY, float zoom) {
        if (isInline) {
            int dX = (int) (x * zoom) + originX;
            int dY = (int) (y * zoom) + originY;
            Rectangle r = wpShape.getBounds();
            rect.set(dX, dY, (int) (dX + r.width * zoom), (int) (dY + r.height * zoom));
            if (wpShape.getGroupShape() != null) {
                drawGroupShape(canvas, wpShape.getGroupShape(), rect, zoom);
            } else if (wpShape.getType() == AbstractShape.SHAPE_AUTOSHAPE) {
                AutoShapeKit.instance().drawAutoShape(canvas, getControl(), getPageNumber(), wpShape, rect, zoom);
            } else if (wpShape.getType() == AbstractShape.SHAPE_CHART) {
                AbstractChart chart = ((WPChartShape) wpShape).getAChart();
                chart.setZoomRate(zoom);
                chart.draw(canvas, getControl(), rect.left, rect.top, rect.width(), rect.height(), PaintKit.instance().getPaint());
            }

            if (roots.size() > 0 && wpShape.getElementIndex() >= 0) {
                WPSTRoot root = roots.get(wpShape.getElementIndex());
                if (root != null) {
                    canvas.save();
                    canvas.rotate(wpShape.getRotation(), rect.exactCenterX(), rect.exactCenterY());
                    root.draw(canvas, dX, dY, zoom);
                    canvas.restore();
                }
            }
        }
    }


    public synchronized void drawForWrap(Canvas canvas, int originX, int originY, float zoom) {
        try {
            int dX = (int) (x * zoom) + originX;
            int dY = (int) (y * zoom) + originY;
            Rectangle r = wpShape.getBounds();
            if (wpShape.isWatermarkShape()) {

                String str = ((WatermarkShape) wpShape).getWatermartString();
                if (str != null && str.length() > 0) {
                    canvas.save();

                    float oldSize = paint.getTextSize();
                    paint.setTextSize(((WatermarkShape) wpShape).getFontSize() * zoom);

                    float angle = wpShape.getRotation();

                    int mainBodyWidth = pageAttr.pageWidth - pageAttr.leftMargin - pageAttr.rightMargin;
                    int mainBodyHeight = pageAttr.pageHeight - pageAttr.topMargin - pageAttr.bottomMargin;

                    float centerX = originX + (pageAttr.leftMargin + mainBodyWidth / 2f) * zoom;
                    float centerY = originY + (pageAttr.topMargin + mainBodyHeight / 2f) * zoom;

                    canvas.translate(centerX, centerY);

                    canvas.rotate(angle, 0, 0);

                    canvas.drawText(str, -rect.width() * zoom / 2f, 0, paint);

                    paint.setTextSize(oldSize);
                    canvas.restore();
                    return;
                }
            } else {
                rect.set(dX, dY, (int) (dX + r.width * zoom), (int) (dY + r.height * zoom));
                if (wpShape.getGroupShape() != null) {

                    AutoShapeKit.instance().drawAutoShape(canvas, getControl(), getPageNumber(), wpShape, rect, zoom);
                    drawGroupShape(canvas, wpShape.getGroupShape(), rect, zoom);
                } else if (wpShape.getType() == AbstractShape.SHAPE_AUTOSHAPE) {
                    AutoShapeKit.instance().drawAutoShape(canvas, getControl(), getPageNumber(), wpShape, rect, zoom);
                } else if (wpShape.getType() == AbstractShape.SHAPE_CHART) {
                    AbstractChart chart = ((WPChartShape) wpShape).getAChart();
                    chart.setZoomRate(zoom);
                    chart.draw(canvas, getControl(), rect.left, rect.top, rect.width(), rect.height(), PaintKit.instance().getPaint());
                }
            }
            if (roots.size() > 0 && wpShape.getElementIndex() >= 0) {
                WPSTRoot root = roots.get(wpShape.getElementIndex());
                if (root != null) {
                    canvas.save();
                    canvas.rotate(wpShape.getRotation(), rect.exactCenterX(), rect.exactCenterY());
                    root.draw(canvas, dX, dY, zoom);
                    canvas.restore();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void drawGroupShape(Canvas canvas, GroupShape gs, Rect rect, float zoom) {
        if (gs != null) {
            IShape[] shapes = gs.getShapes();
            if (shapes != null) {
                Rect gsRect = new Rect();
                Rectangle r;
                for (IShape shape : shapes) {
                    if (shape.getType() == AbstractShape.SHAPE_GROUP) {
                        drawGroupShape(canvas, (GroupShape) shape, rect, zoom);
                    } else if (shape.getType() == AbstractShape.SHAPE_PICTURE) {
                        gsRect.setEmpty();
                        r = shape.getBounds();
                        gsRect.left = rect.left + (int) (r.x * zoom);
                        gsRect.top = rect.top + (int) (r.y * zoom);
                        gsRect.right = (int) (gsRect.left + r.width * zoom);
                        gsRect.bottom = (int) (gsRect.top + r.height * zoom);
                        if (shape instanceof WPPictureShape) {
                            shape = ((WPPictureShape) shape).getPictureShape();
                        }

                        if (shape != null) {
                            BackgroundDrawer.drawLineAndFill(canvas, getControl(), getPageNumber(),
                                    (PictureShape) shape, rect, zoom);

                            PictureKit.instance().drawPicture(canvas, getControl(), getPageNumber(), ((PictureShape) shape).getPicture(getControl()),
                                    gsRect.left, gsRect.top, zoom, shape.getBounds().width * zoom, shape.getBounds().height * zoom,
                                    ((PictureShape) shape).getPictureEffectInfor());
                        }
                    } else if (shape.getType() == AbstractShape.SHAPE_AUTOSHAPE) {
                        gsRect.setEmpty();
                        r = shape.getBounds();
                        gsRect.left = rect.left + (int) (r.x * zoom);
                        gsRect.top = rect.top + (int) (r.y * zoom);
                        gsRect.right = (int) (gsRect.left + r.width * zoom);
                        gsRect.bottom = (int) (gsRect.top + r.height * zoom);
                        AutoShapeKit.instance().drawAutoShape(canvas, getControl(), getPageNumber(), (AutoShape) shape, gsRect, zoom);
                        WPAutoShape txShape = (WPAutoShape) shape;
                        if (txShape.getElementIndex() >= 0) {
                            WPSTRoot root = roots.get(txShape.getElementIndex());
                            if (root != null) {
                                root.draw(canvas, gsRect.left, gsRect.top, zoom);
                            }
                        }
                    }
                }
            }
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


    public int getBaseline() {
        return isInline ? (int) wpShape.getBounds().getHeight() : 0;
    }

    public boolean isBehindDoc() {
        if (wpShape.getGroupShape() != null) {
            return wpShape.getGroupShape().getWrapType() == WPAutoShape.WRAP_BOTTOM;
        } else {
            return wpShape.getWrap() == WPAutoShape.WRAP_BOTTOM;
        }
    }


    public boolean isInline() {
        return isInline;
    }


    public void free() {

    }


    public void dispose() {
        super.dispose();
        if (roots != null) {
            Set<Integer> keySet = roots.keySet();
            for (Integer key : keySet) {
                WPSTRoot root = roots.get(key);
                if (root != null) {
                    root.dispose();
                }
            }
            roots.clear();
            roots = null;
        }
        wpShape = null;
    }

}
