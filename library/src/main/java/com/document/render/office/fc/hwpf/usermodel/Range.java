

package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.fc.hwpf.HWPFDocument;
import com.document.render.office.fc.hwpf.HWPFDocumentCore;
import com.document.render.office.fc.hwpf.model.CHPX;
import com.document.render.office.fc.hwpf.model.FileInformationBlock;
import com.document.render.office.fc.hwpf.model.ListTables;
import com.document.render.office.fc.hwpf.model.PAPX;
import com.document.render.office.fc.hwpf.model.PropertyNode;
import com.document.render.office.fc.hwpf.model.SEPX;
import com.document.render.office.fc.hwpf.model.StyleSheet;
import com.document.render.office.fc.hwpf.model.SubdocumentType;
import com.document.render.office.fc.hwpf.model.TextPieceTable;
import com.document.render.office.fc.hwpf.sprm.CharacterSprmCompressor;
import com.document.render.office.fc.hwpf.sprm.ParagraphSprmCompressor;
import com.document.render.office.fc.hwpf.sprm.SprmBuffer;
import com.document.render.office.fc.util.LittleEndian;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.NoSuchElementException;



public class Range {



    public static final int TYPE_PARAGRAPH = 0;
    public static final int TYPE_CHARACTER = 1;
    public static final int TYPE_SECTION = 2;
    public static final int TYPE_TEXT = 3;
    public static final int TYPE_LISTENTRY = 4;
    public static final int TYPE_TABLE = 5;
    public static final int TYPE_UNDEFINED = 6;

    protected int _start;

    protected int _end;

    protected HWPFDocumentCore _doc;

    protected List<SEPX> _sections;

    protected int _sectionStart;

    protected int _sectionEnd;

    protected boolean _parRangeFound;

    protected List<PAPX> _paragraphs;

    protected int _parStart;

    protected int _parEnd;

    protected boolean _charRangeFound;

    protected List<CHPX> _characters;

    protected int _charStart;

    protected int _charEnd;
    protected StringBuilder _text;

    boolean _sectionRangeFound;

    private WeakReference<Range> _parent;







    public Range(int start, int end, HWPFDocumentCore doc) {
        _start = start;
        _end = end;
        _doc = doc;
        _sections = _doc.getSectionTable().getSections();
        _paragraphs = _doc.getParagraphTable().getParagraphs();
        _characters = _doc.getCharacterTable().getTextRuns();
        _text = _doc.getText();
        _parent = new WeakReference<Range>(null);

        sanityCheckStartEnd();
    }


    protected Range(int start, int end, Range parent) {
        _start = start;
        _end = end;
        _doc = parent._doc;
        _sections = parent._sections;
        _paragraphs = parent._paragraphs;
        _characters = parent._characters;
        _text = parent._text;
        _parent = new WeakReference<Range>(parent);

        sanityCheckStartEnd();
        assert sanityCheck();
    }


    @Deprecated
    protected Range(int startIdx, int endIdx, int idxType, Range parent) {
        _doc = parent._doc;
        _sections = parent._sections;
        _paragraphs = parent._paragraphs;
        _characters = parent._characters;
        _text = parent._text;
        _parent = new WeakReference<Range>(parent);

        sanityCheckStartEnd();
    }


    public static String stripFields(String text) {





        if (text.indexOf('\u0013') == -1)
            return text;



        while (text.indexOf('\u0013') > -1 && text.indexOf('\u0015') > -1) {
            int first13 = text.indexOf('\u0013');
            int next13 = text.indexOf('\u0013', first13 + 1);
            int first14 = text.indexOf('\u0014', first13 + 1);
            int last15 = text.lastIndexOf('\u0015');


            if (last15 < first13) {
                break;
            }


            if (next13 == -1 && first14 == -1) {
                text = text.substring(0, first13) + text.substring(last15 + 1);
                break;
            }




            if (first14 != -1 && (first14 < next13 || next13 == -1)) {
                text = text.substring(0, first13) + text.substring(first14 + 1, last15)
                        + text.substring(last15 + 1);
                continue;
            }




            text = text.substring(0, first13) + text.substring(last15 + 1);
            continue;
        }

        return text;
    }

