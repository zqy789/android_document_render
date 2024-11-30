
package com.document.render.office.wp.control;

import com.document.render.office.common.shape.AbstractShape;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class WPShapeManage {

    private Map<Integer, AbstractShape> shapes;


    public WPShapeManage() {
        shapes = new HashMap<Integer, AbstractShape>(20);
    }


    public int addShape(AbstractShape shape) {
        int size = shapes.size();
        shapes.put(size, shape);
        return size;
    }


    public AbstractShape getShape(int index) {
        if (index < 0 || index >= shapes.size()) {
            return null;
        }
        return shapes.get(index);
    }


    public void dispose() {
        if (shapes != null) {
            Collection<AbstractShape> ass = shapes.values();
            if (ass != null) {
                for (AbstractShape as : ass) {
                    as.dispose();
                }
                shapes.clear();
            }
        }
    }

}
