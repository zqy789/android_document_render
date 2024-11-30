

package com.document.render.office.fc.ss.usermodel;


public interface Comment {


    boolean isVisible();


    void setVisible(boolean visible);


    int getRow();


    void setRow(int row);


    int getColumn();


    void setColumn(int col);


    String getAuthor();


    void setAuthor(String author);


    public RichTextString getString();


    void setString(RichTextString string);

}
