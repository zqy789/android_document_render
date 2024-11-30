
package com.document.render.office.pg.control;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.document.render.office.common.ISlideShow;
import com.document.render.office.system.IControl;
import com.document.render.office.system.beans.AEventManage;


public class PGEventManage extends AEventManage {

    private Presentation presentation;


    public PGEventManage(Presentation presentation, IControl control) {
        super(presentation.getContext(), control);
        this.presentation = presentation;
        presentation.setOnTouchListener(this);
        presentation.setLongClickable(true);
    }


    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v, event);
        return false;
    }


    public boolean onDoubleTap(MotionEvent e) {
        super.onDoubleTap(e);
        return true;
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        super.onScroll(e1, e2, distanceX, distanceY);
        return true;
    }


    public void fling(int velocityX, int velocityY) {
        if (presentation.isSlideShow()) {
            if (Math.abs(velocityY) < 400 && Math.abs(velocityX) < 400) {
                presentation.slideShow(ISlideShow.SlideShow_NextStep);
                return;
            }

            super.fling(velocityX, velocityY);
            int currentIndex = presentation.getCurrentIndex();

            if (Math.abs(velocityY) > Math.abs(velocityX)) {

                if (velocityY < 0 && currentIndex >= 0) {

                    presentation.slideShow(ISlideShow.SlideShow_NextStep);
                } else if (velocityY > 0 && currentIndex <= presentation.getRealSlideCount() - 1) {

                    presentation.slideShow(ISlideShow.SlideShow_PreviousStep);
                }
            } else {

                if (velocityX < 0 && currentIndex >= 0) {

                    presentation.slideShow(ISlideShow.SlideShow_PreviousSlide);
                } else if (velocityX > 0 && currentIndex < presentation.getRealSlideCount() - 1) {

                    presentation.slideShow(ISlideShow.SlideShow_NextSlide);
                }
            }
        }

    }


    public boolean onSingleTapUp(MotionEvent e) {
        super.onSingleTapUp(e);
        if (e.getAction() == MotionEvent.ACTION_UP) {
            Rect drawRect = presentation.getSlideDrawingRect();
            if (presentation.isSlideShow() && drawRect.contains((int) e.getX(), (int) e.getY())) {

                this.presentation.slideShow(ISlideShow.SlideShow_NextStep);
            }
        }
        return true;
    }


    public void dispose() {
        super.dispose();
        presentation = null;
    }
}
