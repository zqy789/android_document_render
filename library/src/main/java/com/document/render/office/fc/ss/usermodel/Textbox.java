

package com.document.render.office.fc.ss.usermodel;

public interface Textbox {

    public final static short OBJECT_TYPE_TEXT = 6;


    RichTextString getString();


    void setString(RichTextString string);


    int getMarginLeft();


    void setMarginLeft(int marginLeft);


    int getMarginRight();


    void setMarginRight(int marginRight);


    int getMarginTop();


    void setMarginTop(int marginTop);


    int getMarginBottom();


    void setMarginBottom(int marginBottom);

}
