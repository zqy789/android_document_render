

package com.document.render.office.fc.hssf.model;

import com.document.render.office.fc.hssf.record.Record;

import java.util.List;


public final class RecordStream {

    private final List<Record> _list;
    private final int _endIx;
    private int _nextIndex;
    private int _countRead;


    public RecordStream(List<Record> inputList, int startIndex, int endIx) {
        _list = inputList;
        _nextIndex = startIndex;
        _endIx = endIx;
        _countRead = 0;
    }

    public RecordStream(List<Record> records, int startIx) {
        this(records, startIx, records.size());
    }

    public boolean hasNext() {
        return _nextIndex < _endIx;
    }

    public Record getNext() {
        if (!hasNext()) {
            throw new RuntimeException("Attempt to read past end of record stream");
        }
        _countRead++;
        return (Record) _list.get(_nextIndex++);
    }


    public Class<? extends Record> peekNextClass() {
        if (!hasNext()) {
            return null;
        }
        return _list.get(_nextIndex).getClass();
    }


    public int peekNextSid() {
        if (!hasNext()) {
            return -1;
        }
        return ((Record) _list.get(_nextIndex)).getSid();
    }

    public int getCountRead() {
        return _countRead;
    }
}
