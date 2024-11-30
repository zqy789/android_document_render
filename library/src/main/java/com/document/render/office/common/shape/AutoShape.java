
package com.document.render.office.common.shape;



public class AutoShape extends AbstractShape {

    private int type;

    private Float[] values;

    private boolean shape07 = true;


    public AutoShape() {

    }


    public AutoShape(int type) {
        this.type = type;
    }


    public short getType() {
        return SHAPE_AUTOSHAPE;
    }


    public int getShapeType() {
        return type;
    }


    public void setShapeType(int type) {
        this.type = type;
    }


    public Float[] getAdjustData() {
        return values;
    }


    public void setAdjustData(Float[] values) {
        this.values = values;
    }


    public void setAuotShape07(boolean shape07) {
        this.shape07 = shape07;
    }


    public boolean isAutoShape07() {
        return shape07;
    }


    public void dispose() {
        super.dispose();
    }
}
