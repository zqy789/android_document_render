
package com.document.render.office.common;

public interface ISlideShow {


    public static final byte SlideShow_Begin = 0;

    public static final byte SlideShow_Exit = SlideShow_Begin + 1;

    public static final byte SlideShow_PreviousStep = SlideShow_Exit + 1;

    public static final byte SlideShow_NextStep = SlideShow_PreviousStep + 1;

    public static final byte SlideShow_PreviousSlide = SlideShow_NextStep + 1;

    public static final byte SlideShow_NextSlide = SlideShow_PreviousSlide + 1;








    public void exit();
}
