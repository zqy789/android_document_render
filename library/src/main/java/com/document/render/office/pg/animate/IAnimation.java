package com.document.render.office.pg.animate;

import android.graphics.Rect;

public interface IAnimation {

    public byte getAnimationStatus();


    public void start();


    public void stop();


    public void animation(int frameIndex);


    public AnimationInformation getCurrentAnimationInfor();


    public ShapeAnimation getShapeAnimation();


    public int getDuration();


    public void setDuration(int duration);


    public int getFPS();

    public void dispose();

    public class AnimationInformation {

        public static final int ROTATION = 720;
        private int alpha;
        private int angle;
        private Rect postion;
        private float progress;

        public AnimationInformation(Rect postion, int alpha, int angle) {
            if (postion != null) {
                this.postion = new Rect(postion);
            }

            this.alpha = alpha;
            this.angle = angle;
        }


        public void setAnimationInformation(Rect postion, int alpha, int angle) {
            if (postion != null) {
                this.postion = new Rect(postion);
            }
            this.alpha = alpha;
            this.angle = angle;

            progress = 0;
        }


        public float getProgress() {
            return progress;
        }


        public void setProgress(float progress) {
            this.progress = progress;
        }


        public Rect getPostion() {
            return postion;
        }


        public void setPostion(Rect postion) {
            if (postion != null) {
                this.postion = new Rect(postion);
            }
        }


        public int getAlpha() {
            return alpha;
        }

        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }


        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }


        public void dispose() {
            postion = null;
        }
    }
}
