

package com.document.render.office.fc.hslf.model;


import com.document.render.office.fc.hslf.exceptions.HSLFException;

import java.lang.reflect.Field;
import java.util.HashMap;


public final class ShapeTypes implements com.document.render.office.common.shape.ShapeTypes {
    public static HashMap types;

    static {
        types = new HashMap();
        try {
            Field[] f = com.document.render.office.common.shape.ShapeTypes.class.getFields();
            for (int i = 0; i < f.length; i++) {
                Object val = f[i].get(null);
                if (val instanceof Integer) {
                    types.put(val, f[i].getName());
                }
            }
        } catch (IllegalAccessException e) {
            throw new HSLFException("Failed to initialize shape types");
        }
    }


    public static String typeName(int type) {
        String name = (String) types.get(Integer.valueOf(type));
        return name;
    }

}
