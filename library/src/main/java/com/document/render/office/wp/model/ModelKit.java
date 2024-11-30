
package com.document.render.office.wp.model;

import com.document.render.office.constant.wp.WPModelConstant;


public class ModelKit {
    private static ModelKit kit = new ModelKit();


    public static ModelKit instance() {
        return kit;
    }


    public long getArea(long offset) {
        return offset & WPModelConstant.AREA_MASK;
    }

}
