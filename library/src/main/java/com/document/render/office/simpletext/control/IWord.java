
package com.document.render.office.simpletext.control;

import com.document.render.office.common.shape.IShape;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.pg.animate.IAnimation;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.system.IControl;


public interface IWord {

    public IHighlight getHighlight();


    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack);


    public IDocument getDocument();


    public String getText(long start, long end);


    public long viewToModel(int x, int y, boolean isBack);


    public byte getEditType();


    public IAnimation getParagraphAnimation(int pargraphID);


    public IShape getTextBox();


    public IControl getControl();


    public void dispose();
}
