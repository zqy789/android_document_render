
package com.document.render.office.common.shape;

import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.simpletext.view.STRoot;


public class TextBox extends AbstractShape {

    public static final byte MC_None = 0;
    public static final byte MC_SlideNumber = 1;
    public static final byte MC_DateTime = 2;
    public static final byte MC_GenericDate = 3;
    public static final byte MC_Footer = 4;
    public static final byte MC_RTFDateTime = 5;
    private boolean isWrapLine;

    private boolean isEditor;

    private SectionElement elem;

    private STRoot rootView;
    private byte mcType = MC_None;
    private boolean isWordArt;

    public TextBox() {
        setPlaceHolderID(-1);
    }

    public short getType() {
        return SHAPE_TEXTBOX;
    }


    public SectionElement getElement() {
        return elem;
    }


    public void setElement(SectionElement section) {
        this.elem = section;
    }


    public Object getData() {
        return elem;
    }


    public void setData(Object data) {
        if (data instanceof SectionElement) {
            this.elem = (SectionElement) data;
        }
    }


    public boolean isEditor() {
        return isEditor;
    }


    public void setEditor(boolean isEditor) {
        this.isEditor = isEditor;
    }


    public STRoot getRootView() {
        return rootView;
    }


    public void setRootView(STRoot rootView) {
        this.rootView = rootView;
    }


    public boolean isWrapLine() {
        return isWrapLine;
    }


    public void setWrapLine(boolean isWrapLine) {
        this.isWrapLine = isWrapLine;
    }


    public byte getMCType() {
        return mcType;
    }


    public void setMCType(byte mcType) {
        this.mcType = mcType;
    }

    public boolean isWordArt() {
        return isWordArt;
    }

    public void setWordArt(boolean isWordArt) {
        this.isWordArt = isWordArt;
    }


    public void dispose() {
        super.dispose();
        if (elem != null) {
            elem.dispose();
            elem = null;
        }
    }
}
