

package com.document.render.office.simpletext.control;

import android.graphics.Canvas;

import com.document.render.office.simpletext.view.IView;


public interface IHighlight {


    public void draw(Canvas canvas, IView line, int originX, int originY, long start, long end, float zoom);


    public String getSelectText();


    public boolean isSelectText();


    public void removeHighlight();


    public void addHighlight(long start, long end);


    public long getSelectStart();


    public void setSelectStart(long selectStart);


    public long getSelectEnd();


    public void setSelectEnd(long selectEnd);


    public void setPaintHighlight(boolean isPaintHighlight);


    public void dispose();

}
