package com.document.render.office.fc.doc;

import android.graphics.Path;

public class PathWithArrow {
    private Path startArrow;
    private Path endArrow;
    private Path[] polygon;

    public PathWithArrow(Path[] polygon, Path startArrow, Path endArrow) {
        this.polygon = polygon;
        this.startArrow = startArrow;
        this.endArrow = endArrow;
    }

    public Path getStartArrow() {
        return startArrow;
    }

    public Path getEndArrow() {
        return endArrow;
    }

    public Path[] getPolygonPath() {
        return polygon;
    }
}
