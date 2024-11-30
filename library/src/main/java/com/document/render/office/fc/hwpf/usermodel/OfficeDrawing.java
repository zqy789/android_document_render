
package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.common.pictureefftect.PictureEffectInfo;
import com.document.render.office.system.IControl;


public interface OfficeDrawing {


    public PictureEffectInfo getPictureEffectInfor();


    public byte getHorizontalPositioning();


    public byte getHorizontalRelative();


    byte[] getPictureData(IControl control);

    public byte[] getPictureData(IControl control, int index);

    public HWPFShape getAutoShape();


    int getRectangleBottom();


    int getRectangleLeft();


    int getRectangleRight();


    int getRectangleTop();


    int getShapeId();


    public int getWrap();


    public boolean isBelowText();


    public boolean isAnchorLock();


    public String getTempFilePath(IControl control);


    public byte getVerticalPositioning();


    public byte getVerticalRelativeElement();

    public enum HorizontalPositioning {


        ABSOLUTE,


        CENTER,


        INSIDE,


        LEFT,


        OUTSIDE,


        RIGHT;
    }

    public enum HorizontalRelativeElement {
        CHAR, MARGIN, PAGE, TEXT;
    }

    public enum VerticalPositioning {


        ABSOLUTE,


        BOTTOM,


        CENTER,


        INSIDE,


        OUTSIDE,


        TOP;
    }

    public enum VerticalRelativeElement {
        LINE, MARGIN, PAGE, TEXT;
    }

}
