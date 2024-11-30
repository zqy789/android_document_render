

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Internal
public class CHPBinTable {


    protected ArrayList<CHPX> _textRuns = new ArrayList<CHPX>();

    public CHPBinTable() {
    }


    public CHPBinTable(byte[] documentStream, byte[] tableStream, int offset, int size, int fcMin,
                       TextPieceTable tpt) {
        this(documentStream, tableStream, offset, size, tpt);
    }


    public CHPBinTable(byte[] documentStream, byte[] tableStream, int offset, int size,
                       CharIndexTranslator translator) {

        PlexOfCps bte = new PlexOfCps(tableStream, offset, size, 4);

        int length = bte.length();
        for (int x = 0; x < length; x++) {
            GenericPropertyNode node = bte.getProperty(x);

            int pageNum = LittleEndian.getInt(node.getBytes());
            int pageOffset = POIFSConstants.SMALLER_BIG_BLOCK_SIZE * pageNum;

            CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage(documentStream, pageOffset,
                    translator);

            int fkpSize = cfkp.size();

            for (int y = 0; y < fkpSize; y++) {
                final CHPX chpx = cfkp.getCHPX(y);
                if (chpx != null)
                    _textRuns.add(chpx);
            }
        }
    }

    private static int binarySearch(List<CHPX> chpxs, int startPosition) {
        int low = 0;
        int high = chpxs.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            CHPX midVal = chpxs.get(mid);
            int midValue = midVal.getStart();

            if (midValue < startPosition)
                low = mid + 1;
            else if (midValue > startPosition)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public void rebuild(ComplexFileTable complexFileTable) {
        long start = System.currentTimeMillis();

        if (complexFileTable != null) {
            SprmBuffer[] sprmBuffers = complexFileTable.getGrpprls();


            for (TextPiece textPiece : complexFileTable.getTextPieceTable().getTextPieces()) {
                PropertyModifier prm = textPiece.getPieceDescriptor().getPrm();
                if (!prm.isComplex())
                    continue;
                int igrpprl = prm.getIgrpprl();

                if (igrpprl < 0 || igrpprl >= sprmBuffers.length) {
                    continue;
                }

                boolean hasChp = false;
                SprmBuffer sprmBuffer = sprmBuffers[igrpprl];
                for (SprmIterator iterator = sprmBuffer.iterator(); iterator.hasNext(); ) {
                    SprmOperation sprmOperation = iterator.next();
                    if (sprmOperation.getType() == SprmOperation.TYPE_CHP) {
                        hasChp = true;
                        break;
                    }
                }

                if (hasChp) {
                    SprmBuffer newSprmBuffer;
                    try {
                        newSprmBuffer = (SprmBuffer) sprmBuffer.clone();
                    } catch (CloneNotSupportedException e) {

                        throw new Error(e);
                    }

                    CHPX chpx = new CHPX(textPiece.getStart(), textPiece.getEnd(), newSprmBuffer);
                    _textRuns.add(chpx);
                }
            }
        }

        List<CHPX> oldChpxSortedByStartPos = new ArrayList<CHPX>(_textRuns);
        Collections.sort(oldChpxSortedByStartPos, PropertyNode.StartComparator.instance);

        final Map<CHPX, Integer> chpxToFileOrder = new IdentityHashMap<CHPX, Integer>();
        {
            int counter = 0;
            for (CHPX chpx : _textRuns) {
                chpxToFileOrder.put(chpx, Integer.valueOf(counter++));
            }
        }
        final Comparator<CHPX> chpxFileOrderComparator = new Comparator<CHPX>() {
            public int compare(CHPX o1, CHPX o2) {
                Integer i1 = chpxToFileOrder.get(o1);
                Integer i2 = chpxToFileOrder.get(o2);
                return i1.compareTo(i2);
            }
        };


        List<Integer> textRunsBoundariesList;
        {
            Set<Integer> textRunsBoundariesSet = new HashSet<Integer>();
            for (CHPX chpx : _textRuns) {
                textRunsBoundariesSet.add(Integer.valueOf(chpx.getStart()));
                textRunsBoundariesSet.add(Integer.valueOf(chpx.getEnd()));
            }
            textRunsBoundariesSet.remove(Integer.valueOf(0));
            textRunsBoundariesList = new ArrayList<Integer>(textRunsBoundariesSet);
            Collections.sort(textRunsBoundariesList);
        }

        List<CHPX> newChpxs = new LinkedList<CHPX>();
        int lastTextRunStart = 0;
        for (Integer objBoundary : textRunsBoundariesList) {
            final int boundary = objBoundary.intValue();

            final int startInclusive = lastTextRunStart;
            final int endExclusive = boundary;
            lastTextRunStart = endExclusive;

            int startPosition = binarySearch(oldChpxSortedByStartPos, boundary);
            startPosition = Math.abs(startPosition);
            while (startPosition >= oldChpxSortedByStartPos.size())
                startPosition--;
            while (startPosition > 0
                    && oldChpxSortedByStartPos.get(startPosition).getStart() >= boundary)
                startPosition--;

            List<CHPX> chpxs = new LinkedList<CHPX>();
            for (int c = startPosition; c < oldChpxSortedByStartPos.size(); c++) {
                CHPX chpx = oldChpxSortedByStartPos.get(c);

                if (boundary < chpx.getStart())
                    break;

                int left = Math.max(startInclusive, chpx.getStart());
                int right = Math.min(endExclusive, chpx.getEnd());

                if (left < right) {
                    chpxs.add(chpx);
                }
            }

            if (chpxs.size() == 0) {

                CHPX chpx = new CHPX(startInclusive, endExclusive, new SprmBuffer(0));
                newChpxs.add(chpx);
                continue;
            }

            if (chpxs.size() == 1) {

                CHPX existing = chpxs.get(0);
                if (existing.getStart() == startInclusive && existing.getEnd() == endExclusive) {
                    newChpxs.add(existing);
                    continue;
                }
            }

            Collections.sort(chpxs, chpxFileOrderComparator);

            SprmBuffer sprmBuffer = new SprmBuffer(0);
            for (CHPX chpx : chpxs) {
                sprmBuffer.append(chpx.getGrpprl(), 0);
            }
            CHPX newChpx = new CHPX(startInclusive, endExclusive, sprmBuffer);
            newChpxs.add(newChpx);

            continue;
        }
        this._textRuns = new ArrayList<CHPX>(newChpxs);


        CHPX previous = null;
        for (Iterator<CHPX> iterator = _textRuns.iterator(); iterator.hasNext(); ) {
            CHPX current = iterator.next();
            if (previous == null) {
                previous = current;
                continue;
            }

            if (previous.getEnd() == current.getStart()
                    && Arrays.equals(previous.getGrpprl(), current.getGrpprl())) {
                previous.setEnd(current.getEnd());
                iterator.remove();
                continue;
            }

            previous = current;
        }
    }

    public void adjustForDelete(int listIndex, int offset, int length) {
        int size = _textRuns.size();
        int endMark = offset + length;
        int endIndex = listIndex;

        CHPX chpx = _textRuns.get(endIndex);
        while (chpx.getEnd() < endMark) {
            chpx = _textRuns.get(++endIndex);
        }
        if (listIndex == endIndex) {
            chpx = _textRuns.get(endIndex);
            chpx.setEnd((chpx.getEnd() - endMark) + offset);
        } else {
            chpx = _textRuns.get(listIndex);
            chpx.setEnd(offset);
            for (int x = listIndex + 1; x < endIndex; x++) {
                chpx = _textRuns.get(x);
                chpx.setStart(offset);
                chpx.setEnd(offset);
            }
            chpx = _textRuns.get(endIndex);
            chpx.setEnd((chpx.getEnd() - endMark) + offset);
        }

        for (int x = endIndex + 1; x < size; x++) {
            chpx = _textRuns.get(x);
            chpx.setStart(chpx.getStart() - length);
            chpx.setEnd(chpx.getEnd() - length);
        }
    }

    public void insert(int listIndex, int cpStart, SprmBuffer buf) {

        CHPX insertChpx = new CHPX(0, 0, buf);


        insertChpx.setStart(cpStart);
        insertChpx.setEnd(cpStart);

        if (listIndex == _textRuns.size()) {
            _textRuns.add(insertChpx);
        } else {
            CHPX chpx = _textRuns.get(listIndex);
            if (chpx.getStart() < cpStart) {





                CHPX clone = new CHPX(0, 0, chpx.getSprmBuf());

                clone.setStart(cpStart);
                clone.setEnd(chpx.getEnd());

                chpx.setEnd(cpStart);

                _textRuns.add(listIndex + 1, insertChpx);
                _textRuns.add(listIndex + 2, clone);
            } else {
                _textRuns.add(listIndex, insertChpx);
            }
        }
    }

    public void adjustForInsert(int listIndex, int length) {
        int size = _textRuns.size();
        CHPX chpx = _textRuns.get(listIndex);
        chpx.setEnd(chpx.getEnd() + length);

        for (int x = listIndex + 1; x < size; x++) {
            chpx = _textRuns.get(x);
            chpx.setStart(chpx.getStart() + length);
            chpx.setEnd(chpx.getEnd() + length);
        }
    }

    public List<CHPX> getTextRuns() {
        return _textRuns;
    }

    @Deprecated
    public void writeTo(HWPFFileSystem sys, int fcMin, CharIndexTranslator translator)
            throws IOException {
        HWPFOutputStream docStream = sys.getStream("WordDocument");
        HWPFOutputStream tableStream = sys.getStream("1Table");

        writeTo(docStream, tableStream, fcMin, translator);
    }

    public void writeTo(HWPFOutputStream wordDocumentStream, HWPFOutputStream tableStream,
                        int fcMin, CharIndexTranslator translator) throws IOException {


        PlexOfCps bte = new PlexOfCps(4);


        int docOffset = wordDocumentStream.getOffset();
        int mod = docOffset % POIFSConstants.SMALLER_BIG_BLOCK_SIZE;
        if (mod != 0) {
            byte[] padding = new byte[POIFSConstants.SMALLER_BIG_BLOCK_SIZE - mod];
            wordDocumentStream.write(padding);
        }


        docOffset = wordDocumentStream.getOffset();
        int pageNum = docOffset / POIFSConstants.SMALLER_BIG_BLOCK_SIZE;





        int endingFc = translator.getByteIndex(_textRuns.get(_textRuns.size() - 1).getEnd());

        ArrayList<CHPX> overflow = _textRuns;
        do {
            CHPX startingProp = overflow.get(0);

            int start = translator.getByteIndex(startingProp.getStart());

            CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage();
            cfkp.fill(overflow);

            byte[] bufFkp = cfkp.toByteArray(translator);
            wordDocumentStream.write(bufFkp);
            overflow = cfkp.getOverflow();

            int end = endingFc;
            if (overflow != null) {

                end = translator.getByteIndex(overflow.get(0).getStart());
            }

            byte[] intHolder = new byte[4];
            LittleEndian.putInt(intHolder, pageNum++);
            bte.addProperty(new GenericPropertyNode(start, end, intHolder));

        }
        while (overflow != null);
        tableStream.write(bte.toByteArray());
    }
}
