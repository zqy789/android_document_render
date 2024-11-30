
package com.document.render.office.simpletext.view;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.system.IControl;


public interface IView {

    public IElement getElement();


    public void setElement(IElement elem);


    public short getType();

    public int getX();


    public void setX(int x);

    public int getY();


    public void setY(int y);

    public int getWidth();


    public void setWidth(int w);

    public int getHeight();


    public void setHeight(int h);


    public void setSize(int w, int h);


    public void setLocation(int x, int y);


    public void setBound(int x, int y, int w, int h);

    public int getTopIndent();


    public void setTopIndent(int top);

    public int getBottomIndent();


    public void setBottomIndent(int bottom);

    public int getLeftIndent();


    public void setLeftIndent(int left);

    public int getRightIndent();


    public void setRightIndent(int right);


    public void setIndent(int left, int top, int right, int bottom);


    public IWord getContainer();


    public IControl getControl();


    public IDocument getDocument();

    public IView getChildView();


    public void setChildView(IView view);

    public IView getParentView();


    public void setParentView(IView view);

    public IView getPreView();


    public void setPreView(IView view);

    public IView getNextView();


    public void setNextView(IView view);


    public IView getLastView();


    public int getChildCount();


    public void appendChlidView(IView view);


    public void insertView(IView view, IView newView);


    public void deleteView(IView view, boolean isDeleteChild);


    public void setStartOffset(long start);

    public long getStartOffset(IDocument doc);


    public void setEndOffset(long end);

    public long getEndOffset(IDocument doc);


    public long getElemStart(IDocument doc);


    public long getElemEnd(IDocument doc);


    public Rect getViewRect(int originX, int originY, float zoom);


    public boolean intersection(Rect rect, int originX, int originY, float zoom);


    public boolean contains(long offset, boolean isBack);


    public boolean contains(int x, int y, boolean isBack);


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack);


    public long viewToModel(int x, int y, boolean isBack);


    public long getNextForCoordinate(long offset, int dir, int x, int y);


    public long getNextForOffset(long offset, int dir, int x, int y);


    public void draw(Canvas canvas, int originX, int originY, float zoom);


    public int doLayout(int x, int y, int w, int h, long maxEnd, int flag);


    public int getLayoutSpan(byte axis);


    public IView getView(long offset, int type, boolean isBack);


    public IView getView(int x, int y, int type, boolean isBack);


    public void dispose();


    public void free();

}
