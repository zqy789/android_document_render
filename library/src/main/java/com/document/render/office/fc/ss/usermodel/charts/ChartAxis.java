

package com.document.render.office.fc.ss.usermodel.charts;


public interface ChartAxis {


    long getId();


    AxisPosition getPosition();


    void setPosition(AxisPosition position);


    String getNumberFormat();


    void setNumberFormat(String format);


    boolean isSetLogBase();


    double getLogBase();


    void setLogBase(double logBase);


    boolean isSetMinimum();


    double getMinimum();


    void setMinimum(double min);


    boolean isSetMaximum();


    double getMaximum();


    void setMaximum(double max);


    AxisOrientation getOrientation();


    void setOrientation(AxisOrientation orientation);


    AxisCrosses getCrosses();


    void setCrosses(AxisCrosses crosses);


    void crossAxis(ChartAxis axis);
}
