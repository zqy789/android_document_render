
package com.document.render.office.pg.animate;



public class Animation implements IAnimation {

    public static final byte AnimStatus_NotStarted = 0;
    public static final byte AnimStatus_Animating = 1;
    public static final byte AnimStatus_End = 2;


    public static final byte FadeIn = 0;
    public static final byte FadeOut = 1;


    public static final int Duration = 1200;

    private static final int FPS = 15;
    protected ShapeAnimation shapeAnim;

    protected float duration;
    protected int fps;
    protected int delay;
    protected byte status;

    protected AnimationInformation begin;
    protected AnimationInformation end;
    protected AnimationInformation current;

    public Animation(ShapeAnimation shapeAnim) {
        this(shapeAnim, Duration, FPS);
    }

    public Animation(ShapeAnimation shapeAnim, int duration) {
        this(shapeAnim, duration, FPS);
    }

    public Animation(ShapeAnimation shapeAnim, int duration, int fps) {
        this.shapeAnim = shapeAnim;

        this.duration = duration;
        this.fps = fps;
        delay = 1000 / fps;

        status = AnimStatus_NotStarted;
    }


    public void start() {
        this.status = AnimStatus_Animating;
    }


    public void stop() {
        this.status = AnimStatus_End;
    }


    public AnimationInformation getCurrentAnimationInfor() {
        return current;
    }


    public ShapeAnimation getShapeAnimation() {
        return shapeAnim;
    }


    public int getDuration() {
        return (int) duration;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getFPS() {
        return fps;
    }


    public void animation(int frameIndex) {

    }


    public byte getAnimationStatus() {
        return status;
    }

    public void dispose() {
        shapeAnim = null;

        if (begin != null) {
            begin.dispose();
            begin = null;
        }

        if (end != null) {
            end.dispose();
            end = null;
        }

        if (current != null) {
            current.dispose();
            current = null;
        }
    }
}
