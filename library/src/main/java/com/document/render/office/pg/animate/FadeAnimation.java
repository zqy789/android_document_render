
package com.document.render.office.pg.animate;


public class FadeAnimation extends Animation {
    public FadeAnimation(ShapeAnimation shapeAnim) {
        super(shapeAnim);

        initAnimationKeyPoint();
    }

    public FadeAnimation(ShapeAnimation shapeAnim, int duration) {
        super(shapeAnim, duration);

        initAnimationKeyPoint();
    }

    public FadeAnimation(ShapeAnimation shapeAnim, int duration, int fps) {
        super(shapeAnim, duration, fps);

        initAnimationKeyPoint();
    }


    private void initAnimationKeyPoint() {
        if (shapeAnim != null) {
            begin = new AnimationInformation(null, (shapeAnim.getAnimationType() == ShapeAnimation.SA_ENTR) ? 0 : 255, 0);
            end = new AnimationInformation(null, (shapeAnim.getAnimationType() == ShapeAnimation.SA_ENTR) ? 255 : 0, 0);
            current = new AnimationInformation(null, (shapeAnim.getAnimationType() == ShapeAnimation.SA_ENTR) ? 0 : 255, 0);
        } else {
            begin = new AnimationInformation(null, 0, 0);
            end = new AnimationInformation(null, 255, 0);
            current = new AnimationInformation(null, 0, 0);
        }
    }


    public void animation(int frameIndex) {
        if (shapeAnim != null && current != null) {
            switch (shapeAnim.getAnimationType()) {
                case ShapeAnimation.SA_ENTR:
                    fadeIn(frameIndex * delay);
                    break;

                case ShapeAnimation.SA_EMPH:
                    fadeIn(frameIndex * delay);
                    break;

                case ShapeAnimation.SA_EXIT:
                    fadeOut(frameIndex * delay);
                    break;

                default:
                    break;
            }
        }
    }


    public void start() {
        super.start();
        current.setProgress(0f);
    }


    public void stop() {
        super.stop();

        if (current != null) {
            current.setAngle(0);
            current.setProgress(1f);

            if (shapeAnim != null) {
                switch (shapeAnim.getAnimationType()) {
                    case ShapeAnimation.SA_ENTR:
                        current.setAlpha(255);
                        break;

                    case ShapeAnimation.SA_EXIT:
                        current.setAlpha(0);
                        break;

                    default:
                        break;
                }
            }
        }
    }


    private void fadeIn(int time) {
        if (time < duration) {
            float progress = time / duration;
            current.setProgress(progress);
            current.setAlpha((int) (255 * progress));

        } else {
            status = Animation.AnimStatus_End;
            current.setProgress(1f);
            current.setAlpha(255);

        }
    }


    private void fadeOut(int time) {
        if (time < duration) {
            float progress = time / duration;
            current.setProgress(progress);
            current.setAlpha((int) (255 * (1 - progress)));

        } else {
            status = Animation.AnimStatus_End;
            current.setProgress(1f);
            current.setAlpha(0);

        }
    }

}
