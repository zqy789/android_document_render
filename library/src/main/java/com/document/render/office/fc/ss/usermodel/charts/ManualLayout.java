

package com.document.render.office.fc.ss.usermodel.charts;


public interface ManualLayout {


    public LayoutTarget getTarget();


    public void setTarget(LayoutTarget target);


    public LayoutMode getXMode();


    public void setXMode(LayoutMode mode);


    public LayoutMode getYMode();


    public void setYMode(LayoutMode mode);


    public double getX();


    public void setX(double x);



    public double getY();


    public void setY(double y);


    public LayoutMode getWidthMode();


    public void setWidthMode(LayoutMode mode);


    public LayoutMode getHeightMode();


    public void setHeightMode(LayoutMode mode);


    public double getWidthRatio();


    public void setWidthRatio(double ratio);


    public double getHeightRatio();


    public void setHeightRatio(double ratio);

}
