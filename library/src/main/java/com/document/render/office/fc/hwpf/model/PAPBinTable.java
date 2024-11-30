

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.hwpf.model.io.HWPFFileSystem;
import com.document.render.office.fc.hwpf.model.io.HWPFOutputStream;
import com.document.render.office.fc.hwpf.sprm.SprmBuffer;
import com.document.render.office.fc.hwpf.sprm.SprmIterator;
import com.document.render.office.fc.hwpf.sprm.SprmOperation;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;



@Internal
public class PAPBinTable {

    protected ArrayList<PAPX> _paragraphs = new ArrayList<PAPX>();

    public PAPBinTable() {
    }


    @SuppressWarnings("unused")
    public PAPBinTable(byte[] documentStream, byte[] tableStream,
                       byte[] dataStream, int offset, int size, int fcMin,
                       TextPieceTable tpt) {
        this(documentStream, tableStream, dataStream, offset, size, tpt);
    }

    public PAPBinTable(byte[] documentStream, byte[] tableStream,
                       byte[] dataStream, int offset, int size,
                       CharIndexTranslator charIndexTranslator) {
        long start = System.currentTimeMillis();

        {
            PlexOfCps binTable = new PlexOfCps(tableStream, offset, size, 4);

            int length = binTable.length();
            for (int x = 0; x < length; x++) {
                GenericPropertyNode node = binTable.getProperty(x);

                int pageNum = LittleEndian.getInt(node.getBytes());
                int pageOffset = POIFSConstants.SMALLER_BIG_BLOCK_SIZE
                        * pageNum;

                PAPFormattedDiskPage pfkp = new PAPFormattedDiskPage(
                        documentStream, dataStream, pageOffset,
                        charIndexTranslator);

                int fkpSize = pfkp.size();

                for (int y = 0; y < fkpSize; y++) {
                    PAPX papx = pfkp.getPAPX(y);

                    if (papx != null)
                        _paragraphs.add(papx);
                }
            }
        }
    }

    public void rebuild(final StringBuilder docText,
                        ComplexFileTable complexFileTable) {
        long start = System.currentTimeMillis();

        if (complexFileTable != null) {
            SprmBuffer[] sprmBuffers = complexFileTable.getGrpprls();


            for (TextPiece textPiece : complexFileTable.getTextPieceTable()
                    .getTextPieces()) {
                PropertyModifier prm = textPiece.getPieceDescriptor().getPrm();
                if (!prm.isComplex())
                    continue;
                int igrpprl = prm.getIgrpprl();

                if (igrpprl < 0 || igrpprl >= sprmBuffers.length) {
                    continue;
                }

                boolean hasPap = false;
                SprmBuffer sprmBuffer = sprmBuffers[igrpprl];
                for (SprmIterator iterator = sprmBuffer.iterator(); iterator
                        .hasNext(); ) {
                    SprmOperation sprmOperation = iterator.next();
                    if (sprmOperation.getType() == SprmOperation.TYPE_PAP) {
                        hasPap = true;
                        break;
                    }
                }

                if (hasPap) {
                    SprmBuffer newSprmBuffer = new SprmBuffer(2);
                    newSprmBuffer.append(sprmBuffer.toByteArray());

                    PAPX papx = new PAPX(textPiece.getStart(),
                            textPiece.getEnd(), newSprmBuffer);
                    _paragraphs.add(papx);
                }
            }
        }

        List<PAPX> oldPapxSortedByEndPos = new ArrayList<PAPX>(_paragraphs);
        Collections.sort(oldPapxSortedByEndPos,
                PropertyNode.EndComparator.instance);

        start = System.currentTimeMillis();

        final Map<PAPX, Integer> papxToFileOrder = new IdentityHashMap<PAPX, Integer>();
        {
            int counter = 0;
            for (PAPX papx : _paragraphs) {
                papxToFileOrder.put(papx, Integer.valueOf(counter++));
            }
        }
        final Comparator<PAPX> papxFileOrderComparator = new Comparator<PAPX>() {
            public int compare(PAPX o1, PAPX o2) {
                Integer i1 = papxToFileOrder.get(o1);
                Integer i2 = papxToFileOrder.get(o2);
                return i1.compareTo(i2);
            }
        };
        start = System.currentTimeMillis();

        List<PAPX> newPapxs = new LinkedList<PAPX>();
        int lastParStart = 0;
        int lastPapxIndex = 0;
        for (int charIndex = 0; charIndex < docText.length(); charIndex++) {
            final char c = docText.charAt(charIndex);
            if (c != 13 && c != 7 && c != 12)
                continue;

            final int startInclusive = lastParStart;
            final int endExclusive = charIndex + 1;

            boolean broken = false;
            List<PAPX> papxs = new LinkedList<PAPX>();
            for (int papxIndex = lastPapxIndex; papxIndex < oldPapxSortedByEndPos
                    .size(); papxIndex++) {
                broken = false;
                PAPX papx = oldPapxSortedByEndPos.get(papxIndex);

                assert startInclusive == 0
                        || papxIndex + 1 == oldPapxSortedByEndPos.size()
                        || papx.getEnd() > startInclusive;

                if (papx.getEnd() - 1 > charIndex) {
                    lastPapxIndex = papxIndex;
                    broken = true;
                    break;
                }

                papxs.add(papx);
            }
            if (!broken) {
                lastPapxIndex = oldPapxSortedByEndPos.size() - 1;
            }

            if (papxs.size() == 0) {

                PAPX papx = new PAPX(startInclusive, endExclusive,
                        new SprmBuffer(2));
                newPapxs.add(papx);

                lastParStart = endExclusive;
                continue;
            }

            if (papxs.size() == 1) {

                PAPX existing = papxs.get(0);
                if (existing.getStart() == startInclusive
                        && existing.getEnd() == endExclusive) {
                    newPapxs.add(existing);
                    lastParStart = endExclusive;
                    continue;
                }
            }


            Collections.sort(papxs, papxFileOrderComparator);

            SprmBuffer sprmBuffer = null;
            for (PAPX papx : papxs) {
                if (sprmBuffer == null)
                    try {
                        sprmBuffer = (SprmBuffer) papx.getSprmBuf().clone();
                    } catch (CloneNotSupportedException e) {

                        throw new Error(e);
                    }
                else
                    sprmBuffer.append(papx.getGrpprl(), 2);
            }
            PAPX newPapx = new PAPX(startInclusive, endExclusive, sprmBuffer);
            newPapxs.add(newPapx);

            lastParStart = endExclusive;
            continue;
        }
        this._paragraphs = new ArrayList<PAPX>(newPapxs);

        start = System.currentTimeMillis();
    }

