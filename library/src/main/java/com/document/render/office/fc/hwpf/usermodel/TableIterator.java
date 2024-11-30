

package com.document.render.office.fc.hwpf.usermodel;


public final class TableIterator {
    Range _range;
    int _index;
    int _levelNum;

    TableIterator(Range range, int levelNum) {
        _range = range;
        _index = 0;
        _levelNum = levelNum;
    }

    public TableIterator(Range range) {
        this(range, 1);
    }


    public boolean hasNext() {
        int numParagraphs = _range.numParagraphs();
        for (; _index < numParagraphs; _index++) {
            Paragraph paragraph = _range.getParagraph(_index);
            if (paragraph.isInTable() && paragraph.getTableLevel() == _levelNum) {
                return true;
            }
        }
        return false;
    }

    public Table next() {
        int numParagraphs = _range.numParagraphs();
        int startIndex = _index;
        int endIndex = _index;

        for (; _index < numParagraphs; _index++) {
            Paragraph paragraph = _range.getParagraph(_index);
            if (!paragraph.isInTable() || paragraph.getTableLevel() < _levelNum) {
                endIndex = _index;
                break;
            }
        }
        return new Table((int) _range.getParagraph(startIndex).getStartOffset(),
                (int) _range.getParagraph(endIndex - 1).getEndOffset(), _range,
                _levelNum);
    }

}
