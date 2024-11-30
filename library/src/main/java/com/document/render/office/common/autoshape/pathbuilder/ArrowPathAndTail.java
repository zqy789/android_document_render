package com.document.render.office.common.autoshape.pathbuilder;

import android.graphics.Path;
import android.graphics.PointF;

public class ArrowPathAndTail {

    private Path path;

    private PointF tail;

    public void reset() {
        path = null;
        tail = null;
    }

    public Path getArrowPath() {
        return path;
    }

    public void setArrowPath(Path path) {
        this.path = path;
    }

    public PointF getArrowTailCenter() {
        return tail;
    }

    public void setArrowTailCenter(float x, float y) {
        this.tail = new PointF(x, y);
    }
}
