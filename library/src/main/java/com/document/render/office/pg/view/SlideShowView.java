
package com.document.render.office.pg.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;

import com.document.render.office.common.shape.GroupShape;
import com.document.render.office.common.shape.IShape;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.pg.animate.Animation;
import com.document.render.office.pg.animate.AnimationManager;
import com.document.render.office.pg.animate.EmphanceAnimation;
import com.document.render.office.pg.animate.FadeAnimation;
import com.document.render.office.pg.animate.IAnimation;
import com.document.render.office.pg.animate.ShapeAnimation;
import com.document.render.office.pg.control.Presentation;
import com.document.render.office.pg.model.PGSlide;
import com.document.render.office.system.beans.CalloutView.CalloutView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SlideShowView {
    private Paint paint;
    private Rect bgRect;

    private Presentation presentation;

    private PGSlide slide;

    private int slideshowStep = 0;

    private AnimationManager animationMgr;

    private Map<Integer, Map<Integer, IAnimation>> shapeVisible;

    private Rect animShapeArea;
    private IAnimation pageAnimation;
    private int animDuration = Animation.Duration;


    public SlideShowView(Presentation presentation, PGSlide slide) {
        this.presentation = presentation;
        this.slide = slide;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setTextSize(24);

        bgRect = new Rect();
    }


    private void removeAnimation() {
        if (shapeVisible == null) {
            shapeVisible = new HashMap<Integer, Map<Integer, IAnimation>>();
        } else {
            shapeVisible.clear();
            slideshowStep = 0;
        }

        if (animationMgr != null) {
            animationMgr.stopAnimation();
        }

        if (presentation.getEditor() != null) {
            presentation.getEditor().clearAnimation();
        }

        if (slide != null) {
            int count = slide.getShapeCount();
            for (int i = 0; i < count; i++) {
                removeShapeAnimation(slide.getShape(i));
            }
        }
    }

    private void removeShapeAnimation(IShape shape) {
        if (shape instanceof GroupShape) {
            IShape[] shapes = ((GroupShape) shape).getShapes();
            for (IShape item : shapes) {
                removeShapeAnimation(item);
            }
        } else {
            IAnimation anim = shape.getAnimation();
            if (anim != null) {
                shape.setAnimation(null);
                anim.dispose();
                anim = null;
            }
        }


    }


    public void initSlideShow(PGSlide slide, boolean showAnimation) {

        removeAnimation();

        this.slide = slide;
        if (slide == null) {
            return;
        }

        List<ShapeAnimation> shapeAnimLst = slide.getSlideShowAnimation();
        if (shapeAnimLst != null) {
            int count = shapeAnimLst.size();
            for (int i = 0; i < count; i++) {
                ShapeAnimation shapeAnim = shapeAnimLst.get(i);
                Map<Integer, IAnimation> animMap = shapeVisible.get(shapeAnim.getShapeID());
                if (animMap == null) {
                    animMap = new HashMap<Integer, IAnimation>();
                    shapeVisible.put(shapeAnim.getShapeID(), animMap);
                }

                for (int para = shapeAnim.getParagraphBegin(); para <= shapeAnim.getParagraphEnd(); para++) {
                    IAnimation animation = animMap.get(para);
                    if (animation == null) {
                        animation = new FadeAnimation(shapeAnim, animDuration);
                        for (para = shapeAnim.getParagraphBegin(); para <= shapeAnim.getParagraphEnd(); para++) {
                            animMap.put(para, animation);
                        }
                        setShapeAnimation(shapeAnim.getShapeID(), animation);
                        break;
                    }
                }
            }
        }


        if (animationMgr == null) {
            animationMgr = presentation.getControl().getSysKit().getAnimationManager();
        }

        if (slide.hasTransition()) {
            if (pageAnimation == null) {
                pageAnimation = new FadeAnimation(new ShapeAnimation(ShapeAnimation.Slide, ShapeAnimation.SA_ENTR), animDuration);
            } else {
                pageAnimation.setDuration(animDuration);
            }

            animationMgr.setAnimation(pageAnimation);
            if (showAnimation) {
                animationMgr.beginAnimation(1000 / pageAnimation.getFPS());
            } else {
                animationMgr.stopAnimation();
            }
        }
    }


    private void setShapeAnimation(int shapeID, IAnimation animation) {
        int count = slide.getShapeCount();
        for (int i = 0; i < count; i++) {
            IShape shape = slide.getShape(i);
            if ((shape.getShapeID() == shapeID || shape.getGroupShapeID() == shapeID) && shape.getAnimation() == null) {
                setShapeAnimation(shape, animation);
            }
        }
    }

    private void setShapeAnimation(IShape shape, IAnimation animation) {
        if (shape instanceof GroupShape) {
            IShape[] shapes = ((GroupShape) shape).getShapes();
            for (IShape item : shapes) {
                setShapeAnimation(item, animation);
            }
        } else {
            shape.setAnimation(animation);
        }
    }

    public void endSlideShow() {

        removeAnimation();
    }


    public boolean isExitSlideShow() {
        return slide == null;
    }


    public boolean gotopreviousSlide() {
        List<ShapeAnimation> shapeAnimLst = slide.getSlideShowAnimation();
        if (shapeAnimLst != null) {
            return slideshowStep <= 0;
        }

        return true;
    }


    public boolean gotoNextSlide() {
        List<ShapeAnimation> shapeAnimLst = slide.getSlideShowAnimation();
        if (shapeAnimLst != null) {
            return slideshowStep >= shapeAnimLst.size();
        }

        return true;
    }


    public void previousActionSlideShow() {
        int oldSlideshowStep = slideshowStep - 1;
        initSlideShow(slide, false);
        while (slideshowStep < oldSlideshowStep) {
            slideshowStep = slideshowStep + 1;
            updateShapeAnimation(slideshowStep, false);
            ;
        }
    }


    public void nextActionSlideShow() {
        slideshowStep = slideshowStep + 1;

        updateShapeAnimation(slideshowStep, true);
    }


    public void gotoLastAction() {
        while (!gotoNextSlide()) {
            slideshowStep = slideshowStep + 1;

            updateShapeAnimation(slideshowStep, false);
        }
    }


    private void updateShapeAnimation(int curSlideShowStep, boolean showAnimation) {
        List<ShapeAnimation> shapeAnimLst = slide.getSlideShowAnimation();
        if (shapeAnimLst != null) {
            ShapeAnimation shapeAnim = shapeAnimLst.get(curSlideShowStep - 1);

            updateShapeArea(shapeAnim.getShapeID(), presentation.getZoom());
            IAnimation animation;
            if (shapeAnim.getAnimationType() != ShapeAnimation.SA_EMPH) {
                animation = new FadeAnimation(shapeAnim, animDuration);
            } else {
                animation = new EmphanceAnimation(shapeAnim, animDuration);
            }

            Map<Integer, IAnimation> animMap = shapeVisible.get(shapeAnim.getShapeID());
            animMap.put(shapeAnim.getParagraphBegin(), animation);

            updateShapeAnimation(shapeAnim.getShapeID(), animation, showAnimation);
        }
    }


    private void updateShapeAnimation(int shapeID, IAnimation animation, boolean showAnimation) {
        animationMgr.setAnimation(animation);

        int count = slide.getShapeCount();
        for (int i = 0; i < count; i++) {
            IShape shape = slide.getShape(i);
            if ((shape.getShapeID() == shapeID || shape.getGroupShapeID() == shapeID)) {
                this.setShapeAnimation(shape, animation);
            }
        }

        if (showAnimation) {
            animationMgr.beginAnimation(1000 / animation.getFPS());
        } else {
            animationMgr.stopAnimation();
        }
    }

    private void updateShapeArea(int shapeID, float zoom) {
        int count = slide.getShapeCount();
        for (int i = 0; i < count; i++) {
            IShape shape = slide.getShape(i);
            if (shape.getShapeID() == shapeID) {
                Rectangle shapeRect = shape.getBounds();
                if (shapeRect != null) {
                    int left = Math.round(shapeRect.x * zoom);
                    int top = Math.round(shapeRect.y * zoom);
                    int width = Math.round(shapeRect.width * zoom);
                    int height = Math.round(shapeRect.height * zoom);

                    if (animShapeArea == null) {
                        animShapeArea = new Rect(left, top, left + width, top + height);
                    } else {
                        animShapeArea.set(left, top, left + width, top + height);
                    }


                    return;
                }
            }
        }

        animShapeArea = null;
    }


    public void changeSlide(PGSlide slide) {
        this.slide = slide;
    }


    public Rect getDrawingRect() {
        return bgRect;
    }


    public void drawSlide(Canvas canvas, float zoom, CalloutView callouts) {
        if (pageAnimation != null && pageAnimation.getAnimationStatus() != Animation.AnimStatus_End) {

            zoom *= pageAnimation.getCurrentAnimationInfor().getProgress();
            if (zoom <= 0.001f) {
                return;
            }
        }

        Dimension d = presentation.getPageSize();
        int w = (int) (d.width * zoom);
        int h = (int) (d.height * zoom);
        int x = (presentation.getmWidth() - w) / 2;
        int y = (presentation.getmHeight() - h) / 2;

        canvas.save();
        canvas.translate(x, y);
        canvas.clipRect(0, 0, w, h);
        bgRect.set(0, 0, w, h);

        SlideDrawKit.instance().drawSlide(canvas, presentation.getPGModel(), presentation.getEditor(), slide, zoom, shapeVisible);

        canvas.restore();

        if (callouts != null) {
            if (pageAnimation != null && pageAnimation.getAnimationStatus() != Animation.AnimStatus_End) {
                callouts.setVisibility(View.INVISIBLE);
            } else {
                callouts.setZoom(zoom);
                callouts.layout(x, y, x + w, y + h);
                callouts.setVisibility(View.VISIBLE);
            }
        }
    }


    public void drawSlideForToPicture(Canvas canvas, float zoom, int originBitmapW, int originBitmapH) {
        Rect rect = canvas.getClipBounds();
        if (rect.width() != originBitmapW || rect.height() != originBitmapH) {
            zoom *= Math.min((rect.width() / (float) originBitmapW), (rect.height() / (float) originBitmapH));
        }
        SlideDrawKit.instance().drawSlide(canvas, presentation.getPGModel(), presentation.getEditor(), slide, zoom, shapeVisible);
    }


    public boolean animationStoped() {
        if (animationMgr != null) {
            return animationMgr.hasStoped();
        }

        return true;
    }


    public void setAnimationDuration(int duration) {
        this.animDuration = duration;
    }


    public Bitmap getSlideshowToImage(PGSlide slide, int step) {
        this.slide = slide;
        initSlideShow(slide, false);
        while (slideshowStep < step - 1) {
            slideshowStep = slideshowStep + 1;
            updateShapeAnimation(slideshowStep, false);
            ;
        }

        Bitmap image = SlideDrawKit.instance().slideToImage(presentation.getPGModel(), presentation.getEditor(), slide, shapeVisible);


        removeAnimation();

        return image;
    }


    public void dispose() {
        paint = null;
        presentation = null;
        slide = null;

        if (animationMgr != null) {
            animationMgr.dispose();
            animationMgr = null;
        }

        if (shapeVisible != null) {
            shapeVisible.clear();
            shapeVisible = null;
        }
    }
}
