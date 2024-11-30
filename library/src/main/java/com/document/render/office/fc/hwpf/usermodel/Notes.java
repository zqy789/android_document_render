
package com.document.render.office.fc.hwpf.usermodel;


public interface Notes {

    int getNoteAnchorPosition(int index);


    int getNotesCount();


    int getNoteIndexByAnchorPosition(int anchorPosition);


    int getNoteTextEndOffset(int index);


    int getNoteTextStartOffset(int index);
}
