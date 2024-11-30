
package com.document.render.office.common.shape;

import java.util.ArrayList;
import java.util.List;


public class SmartArt extends AbstractShape {

    private int offX, offY;

    private List<IShape> shapes;


    public SmartArt() {
        shapes = new ArrayList<IShape>();
    }


    public short getType() {
        return SHAPE_SMARTART;
    }


    public void appendShapes(IShape shape) {
        this.shapes.add(shape);
    }


    public IShape[] getShapes() {
        return shapes.toArray(new IShape[shapes.size()]);
    }
}
