
package com.document.render.office.common.shape;

import com.document.render.office.common.autoshape.ExtendPath;

import java.util.ArrayList;
import java.util.List;


public class ArbitraryPolygonShape extends LineShape {

    private List<ExtendPath> paths;

    public ArbitraryPolygonShape() {
        paths = new ArrayList<ExtendPath>();
    }

    public void appendPath(ExtendPath path) {
        this.paths.add(path);
    }

    public List<ExtendPath> getPaths() {
        return paths;
    }
}
