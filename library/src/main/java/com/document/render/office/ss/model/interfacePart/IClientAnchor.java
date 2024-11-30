

package com.document.render.office.ss.model.interfacePart;


public interface IClientAnchor {

    public static final int MOVE_AND_RESIZE = 0;


    public static final int MOVE_DONT_RESIZE = 2;


    public static final int DONT_MOVE_AND_RESIZE = 3;


    public short getCol1();


    public void setCol1(int col1);


    public short getCol2();


    public void setCol2(int col2);


    public int getRow1();


    public void setRow1(int row1);


    public int getRow2();


    public void setRow2(int row2);


    public int getDx1();


    public void setDx1(int dx1);


    public int getDy1();


    public void setDy1(int dy1);


    public int getDy2();


    public void setDy2(int dy2);


    public int getDx2();


    public void setDx2(int dx2);


    public int getAnchorType();


    public void setAnchorType(int anchorType);

}
