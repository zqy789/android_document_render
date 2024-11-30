

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordBase;


public abstract class RecordAggregate extends RecordBase {


    public abstract void visitContainedRecords(RecordVisitor rv);

    public final int serialize(int offset, byte[] data) {
        SerializingRecordVisitor srv = new SerializingRecordVisitor(data, offset);
        visitContainedRecords(srv);
        return srv.countBytesWritten();
    }

    public int getRecordSize() {
        RecordSizingVisitor rsv = new RecordSizingVisitor();
        visitContainedRecords(rsv);
        return rsv.getTotalSize();
    }

    public interface RecordVisitor {

        void visitRecord(Record r);
    }

    private static final class SerializingRecordVisitor implements RecordVisitor {

        private final byte[] _data;
        private final int _startOffset;
        private int _countBytesWritten;

        public SerializingRecordVisitor(byte[] data, int startOffset) {
            _data = data;
            _startOffset = startOffset;
            _countBytesWritten = 0;
        }

        public int countBytesWritten() {
            return _countBytesWritten;
        }

        public void visitRecord(Record r) {
            int currentOffset = _startOffset + _countBytesWritten;
            _countBytesWritten += r.serialize(currentOffset, _data);
        }
    }

    private static final class RecordSizingVisitor implements RecordVisitor {

        private int _totalSize;

        public RecordSizingVisitor() {
            _totalSize = 0;
        }

        public int getTotalSize() {
            return _totalSize;
        }

        public void visitRecord(Record r) {
            _totalSize += r.getRecordSize();
        }
    }


    public static final class PositionTrackingVisitor implements RecordVisitor {
        private final RecordVisitor _rv;
        private int _position;

        public PositionTrackingVisitor(RecordVisitor rv, int initialPosition) {
            _rv = rv;
            _position = initialPosition;
        }

        public void visitRecord(Record r) {
            _position += r.getRecordSize();
            _rv.visitRecord(r);
        }

        public int getPosition() {
            return _position;
        }

        public void setPosition(int position) {
            _position = position;
        }
    }
}
