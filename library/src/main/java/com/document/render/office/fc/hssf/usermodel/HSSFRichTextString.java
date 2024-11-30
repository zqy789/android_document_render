

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.LabelSSTRecord;
import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.ss.usermodel.IFont;
import com.document.render.office.fc.ss.usermodel.RichTextString;

import java.util.Iterator;



public final class HSSFRichTextString implements Comparable<HSSFRichTextString>, RichTextString {

    public static final short NO_FONT = 0;

    private UnicodeString _string;
    private InternalWorkbook _book;
    private LabelSSTRecord _record;

    public HSSFRichTextString() {
        this("");
    }

    public HSSFRichTextString(String string) {
        if (string == null) {
            _string = new UnicodeString("");
        } else {
            _string = new UnicodeString(string);
        }
    }

    HSSFRichTextString(InternalWorkbook book, LabelSSTRecord record) {
        setWorkbookReferences(book, record);

        _string = book.getSSTString(record.getSSTIndex());
    }


    void setWorkbookReferences(InternalWorkbook book, LabelSSTRecord record) {
        _book = book;
        _record = record;
    }


    private UnicodeString cloneStringIfRequired() {
        if (_book == null)
            return _string;
        UnicodeString s = (UnicodeString) _string.clone();
        return s;
    }

    private void addToSSTIfRequired() {
        if (_book != null) {
            int index = _book.addSSTString(_string);
            _record.setSSTIndex(index);


            _string = _book.getSSTString(index);
        }
    }



    public void applyFont(int startIndex, int endIndex, short fontIndex) {
        if (startIndex > endIndex)
            throw new IllegalArgumentException("Start index must be less than end index.");
        if (startIndex < 0 || endIndex > length())
            throw new IllegalArgumentException("Start and end index not in range.");
        if (startIndex == endIndex)
            return;



        short currentFont = NO_FONT;
        if (endIndex != length()) {
            currentFont = this.getFontAtIndex(endIndex);
        }


        _string = cloneStringIfRequired();
        Iterator formatting = _string.formatIterator();
        if (formatting != null) {
            while (formatting.hasNext()) {
                UnicodeString.FormatRun r = (UnicodeString.FormatRun) formatting.next();
                if ((r.getCharacterPos() >= startIndex) && (r.getCharacterPos() < endIndex))
                    formatting.remove();
            }
        }


        _string.addFormatRun(new UnicodeString.FormatRun((short) startIndex, fontIndex));
        if (endIndex != length())
            _string.addFormatRun(new UnicodeString.FormatRun((short) endIndex, currentFont));

        addToSSTIfRequired();
    }


    public void applyFont(int startIndex, int endIndex, IFont font) {
        applyFont(startIndex, endIndex, ((HSSFFont) font).getIndex());
    }


    public void applyFont(IFont font) {
        applyFont(0, _string.getCharCount(), font);
    }


    public void clearFormatting() {
        _string = cloneStringIfRequired();
        _string.clearFormatting();
        addToSSTIfRequired();
    }


    public int getSSTIndex() {
        return _record.getSSTIndex();
    }


    public String getString() {
        return _string.getString();
    }


    public UnicodeString getUnicodeString() {
        return cloneStringIfRequired();
    }


    void setUnicodeString(UnicodeString str) {
        this._string = str;
    }


    UnicodeString getRawUnicodeString() {
        return _string;
    }


    public int length() {
        return _string.getCharCount();
    }


    public short getFontAtIndex(int index) {
        int size = _string.getFormatRunCount();
        UnicodeString.FormatRun currentRun = null;
        for (int i = 0; i < size; i++) {
            UnicodeString.FormatRun r = _string.getFormatRun(i);
            if (r.getCharacterPos() > index) {
                break;
            }
            currentRun = r;
        }
        if (currentRun == null) {
            return NO_FONT;
        }
        return currentRun.getFontIndex();
    }


    public int numFormattingRuns() {
        return _string.getFormatRunCount();
    }


    public int getIndexOfFormattingRun(int index) {
        UnicodeString.FormatRun r = _string.getFormatRun(index);
        return r.getCharacterPos();
    }


    public short getFontOfFormattingRun(int index) {
        UnicodeString.FormatRun r = _string.getFormatRun(index);
        return r.getFontIndex();
    }


    public int compareTo(HSSFRichTextString r) {
        return _string.compareTo(r._string);
    }

    public boolean equals(Object o) {
        if (o instanceof HSSFRichTextString) {
            return _string.equals(((HSSFRichTextString) o)._string);
        }
        return false;

    }


    public String toString() {
        return _string.toString();
    }


    public void applyFont(short fontIndex) {
        applyFont(0, _string.getCharCount(), fontIndex);
    }
}
