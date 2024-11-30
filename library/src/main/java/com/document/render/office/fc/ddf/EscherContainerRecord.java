

package com.document.render.office.fc.ddf;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;



public final class EscherContainerRecord extends EscherRecord {
    public static final short DGG_CONTAINER = (short) 0xF000;
    public static final short BSTORE_CONTAINER = (short) 0xF001;
    public static final short DG_CONTAINER = (short) 0xF002;
    public static final short SPGR_CONTAINER = (short) 0xF003;
    public static final short SP_CONTAINER = (short) 0xF004;
    public static final short SOLVER_CONTAINER = (short) 0xF005;

    private final List<EscherRecord> _childRecords = new ArrayList<EscherRecord>();

    public int fillFields(byte[] data, int pOffset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, pOffset);
        int bytesWritten = 8;
        int offset = pOffset + 8;
        while (bytesRemaining > 0 && offset < data.length) {
            EscherRecord child = recordFactory.createRecord(data, offset);
            int childBytesWritten = child.fillFields(data, offset, recordFactory);
            bytesWritten += childBytesWritten;
            offset += childBytesWritten;
            bytesRemaining -= childBytesWritten;
            addChildRecord(child);
            if (offset >= data.length && bytesRemaining > 0) {


            }
        }
        return bytesWritten;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);

        LittleEndian.putShort(data, offset, getOptions());
        LittleEndian.putShort(data, offset + 2, getRecordId());
        int remainingBytes = 0;
        Iterator<EscherRecord> iterator = _childRecords.iterator();
        while (iterator.hasNext()) {
            EscherRecord r = iterator.next();
            remainingBytes += r.getRecordSize();
        }
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        int pos = offset + 8;
        iterator = _childRecords.iterator();
        while (iterator.hasNext()) {
            EscherRecord r = iterator.next();
            pos += r.serialize(pos, data, listener);
        }

        listener.afterRecordSerialize(pos, getRecordId(), pos - offset, this);
        return pos - offset;
    }

    public int getRecordSize() {
        int childRecordsSize = 0;
        Iterator<EscherRecord> iterator = _childRecords.iterator();
        while (iterator.hasNext()) {
            EscherRecord r = iterator.next();
            childRecordsSize += r.getRecordSize();
        }
        return 8 + childRecordsSize;
    }


    public boolean hasChildOfType(short recordId) {
        Iterator<EscherRecord> iterator = _childRecords.iterator();
        while (iterator.hasNext()) {
            EscherRecord r = iterator.next();
            if (r.getRecordId() == recordId) {
                return true;
            }
        }
        return false;
    }

    public EscherRecord getChild(int index) {
        return _childRecords.get(index);
    }


    public List<EscherRecord> getChildRecords() {
        return new ArrayList<EscherRecord>(_childRecords);
    }


    public void setChildRecords(List<EscherRecord> childRecords) {
        if (childRecords == _childRecords) {
            throw new IllegalStateException("Child records private data member has escaped");
        }
        _childRecords.clear();
        _childRecords.addAll(childRecords);
    }

    public Iterator<EscherRecord> getChildIterator() {
        return new ReadOnlyIterator(_childRecords);
    }

    public boolean removeChildRecord(EscherRecord toBeRemoved) {
        return _childRecords.remove(toBeRemoved);
    }


    public List<EscherContainerRecord> getChildContainers() {
        List<EscherContainerRecord> containers = new ArrayList<EscherContainerRecord>();
        Iterator<EscherRecord> iterator = _childRecords.iterator();
        while (iterator.hasNext()) {
            EscherRecord r = iterator.next();
            if (r instanceof EscherContainerRecord) {
                containers.add((EscherContainerRecord) r);
            }
        }
        return containers;
    }

    public String getRecordName() {
        switch (getRecordId()) {
            case DGG_CONTAINER:
                return "DggContainer";
            case BSTORE_CONTAINER:
                return "BStoreContainer";
            case DG_CONTAINER:
                return "DgContainer";
            case SPGR_CONTAINER:
                return "SpgrContainer";
            case SP_CONTAINER:
                return "SpContainer";
            case SOLVER_CONTAINER:
                return "SolverContainer";
            default:
                return "Container 0x" + HexDump.toHex(getRecordId());
        }
    }

    public void display(PrintWriter w, int indent) {
        super.display(w, indent);
        for (Iterator<EscherRecord> iterator = _childRecords.iterator(); iterator.hasNext(); ) {
            EscherRecord escherRecord = iterator.next();
            escherRecord.display(w, indent + 1);
        }
    }

    public void addChildRecord(EscherRecord record) {
        _childRecords.add(record);
    }

    public void addChildBefore(EscherRecord record, int insertBeforeRecordId) {
        for (int i = 0; i < _childRecords.size(); i++) {
            EscherRecord rec = _childRecords.get(i);
            if (rec.getRecordId() == insertBeforeRecordId) {
                _childRecords.add(i++, record);

            }
        }
    }

    public String toString() {
        String nl = System.getProperty("line.separator");

        StringBuffer children = new StringBuffer();
        if (_childRecords.size() > 0) {
            children.append("  children: " + nl);

            int count = 0;
            for (Iterator<EscherRecord> iterator = _childRecords.iterator(); iterator.hasNext(); ) {
                EscherRecord record = iterator.next();
                children.append("   Child " + count + ":" + nl);
                String childResult = String.valueOf(record);
                childResult = childResult.replaceAll("\n", "\n    ");
                children.append("    ");
                children.append(childResult);
                children.append(nl);
                count++;
            }
        }

        return getClass().getName() + " (" + getRecordName() + "):" + nl + "  isContainer: "
                + isContainerRecord() + nl + "  options: 0x" + HexDump.toHex(getOptions()) + nl
                + "  recordId: 0x" + HexDump.toHex(getRecordId()) + nl + "  numchildren: "
                + _childRecords.size() + nl + children.toString();
    }

    public <T extends EscherRecord> T getChildById(short recordId) {
        for (EscherRecord childRecord : _childRecords) {
            if (childRecord.getRecordId() == recordId) {
                @SuppressWarnings("unchecked") final T result = (T) childRecord;
                return result;
            }
        }
        return null;
    }


    public void getRecordsById(short recordId, List<EscherRecord> out) {
        Iterator<EscherRecord> iterator = _childRecords.iterator();
        while (iterator.hasNext()) {
            EscherRecord r = iterator.next();
            if (r instanceof EscherContainerRecord) {
                EscherContainerRecord c = (EscherContainerRecord) r;
                c.getRecordsById(recordId, out);
            } else if (r.getRecordId() == recordId) {
                out.add(r);
            }
        }
    }


    public void dispose() {
        if (_childRecords != null) {
            for (int i = 0; i < _childRecords.size(); i++) {
                _childRecords.get(i).dispose();
            }
            _childRecords.clear();
        }
    }

    private static final class ReadOnlyIterator implements Iterator<EscherRecord> {
        private final List<EscherRecord> _list;
        private int _index;

        public ReadOnlyIterator(List<EscherRecord> list) {
            _list = list;
            _index = 0;
        }

        public boolean hasNext() {
            return _index < _list.size();
        }

        public EscherRecord next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return _list.get(_index++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
