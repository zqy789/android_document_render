
package com.document.render.office.common.shape;

import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.pg.animate.IAnimation;



public interface IShape {

    public int getGroupShapeID();


    public void setGroupShapeID(int id);


    public int getShapeID();


    public void setShapeID(int id);


    public short getType();


    public IShape getParent();


    public void setParent(IShape shape);


    public Rectangle getBounds();


    public void setBounds(Rectangle rect);


    public Object getData();


    public void setData(Object data);


    public boolean getFlipHorizontal();


    public void setFlipHorizontal(boolean flipH);


    public boolean getFlipVertical();


    public void setFlipVertical(boolean flipV);


    public float getRotation();


    public void setRotation(float angle);


    public boolean isHidden();


    public void setHidden(boolean hidden);


    public IAnimation getAnimation();


    public void setAnimation(IAnimation animation);


    public int getPlaceHolderID();


    public void setPlaceHolderID(int placeHolderID);


    public void dispose();

}
