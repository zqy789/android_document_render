
package com.document.render.office.wp.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.simpletext.model.IElement;


public class EncloseCharacterView extends LeafView {

    protected Paint enclosePaint;

    protected Path path;


    public EncloseCharacterView() {

    }


    public EncloseCharacterView(IElement paraElem, IElement elem) {
        super(paraElem, elem);
    }


    public short getType() {
        return WPViewConstant.ENCLOSE_CHARACTER_VIEW;
    }


    public void initProperty(IElement elem, IElement paraElem) {
        super.initProperty(elem, paraElem);

        enclosePaint = new Paint();
        enclosePaint.setColor(charAttr.fontColor);
        enclosePaint.setStyle(Style.STROKE);
        enclosePaint.setAntiAlias(true);
        path = new Path();
    }


    public void draw(Canvas canvas, int originX, int originY, float zoom) {
        super.draw(canvas, originX, originY, zoom);

        drawEnclose(canvas, originX, originY, zoom);
    }


    private void drawEnclose(Canvas canvas, int originX, int originY, float zoom) {
        int dX = (int) (x * zoom) + originX;
        int dY = (int) (y * zoom) + originY;
        int w = (int) (getWidth() * zoom);
        int h = (int) (getHeight() * zoom);

        if (charAttr.encloseType == WPModelConstant.ENCLOSURE_TYPE_ROUND) {
            canvas.drawArc(new RectF(dX, dY, dX + w, dY + h), 0, 360, false, enclosePaint);
        }

        else if (charAttr.encloseType == WPModelConstant.ENCLOSURE_TYPE_SQUARE) {
            canvas.drawRect(dX, dY, dX + w, dY + h, enclosePaint);
        }

        else if (charAttr.encloseType == WPModelConstant.ENCLOSURE_TYPE_TRIANGLE) {
            path.reset();
            path.moveTo(dX + w / 2, dY);
            path.lineTo(dX, dY + h);
            path.lineTo(dX + w, dY + h);
            path.close();
            canvas.drawPath(path, enclosePaint);
        }

        else if (charAttr.encloseType == WPModelConstant.ENCLOSURE_TYPE_RHOMBUS) {
            path.reset();
            path.moveTo(dX + w / 2, dY);
            path.lineTo(dX, dY + h / 2);
            path.lineTo(dX + w / 2, dY + h);
            path.lineTo(dX + w, dY + h / 2);
            path.close();
            canvas.drawPath(path, enclosePaint);
        }
    }


    public void free() {

    }


    public void dispose() {
        super.dispose();
        paint = null;

    }
}
