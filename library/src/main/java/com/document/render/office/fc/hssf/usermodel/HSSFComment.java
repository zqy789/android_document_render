
package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.hssf.record.NoteRecord;
import com.document.render.office.fc.hssf.record.TextObjectRecord;
import com.document.render.office.fc.ss.usermodel.Comment;
import com.document.render.office.fc.ss.usermodel.RichTextString;



public class HSSFComment extends HSSFTextbox implements Comment {



    private boolean _visible;
    private int _row;
    private int _col;
    private String _author;

    private NoteRecord _note;
    private TextObjectRecord _txo;


    public HSSFComment(EscherContainerRecord escherContainer, HSSFShape parent, HSSFAnchor anchor) {
        super(escherContainer, parent, anchor);
        setShapeType(OBJECT_TYPE_COMMENT);


        setFillColor(0x08000050, 255);


        _visible = false;

        _author = "";
    }

    protected HSSFComment(NoteRecord note, TextObjectRecord txo) {
        this(null, (HSSFShape) null, (HSSFAnchor) null);
        _txo = txo;
        _note = note;
    }


    public boolean isVisible() {
        return _visible;
    }


    public void setVisible(boolean visible) {
        if (_note != null) {
            _note.setFlags(visible ? NoteRecord.NOTE_VISIBLE : NoteRecord.NOTE_HIDDEN);
        }
        _visible = visible;
    }


    public int getRow() {
        return _row;
    }


    public void setRow(int row) {
        if (_note != null) {
            _note.setRow(row);
        }
        _row = row;
    }


    public int getColumn() {
        return _col;
    }


    public void setColumn(int col) {
        if (_note != null) {
            _note.setColumn(col);
        }
        _col = col;
    }


    @Deprecated
    public void setColumn(short col) {
        setColumn((int) col);
    }


    public String getAuthor() {
        return _author;
    }


    public void setAuthor(String author) {
        if (_note != null) _note.setAuthor(author);
        this._author = author;
    }


    public void setString(RichTextString string) {
        HSSFRichTextString hstring = (HSSFRichTextString) string;

        if (hstring.numFormattingRuns() == 0) hstring.applyFont((short) 0);

        if (_txo != null) {
            _txo.setStr(hstring);
        }
        super.setString(string);
    }


    protected NoteRecord getNoteRecord() {
        return _note;
    }


    protected TextObjectRecord getTextObjectRecord() {
        return _txo;
    }
}