    private static int binarySearchStart(List<? extends PropertyNode<?>> rpl, int start) {
        if (rpl.get(0).getStart() >= start)
            return 0;

        int low = 0;
        int high = rpl.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            PropertyNode<?> node = rpl.get(mid);

            if (node.getStart() < start) {
                low = mid + 1;
            } else if (node.getStart() > start) {
                high = mid - 1;
            } else {
                assert node.getStart() == start;
                return mid;
            }
        }
        assert low != 0;
        return low - 1;
    }

    private static int binarySearchEnd(List<? extends PropertyNode<?>> rpl, int foundStart,
                                       int end) {
        if (rpl.get(rpl.size() - 1).getEnd() <= end)
            return rpl.size() - 1;

        int low = foundStart;
        int high = rpl.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            PropertyNode<?> node = rpl.get(mid);

            if (node.getEnd() < end) {
                low = mid + 1;
            } else if (node.getEnd() > end) {
                high = mid - 1;
            } else {
                assert node.getEnd() == end;
                return mid;
            }
        }
        assert 0 <= low && low < rpl.size();

        return low;
    }


    private void sanityCheckStartEnd() {
        if (_start < 0) {
            throw new IllegalArgumentException("Range start must not be negative. Given " + _start);
        }
        if (_end < _start) {
            throw new IllegalArgumentException("The end (" + _end
                    + ") must not be before the start (" + _start + ")");
        }
    }


    @Deprecated
    public boolean usesUnicode() {
        return true;
    }


    public String text() {
        return _text.substring(_start, _end);
    }


    public int numSections() {
        initSections();
        return _sectionEnd - _sectionStart;
    }



    public int numParagraphs() {
        initParagraphs();
        return _parEnd - _parStart;
    }



    public int numCharacterRuns() {
        initCharacterRuns();
        return _charEnd - _charStart;
    }


    public CharacterRun insertBefore(String text) {
        initAll();

        _text.insert(_start, text);
        _doc.getCharacterTable().adjustForInsert(_charStart, text.length());
        _doc.getParagraphTable().adjustForInsert(_parStart, text.length());
        _doc.getSectionTable().adjustForInsert(_sectionStart, text.length());
        adjustForInsert(text.length());


        adjustFIB(text.length());

        assert sanityCheck();

        return getCharacterRun(0);
    }


    public CharacterRun insertAfter(String text) {
        initAll();

        _text.insert(_end, text);

        _doc.getCharacterTable().adjustForInsert(_charEnd - 1, text.length());
        _doc.getParagraphTable().adjustForInsert(_parEnd - 1, text.length());
        _doc.getSectionTable().adjustForInsert(_sectionEnd - 1, text.length());
        adjustForInsert(text.length());

        assert sanityCheck();
        return getCharacterRun(numCharacterRuns() - 1);
    }


    @Deprecated
    public CharacterRun insertBefore(String text, CharacterProperties props)

    {
        initAll();
        PAPX papx = _paragraphs.get(_parStart);
        short istd = papx.getIstd();

        StyleSheet ss = _doc.getStyleSheet();
        CharacterProperties baseStyle = ss.getCharacterStyle(istd);
        byte[] grpprl = CharacterSprmCompressor.compressCharacterProperty(props, baseStyle);
        SprmBuffer buf = new SprmBuffer(grpprl, 0);
        _doc.getCharacterTable().insert(_charStart, _start, buf);

        return insertBefore(text);
    }


    @Deprecated
    public CharacterRun insertAfter(String text, CharacterProperties props)

    {
        initAll();
        PAPX papx = _paragraphs.get(_parEnd - 1);
        short istd = papx.getIstd();

        StyleSheet ss = _doc.getStyleSheet();
        CharacterProperties baseStyle = ss.getCharacterStyle(istd);
        byte[] grpprl = CharacterSprmCompressor.compressCharacterProperty(props, baseStyle);
        SprmBuffer buf = new SprmBuffer(grpprl, 0);
        _doc.getCharacterTable().insert(_charEnd, _end, buf);
        _charEnd++;
        return insertAfter(text);
    }


    @Deprecated
    public Paragraph insertBefore(ParagraphProperties props, int styleIndex)

    {
        return this.insertBefore(props, styleIndex, "\r");
    }


    @Deprecated
    protected Paragraph insertBefore(ParagraphProperties props, int styleIndex, String text)

    {
        initAll();
        StyleSheet ss = _doc.getStyleSheet();
        ParagraphProperties baseStyle = ss.getParagraphStyle(styleIndex);
        CharacterProperties baseChp = ss.getCharacterStyle(styleIndex);

        byte[] grpprl = ParagraphSprmCompressor.compressParagraphProperty(props, baseStyle);
        byte[] withIndex = new byte[grpprl.length + LittleEndian.SHORT_SIZE];
        LittleEndian.putShort(withIndex, (short) styleIndex);
        System.arraycopy(grpprl, 0, withIndex, LittleEndian.SHORT_SIZE, grpprl.length);
        SprmBuffer buf = new SprmBuffer(withIndex, 2);

        _doc.getParagraphTable().insert(_parStart, _start, buf);
        insertBefore(text, baseChp);
        return getParagraph(0);
    }


    @Deprecated
    public Paragraph insertAfter(ParagraphProperties props, int styleIndex)

    {
        return this.insertAfter(props, styleIndex, "\r");
    }


    @Deprecated
    protected Paragraph insertAfter(ParagraphProperties props, int styleIndex, String text)

    {
        initAll();
        StyleSheet ss = _doc.getStyleSheet();
        ParagraphProperties baseStyle = ss.getParagraphStyle(styleIndex);
        CharacterProperties baseChp = ss.getCharacterStyle(styleIndex);

        byte[] grpprl = ParagraphSprmCompressor.compressParagraphProperty(props, baseStyle);
        byte[] withIndex = new byte[grpprl.length + LittleEndian.SHORT_SIZE];
        LittleEndian.putShort(withIndex, (short) styleIndex);
        System.arraycopy(grpprl, 0, withIndex, LittleEndian.SHORT_SIZE, grpprl.length);
        SprmBuffer buf = new SprmBuffer(withIndex, 2);

        _doc.getParagraphTable().insert(_parEnd, _end, buf);
        _parEnd++;
        insertAfter(text, baseChp);
        return getParagraph(numParagraphs() - 1);
    }

    public void delete() {

        initAll();

        int numSections = _sections.size();
        int numRuns = _characters.size();
        int numParagraphs = _paragraphs.size();

        for (int x = _charStart; x < numRuns; x++) {
            CHPX chpx = _characters.get(x);
            chpx.adjustForDelete(_start, _end - _start);
        }

        for (int x = _parStart; x < numParagraphs; x++) {
            PAPX papx = _paragraphs.get(x);


            papx.adjustForDelete(_start, _end - _start);


        }

        for (int x = _sectionStart; x < numSections; x++) {
            SEPX sepx = _sections.get(x);


            sepx.adjustForDelete(_start, _end - _start);


        }

        _text.delete(_start, _end);
        Range parent = _parent.get();
        if (parent != null) {
            parent.adjustForInsert(-(_end - _start));
        }


        adjustFIB(-(_end - _start));
    }


    @Deprecated
    public Table insertBefore(TableProperties props, int rows) {
        ParagraphProperties parProps = new ParagraphProperties();
        parProps.setFInTable(true);
        parProps.setItap(1);

        final int oldEnd = this._end;

        int columns = props.getItcMac();
        for (int x = 0; x < rows; x++) {
            Paragraph cell = this.insertBefore(parProps, StyleSheet.NIL_STYLE);
            cell.insertAfter(String.valueOf('\u0007'));
            for (int y = 1; y < columns; y++) {
                cell = cell.insertAfter(parProps, StyleSheet.NIL_STYLE);
                cell.insertAfter(String.valueOf('\u0007'));
            }
            cell = cell.insertAfter(parProps, StyleSheet.NIL_STYLE, String.valueOf('\u0007'));
            cell.setTableRowEnd(props);
        }

        final int newEnd = this._end;
        final int diff = newEnd - oldEnd;

        return new Table(_start, _start + diff, this, 1);
    }


    public Table insertTableBefore(short columns, int rows) {
        ParagraphProperties parProps = new ParagraphProperties();
        parProps.setFInTable(true);
        parProps.setItap(1);

        final int oldEnd = this._end;

        for (int x = 0; x < rows; x++) {
            Paragraph cell = this.insertBefore(parProps, StyleSheet.NIL_STYLE);
            cell.insertAfter(String.valueOf('\u0007'));
            for (int y = 1; y < columns; y++) {
                cell = cell.insertAfter(parProps, StyleSheet.NIL_STYLE);
                cell.insertAfter(String.valueOf('\u0007'));
            }
            cell = cell.insertAfter(parProps, StyleSheet.NIL_STYLE, String.valueOf('\u0007'));
            cell.setTableRowEnd(new TableProperties(columns));
        }

        final int newEnd = this._end;
        final int diff = newEnd - oldEnd;

        return new Table(_start, _start + diff, this, 1);
    }


    @Deprecated
    public ListEntry insertBefore(ParagraphProperties props, int listID, int level, int styleIndex) {
        ListTables lt = _doc.getListTables();
        if (lt.getLevel(listID, level) == null) {
            throw new NoSuchElementException("The specified list and level do not exist");
        }

        int ilfo = lt.getOverrideIndexFromListID(listID);
        props.setIlfo(ilfo);
        props.setIlvl((byte) level);

        return (ListEntry) insertBefore(props, styleIndex);
    }


    @Deprecated
    public ListEntry insertAfter(ParagraphProperties props, int listID, int level, int styleIndex) {
        ListTables lt = _doc.getListTables();
        if (lt.getLevel(listID, level) == null) {
            throw new NoSuchElementException("The specified list and level do not exist");
        }
        int ilfo = lt.getOverrideIndexFromListID(listID);
        props.setIlfo(ilfo);
        props.setIlvl((byte) level);

        return (ListEntry) insertAfter(props, styleIndex);
    }


    public void replaceText(String pPlaceHolder, String pValue, int pOffset) {
        int absPlaceHolderIndex = (int) getStartOffset() + pOffset;

        Range subRange = new Range(absPlaceHolderIndex,
                (absPlaceHolderIndex + pPlaceHolder.length()), this);
        subRange.insertBefore(pValue);


        subRange = new Range((absPlaceHolderIndex + pValue.length()), (absPlaceHolderIndex
                + pPlaceHolder.length() + pValue.length()), this);


        subRange.delete();
    }


    public void replaceText(String pPlaceHolder, String pValue) {
        boolean keepLooking = true;
        while (keepLooking) {

            String text = text();
            int offset = text.indexOf(pPlaceHolder);
            if (offset >= 0)
                replaceText(pPlaceHolder, pValue, offset);
            else
                keepLooking = false;
        }
    }


    public CharacterRun getCharacterRun(int index) {
        initCharacterRuns();

        if (index + _charStart >= _charEnd)
            throw new IndexOutOfBoundsException("CHPX #" + index + " (" + (index + _charStart)
                    + ") not in range [" + _charStart + "; " + _charEnd + ")");

        CHPX chpx = _characters.get(index + _charStart);

        if (chpx == null) {
            return null;
        }

        short istd;
        if (this instanceof Paragraph) {
            istd = ((Paragraph) this)._istd;
        } else {
            int[] point = findRange(_paragraphs, Math.max(chpx.getStart(), _start),
                    Math.min(chpx.getEnd(), _end));

            initParagraphs();
            int parStart = Math.max(point[0], _parStart);

            if (parStart >= _paragraphs.size()) {
                return null;
            }

            PAPX papx = _paragraphs.get(point[0]);
            istd = papx.getIstd();
        }

        CharacterRun chp = new CharacterRun(chpx, _doc.getStyleSheet(), istd, this);

        return chp;
    }


    public CharacterRun getCharacterRunByOffset(long offset) {
        int max = _characters.size();
        int min = 0;
        CHPX chpx = null;
        int start;
        int end;
        while (true) {
            int mid = (max + min) / 2;
            chpx = _characters.get(mid);
            start = chpx.getStart();
            end = chpx.getEnd();
            if (offset >= start && offset < end) {
                break;
            } else if (start > offset) {
                max = mid - 1;
            } else if (end <= offset) {
                min = mid + 1;
            }
        }

        short istd;
        if (this instanceof Paragraph) {
            istd = ((Paragraph) this)._istd;
        } else {
            int[] point = findRange(_paragraphs, Math.max(chpx.getStart(), _start),
                    Math.min(chpx.getEnd(), _end));

            initParagraphs();
            int parStart = Math.max(point[0], _parStart);

            if (parStart >= _paragraphs.size()) {
                return null;
            }

            PAPX papx = _paragraphs.get(point[0]);
            istd = papx.getIstd();
        }
        return new CharacterRun(chpx, _doc.getStyleSheet(), istd, this);
    }


    public Section getSection(int index) {
        initSections();
        if (index + _sectionStart < _sections.size()) {
            SEPX sepx = _sections.get(index + _sectionStart);
            Section sep = new Section(sepx, this);
            return sep;
        }

        return null;
    }



    public Paragraph getParagraph(int index) {
        initParagraphs();

        if (index + _parStart >= _parEnd)
            throw new IndexOutOfBoundsException("Paragraph #" + index + " (" + (index + _parStart)
                    + ") not in range [" + _parStart + "; " + _parEnd + ")");

        PAPX papx = _paragraphs.get(index + _parStart);

        ParagraphProperties props = papx.getParagraphProperties(_doc.getStyleSheet());
        Paragraph pap = null;
        if (props.getIlfo() > 0) {
            pap = new ListEntry(papx, this, _doc.getListTables());
        } else {
            if (((index + _parStart) == 0) && papx.getStart() > 0) {
                pap = new Paragraph(papx, this, 0);
            } else {
                pap = new Paragraph(papx, this);
            }
        }

        return pap;
    }


    public int type() {
        return TYPE_UNDEFINED;
    }


    public Table getTable(Paragraph paragraph) {
        if (!paragraph.isInTable()) {
            throw new IllegalArgumentException("This paragraph doesn't belong to a table");
        }

        Range r = paragraph;
        if (r._parent.get() != this) {
            throw new IllegalArgumentException(
                    "This paragraph is not a child of this range instance");
        }

        r.initAll();
        int tableLevel = paragraph.getTableLevel();
        int tableEndInclusive = r._parStart;

        if (r._parStart != 0) {
            Paragraph previous = new Paragraph(_paragraphs.get(r._parStart - 1), this);
            if (previous.isInTable() &&
                    previous.getTableLevel() == tableLevel
                    && previous._sectionEnd >= r._sectionStart) {
                throw new IllegalArgumentException(
                        "This paragraph is not the first one in the table");
            }
        }

        Range overallRange = _doc.getOverallRange();
        int limit = _paragraphs.size();
        for (; tableEndInclusive < limit - 1; tableEndInclusive++) {
            Paragraph next = new Paragraph(_paragraphs.get(tableEndInclusive + 1), overallRange);
            if (!next.isInTable() || next.getTableLevel() < tableLevel)
                break;
        }

        initAll();


        if (tableEndInclusive < 0) {
            throw new ArrayIndexOutOfBoundsException(
                    "The table's end is negative, which isn't allowed!");
        }

        int endOffsetExclusive = _paragraphs.get(tableEndInclusive).getEnd();

        return new Table((int) paragraph.getStartOffset(), endOffsetExclusive, this,
                paragraph.getTableLevel());
    }


    protected void initAll() {
        initCharacterRuns();
        initParagraphs();
        initSections();
    }


    private void initParagraphs() {
        if (!_parRangeFound) {
            int[] point = findRange(_paragraphs, _start, _end);
            _parStart = point[0];
            _parEnd = point[1];
            _parRangeFound = true;
        }
    }


    private void initCharacterRuns() {
        if (!_charRangeFound) {
            int[] point = findRange(_characters, _start, _end);
            _charStart = point[0];
            _charEnd = point[1];
            _charRangeFound = true;
        }
    }


    private void initSections() {
        if (!_sectionRangeFound) {
            int[] point = findRange(_sections, _sectionStart, _start, _end);
            _sectionStart = point[0];
            _sectionEnd = point[1];
            _sectionRangeFound = true;
        }
    }


    private int[] findRange(List<? extends PropertyNode<?>> rpl, int start, int end) {
        int startIndex = binarySearchStart(rpl, start);
        while (startIndex > 0 && rpl.get(startIndex - 1).getStart() >= start)
            startIndex--;

        int endIndex = binarySearchEnd(rpl, startIndex, end);
        while (endIndex < rpl.size() - 1 && rpl.get(endIndex + 1).getEnd() <= end)
            endIndex--;

        if (startIndex < 0 || startIndex >= rpl.size() || startIndex > endIndex || endIndex < 0
                || endIndex >= rpl.size())
            throw new AssertionError();

        return new int[]{startIndex, endIndex + 1};
    }


    private int[] findRange(List<? extends PropertyNode<?>> rpl, int min, int start, int end) {
        int x = min;

        if (rpl.size() == min)
            return new int[]{min, min};

        PropertyNode<?> node = rpl.get(x);

        while (node == null || (node.getEnd() <= start && x < rpl.size() - 1)) {
            x++;

            if (x >= rpl.size()) {
                return new int[]{0, 0};
            }

            node = rpl.get(x);
        }

        if (node.getStart() > end) {
            return new int[]{0, 0};
        }

        if (node.getEnd() <= start) {
            return new int[]{rpl.size(), rpl.size()};
        }

        for (int y = x; y < rpl.size(); y++) {
            node = rpl.get(y);
            if (node == null)
                continue;

            if (node.getStart() < end && node.getEnd() <= end)
                continue;

            if (node.getStart() < end)
                return new int[]{x, y + 1};

            return new int[]{x, y};
        }
        return new int[]{x, rpl.size()};
    }


    protected void reset() {
        _charRangeFound = false;
        _parRangeFound = false;
        _sectionRangeFound = false;
    }


    protected void adjustFIB(int adjustment) {
        assert (_doc instanceof HWPFDocument);








        FileInformationBlock fib = _doc.getFileInformationBlock();



























        int currentEnd = 0;
        for (SubdocumentType type : SubdocumentType.ORDERED) {
            int currentLength = fib.getSubdocumentTextStreamLength(type);
            currentEnd += currentLength;


            if (_start > currentEnd)
                continue;

            fib.setSubdocumentTextStreamLength(type, currentLength + adjustment);

            break;
        }
    }


    private void adjustForInsert(int length) {
        _end += length;

        reset();
        Range parent = _parent.get();
        if (parent != null) {
            parent.adjustForInsert(length);
        }
    }


    public int getStartOffset() {
        return _start;
    }


    public int getEndOffset() {
        return _end;
    }

    protected HWPFDocumentCore getDocument() {
        return _doc;
    }

    @Override
    public String toString() {
        return "Range from " + getStartOffset() + " to " + getEndOffset() + " (chars)";
    }


    public boolean sanityCheck() {
        if (_start < 0)
            throw new AssertionError();
        if (_start > _text.length())
            throw new AssertionError();
        if (_end < 0)
            throw new AssertionError();
        if (_end > _text.length())
            throw new AssertionError();
        if (_start > _end)
            throw new AssertionError();

        if (_charRangeFound) {
            for (int c = _charStart; c < _charEnd; c++) {
                CHPX chpx = _characters.get(c);

                int left = Math.max(this._start, chpx.getStart());
                int right = Math.min(this._end, chpx.getEnd());

                if (left >= right)
                    throw new AssertionError();
            }
        }
        if (_parRangeFound) {
            for (int p = _parStart; p < _parEnd; p++) {
                PAPX papx = _paragraphs.get(p);

                int left = Math.max(this._start, papx.getStart());
                int right = Math.min(this._end, papx.getEnd());

                if (left >= right)
                    throw new AssertionError();
            }
        }

        return true;
    }
}
