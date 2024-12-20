/*
 * 文件名称:          PathExtend.java
 *
 * 编译器:            android2.2
 * 时间:              上午10:42:18
 */
package com.document.render.office.common.autoshape;

import android.graphics.Path;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            jqin
 * <p>
 * 日期:            2012-9-27
 * <p>
 * 负责人:           jqin
 * <p>
 * 负责小组:
 * <p>
 * <p>
 */
public class ExtendPath {
    private Path path;
    private BackgroundAndFill fill;
    private boolean hasLine;
    private Line line;
    private boolean isArrow;

    public ExtendPath() {
        path = new Path();
        fill = null;
    }

    public ExtendPath(ExtendPath extendPath) {
        path = new Path(extendPath.getPath());
        fill = extendPath.getBackgroundAndFill();
        hasLine = extendPath.hasLine();
        line = extendPath.getLine();
        isArrow = extendPath.isArrowPath();
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public BackgroundAndFill getBackgroundAndFill() {
        return fill;
    }

    public void setBackgroundAndFill(BackgroundAndFill fill) {
        this.fill = fill;
    }

    /*
     *
     */
    public boolean hasLine() {
        return hasLine;
    }

    /**
     * @return
     */
    public Line getLine() {
        return line;
    }

    /**
     * @param border
     */
    public void setLine(boolean hasLine) {
        this.hasLine = hasLine;
        if (hasLine && line == null) {
            line = new Line();
        }
    }

    /**
     *
     */
    public void setLine(Line line) {
        this.line = line;
        if (line != null) {
            hasLine = true;
        } else {
            hasLine = false;
        }
    }

    public void setArrowFlag(boolean isArrow) {
        this.isArrow = isArrow;
    }

    public boolean isArrowPath() {
        return isArrow;
    }

    public void dispose() {
        path = null;
        if (fill != null) {
            fill.dispose();
        }
    }
}
