

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;

import java.util.ArrayList;
import java.util.List;



public class ExternSheetRecord extends StandardRecord {

    @Keep
    public final static short sid = 0x0017;
    private List<RefSubRecord> _list;

    public ExternSheetRecord() {
        _list = new ArrayList<RefSubRecord>();
    }


    public ExternSheetRecord(RecordInputStream in) {
        _list = new ArrayList<RefSubRecord>();

        int nItems = in.readShort();

        for (int i = 0; i < nItems; ++i) {
            RefSubRecord rec = new RefSubRecord(in);
            _list.add(rec);
        }
    }

    public static ExternSheetRecord combine(ExternSheetRecord[] esrs) {
        ExternSheetRecord result = new ExternSheetRecord();
        for (int i = 0; i < esrs.length; i++) {
            ExternSheetRecord esr = esrs[i];
            int nRefs = esr.getNumOfREFRecords();
            for (int j = 0; j < nRefs; j++) {
                result.addREFRecord(esr.getRef(j));
            }
        }
        return result;
    }


    public int getNumOfRefs() {
        return _list.size();
    }


    public void addREFRecord(RefSubRecord rec) {
        _list.add(rec);
    }


    public int getNumOfREFRecords() {
        return _list.size();
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        int nItems = _list.size();
        sb.append("[EXTERNSHEET]\n");
        sb.append("   numOfRefs     = ").append(nItems).append("\n");
        for (int i = 0; i < nItems; i++) {
            sb.append("refrec         #").append(i).append(": ");
            sb.append(getRef(i).toString());
            sb.append('\n');
        }
        sb.append("[/EXTERNSHEET]\n");


        return sb.toString();
    }

    protected int getDataSize() {
        return 2 + _list.size() * RefSubRecord.ENCODED_SIZE;
    }

    public void serialize(LittleEndianOutput out) {
        int nItems = _list.size();

        out.writeShort(nItems);

        for (int i = 0; i < nItems; i++) {
            getRef(i).serialize(out);
        }
    }

    private RefSubRecord getRef(int i) {
        return _list.get(i);
    }


    public short getSid() {
        return sid;
    }

    public int getExtbookIndexFromRefIndex(int refIndex) {
        return getRef(refIndex).getExtBookIndex();
    }


    public int findRefIndexFromExtBookIndex(int extBookIndex) {
        int nItems = _list.size();
        for (int i = 0; i < nItems; i++) {
            if (getRef(i).getExtBookIndex() == extBookIndex) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstSheetIndexFromRefIndex(int extRefIndex) {
        return getRef(extRefIndex).getFirstSheetIndex();
    }


    public int addRef(int extBookIndex, int firstSheetIndex, int lastSheetIndex) {
        _list.add(new RefSubRecord(extBookIndex, firstSheetIndex, lastSheetIndex));
        return _list.size() - 1;
    }

    public int getRefIxForSheet(int externalBookIndex, int sheetIndex) {
        int nItems = _list.size();
        for (int i = 0; i < nItems; i++) {
            RefSubRecord ref = getRef(i);
            if (ref.getExtBookIndex() != externalBookIndex) {
                continue;
            }
            if (ref.getFirstSheetIndex() == sheetIndex && ref.getLastSheetIndex() == sheetIndex) {
                return i;
            }
        }
        return -1;
    }

    private static final class RefSubRecord {
        public static final int ENCODED_SIZE = 6;


        private int _extBookIndex;
        private int _firstSheetIndex;
        private int _lastSheetIndex;



        public RefSubRecord(int extBookIndex, int firstSheetIndex, int lastSheetIndex) {
            _extBookIndex = extBookIndex;
            _firstSheetIndex = firstSheetIndex;
            _lastSheetIndex = lastSheetIndex;
        }


        public RefSubRecord(RecordInputStream in) {
            this(in.readShort(), in.readShort(), in.readShort());
        }

        public int getExtBookIndex() {
            return _extBookIndex;
        }

        public int getFirstSheetIndex() {
            return _firstSheetIndex;
        }

        public int getLastSheetIndex() {
            return _lastSheetIndex;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("extBook=").append(_extBookIndex);
            buffer.append(" firstSheet=").append(_firstSheetIndex);
            buffer.append(" lastSheet=").append(_lastSheetIndex);
            return buffer.toString();
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(_extBookIndex);
            out.writeShort(_firstSheetIndex);
            out.writeShort(_lastSheetIndex);
        }
    }
}
