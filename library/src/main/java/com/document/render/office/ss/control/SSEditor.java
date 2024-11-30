
package com.document.render.office.ss.control;

import com.document.render.office.common.shape.IShape;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.pg.animate.IAnimation;
import com.document.render.office.simpletext.control.IHighlight;
import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.system.IControl;


public class SSEditor implements IWord {

    private Spreadsheet ss;

    public SSEditor(Spreadsheet ss) {
        this.ss = ss;
    }

    public IHighlight getHighlight() {

        return null;
    }

    @Override
    public Rectangle modelToView(long offset, Rectangle rect, boolean isBack) {

        return null;
    }

    @Override
    public IDocument getDocument() {

        return null;
    }

    @Override
    public String getText(long start, long end) {

        return "";
    }

    @Override
    public long viewToModel(int x, int y, boolean isBack) {

        return 0;
    }

    @Override
    public byte getEditType() {
        return MainConstant.APPLICATION_TYPE_SS;
    }

    @Override
    public IAnimation getParagraphAnimation(int pargraphID) {

        return null;
    }

    @Override
    public IShape getTextBox() {

        return null;
    }

    @Override
    public IControl getControl() {

        return ss.getControl();
    }

    @Override
    public void dispose() {
        ss = null;
    }
}
