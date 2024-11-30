

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.util.LittleEndianOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public abstract class PageBreakRecord extends StandardRecord {
    private static final int[] EMPTY_INT_ARRAY = {};

    private List<Break> _breaks;
    private Map<Integer, Break> _breakMap;

    protected PageBreakRecord() {
        _breaks = new ArrayList<Break>();
        _breakMap = new HashMap<Integer, Break>();
    }

    public PageBreakRecord(RecordInputStream in) {
        int nBreaks = in.readShort();
        _breaks = new ArrayList<Break>(nBreaks + 2);
        _breakMap = new HashMap<Integer, Break>();

        for (int k = 0; k < nBreaks; k++) {
            Break br = new Break(in);
            _breaks.add(br);
            _breakMap.put(Integer.valueOf(br.main), br);
        }

    }

    public boolean isEmpty() {
        return _breaks.isEmpty();
    }

    protected int getDataSize() {
        return 2 + _breaks.size() * Break.ENCODED_SIZE;
    }

    public final void serialize(LittleEndianOutput out) {
        int nBreaks = _breaks.size();
        out.writeShort(nBreaks);
        for (int i = 0; i < nBreaks; i++) {
            _breaks.get(i).serialize(out);
        }
    }

    public int getNumBreaks() {
        return _breaks.size();
    }

    public final Iterator<Break> getBreaksIterator() {
        return _breaks.iterator();
    }

    public String toString() {
        StringBuffer retval = new StringBuffer();

        String label;
        String mainLabel;
        String subLabel;

        if (getSid() == HorizontalPageBreakRecord.sid) {
            label = "HORIZONTALPAGEBREAK";
            mainLabel = "row";
            subLabel = "col";
        } else {
            label = "VERTICALPAGEBREAK";
            mainLabel = "column";
            subLabel = "row";
        }

        retval.append("[" + label + "]").append("\n");
        retval.append("     .sid        =").append(getSid()).append("\n");
        retval.append("     .numbreaks =").append(getNumBreaks()).append("\n");
        Iterator<Break> iterator = getBreaksIterator();
        for (int k = 0; k < getNumBreaks(); k++) {
            Break region = iterator.next();

            retval.append("     .").append(mainLabel).append(" (zero-based) =").append(region.main).append("\n");
            retval.append("     .").append(subLabel).append("From    =").append(region.subFrom).append("\n");
            retval.append("     .").append(subLabel).append("To      =").append(region.subTo).append("\n");
        }

        retval.append("[" + label + "]").append("\n");
        return retval.toString();
    }


    public void addBreak(int main, int subFrom, int subTo) {

        Integer key = Integer.valueOf(main);
        Break region = _breakMap.get(key);
        if (region == null) {
            region = new Break(main, subFrom, subTo);
            _breakMap.put(key, region);
            _breaks.add(region);
        } else {
            region.main = main;
            region.subFrom = subFrom;
            region.subTo = subTo;
        }
    }


    public final void removeBreak(int main) {
        Integer rowKey = Integer.valueOf(main);
        Break region = _breakMap.get(rowKey);
        _breaks.remove(region);
        _breakMap.remove(rowKey);
    }


    public final Break getBreak(int main) {
        Integer rowKey = Integer.valueOf(main);
        return _breakMap.get(rowKey);
    }

    public final int[] getBreaks() {
        int count = getNumBreaks();
        if (count < 1) {
            return EMPTY_INT_ARRAY;
        }
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            Break breakItem = _breaks.get(i);
            result[i] = breakItem.main;
        }
        return result;
    }


    public static final class Break {

        public static final int ENCODED_SIZE = 6;
        public int main;
        public int subFrom;
        public int subTo;

        public Break(int main, int subFrom, int subTo) {
            this.main = main;
            this.subFrom = subFrom;
            this.subTo = subTo;
        }

        public Break(RecordInputStream in) {
            main = in.readUShort() - 1;
            subFrom = in.readUShort();
            subTo = in.readUShort();
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(main + 1);
            out.writeShort(subFrom);
            out.writeShort(subTo);
        }
    }
}