    public void insert(int listIndex, int cpStart, SprmBuffer buf) {

        PAPX forInsert = new PAPX(0, 0, buf);


        forInsert.setStart(cpStart);
        forInsert.setEnd(cpStart);

        if (listIndex == _paragraphs.size()) {
            _paragraphs.add(forInsert);
        } else {
            PAPX currentPap = _paragraphs.get(listIndex);
            if (currentPap != null && currentPap.getStart() < cpStart) {
                SprmBuffer clonedBuf = null;
                try {
                    clonedBuf = (SprmBuffer) currentPap.getSprmBuf().clone();
                } catch (CloneNotSupportedException exc) {
                    exc.printStackTrace();
                }






                PAPX clone = new PAPX(0, 0, clonedBuf);

                clone.setStart(cpStart);
                clone.setEnd(currentPap.getEnd());

                currentPap.setEnd(cpStart);

                _paragraphs.add(listIndex + 1, forInsert);
                _paragraphs.add(listIndex + 2, clone);
            } else {
                _paragraphs.add(listIndex, forInsert);
            }
        }

    }

    public void adjustForDelete(int listIndex, int offset, int length) {
        int size = _paragraphs.size();
        int endMark = offset + length;
        int endIndex = listIndex;

        PAPX papx = _paragraphs.get(endIndex);
        while (papx.getEnd() < endMark) {
            papx = _paragraphs.get(++endIndex);
        }
        if (listIndex == endIndex) {
            papx = _paragraphs.get(endIndex);
            papx.setEnd((papx.getEnd() - endMark) + offset);
        } else {
            papx = _paragraphs.get(listIndex);
            papx.setEnd(offset);
            for (int x = listIndex + 1; x < endIndex; x++) {
                papx = _paragraphs.get(x);
                papx.setStart(offset);
                papx.setEnd(offset);
            }
            papx = _paragraphs.get(endIndex);
            papx.setEnd((papx.getEnd() - endMark) + offset);
        }

        for (int x = endIndex + 1; x < size; x++) {
            papx = _paragraphs.get(x);
            papx.setStart(papx.getStart() - length);
            papx.setEnd(papx.getEnd() - length);
        }
    }


    public void adjustForInsert(int listIndex, int length) {
        int size = _paragraphs.size();
        PAPX papx = _paragraphs.get(listIndex);
        papx.setEnd(papx.getEnd() + length);

        for (int x = listIndex + 1; x < size; x++) {
            papx = _paragraphs.get(x);
            papx.setStart(papx.getStart() + length);
            papx.setEnd(papx.getEnd() + length);
        }
    }


    public ArrayList<PAPX> getParagraphs() {
        return _paragraphs;
    }

    @Deprecated
    public void writeTo(HWPFFileSystem sys, CharIndexTranslator translator)
            throws IOException {
        HWPFOutputStream wordDocumentStream = sys.getStream("WordDocument");
        HWPFOutputStream tableStream = sys.getStream("1Table");

        writeTo(wordDocumentStream, tableStream, translator);
    }

    public void writeTo(HWPFOutputStream wordDocumentStream,
                        HWPFOutputStream tableStream, CharIndexTranslator translator)
            throws IOException {

        PlexOfCps binTable = new PlexOfCps(4);


        int docOffset = wordDocumentStream.getOffset();
        int mod = docOffset % POIFSConstants.SMALLER_BIG_BLOCK_SIZE;
        if (mod != 0) {
            byte[] padding = new byte[POIFSConstants.SMALLER_BIG_BLOCK_SIZE - mod];
            wordDocumentStream.write(padding);
        }


        docOffset = wordDocumentStream.getOffset();
        int pageNum = docOffset / POIFSConstants.SMALLER_BIG_BLOCK_SIZE;




        int endingFc = translator.getByteIndex(_paragraphs.get(
                _paragraphs.size() - 1).getEnd());

        ArrayList<PAPX> overflow = _paragraphs;
        do {
            PAPX startingProp = overflow.get(0);


            int start = translator.getByteIndex(startingProp.getStart());

            PAPFormattedDiskPage pfkp = new PAPFormattedDiskPage();
            pfkp.fill(overflow);

            byte[] bufFkp = pfkp.toByteArray(tableStream, translator);
            wordDocumentStream.write(bufFkp);
            overflow = pfkp.getOverflow();

            int end = endingFc;
            if (overflow != null) {

                end = translator.getByteIndex(overflow.get(0).getStart());
            }

            byte[] intHolder = new byte[4];
            LittleEndian.putInt(intHolder, pageNum++);
            binTable.addProperty(new GenericPropertyNode(start, end, intHolder));

        }
        while (overflow != null);
        tableStream.write(binTable.toByteArray());
    }
}
