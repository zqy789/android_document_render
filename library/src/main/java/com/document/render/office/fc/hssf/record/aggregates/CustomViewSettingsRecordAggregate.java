

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordBase;
import com.document.render.office.fc.hssf.record.UserSViewBegin;
import com.document.render.office.fc.hssf.record.UserSViewEnd;

import java.util.ArrayList;
import java.util.List;



public final class CustomViewSettingsRecordAggregate extends RecordAggregate {

    private final Record _begin;
    private final Record _end;

    private final List<RecordBase> _recs;
    private PageSettingsBlock _psBlock;

    public CustomViewSettingsRecordAggregate(RecordStream rs) {
        _begin = rs.getNext();
        if (_begin.getSid() != UserSViewBegin.sid) {
            throw new IllegalStateException("Bad begin record");
        }
        List<RecordBase> temp = new ArrayList<RecordBase>();
        while (rs.peekNextSid() != UserSViewEnd.sid) {
            if (PageSettingsBlock.isComponentRecord(rs.peekNextSid())) {
                if (_psBlock != null) {
                    throw new IllegalStateException(
                            "Found more than one PageSettingsBlock in custom view settings sub-stream");
                }
                _psBlock = new PageSettingsBlock(rs);
                temp.add(_psBlock);
                continue;
            }
            temp.add(rs.getNext());
        }
        _recs = temp;
        _end = rs.getNext();
        if (_end.getSid() != UserSViewEnd.sid) {
            throw new IllegalStateException("Bad custom view settings end record");
        }
    }

    public static boolean isBeginRecord(int sid) {
        return sid == UserSViewBegin.sid;
    }

    public void visitContainedRecords(RecordVisitor rv) {
        if (_recs.isEmpty()) {
            return;
        }
        rv.visitRecord(_begin);
        for (int i = 0; i < _recs.size(); i++) {
            RecordBase rb = _recs.get(i);
            if (rb instanceof RecordAggregate) {
                ((RecordAggregate) rb).visitContainedRecords(rv);
            } else {
                rv.visitRecord((Record) rb);
            }
        }
        rv.visitRecord(_end);
    }

    public void append(RecordBase r) {
        _recs.add(r);
    }
}
