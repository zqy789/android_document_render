

package com.document.render.office.fc.ss.usermodel;


public interface RichTextString {


    void applyFont(int startIndex, int endIndex, short fontIndex);


    void applyFont(int startIndex, int endIndex, IFont font);


    void applyFont(IFont font);


    void clearFormatting();


    String getString();


    int length();


    int numFormattingRuns();


    int getIndexOfFormattingRun(int index);


    void applyFont(short fontIndex);

}
