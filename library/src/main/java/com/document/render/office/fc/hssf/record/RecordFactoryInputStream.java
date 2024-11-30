
package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.EncryptedDocumentException;
import com.document.render.office.fc.hssf.eventusermodel.HSSFEventFactory;
import com.document.render.office.fc.hssf.eventusermodel.HSSFListener;
import com.document.render.office.fc.hssf.record.crypto.Biff8EncryptionKey;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;



public final class RecordFactoryInputStream {

    private final boolean _shouldIncludeContinueRecords;
    private  RecordInputStream _recStream;

    private Record[] _unreadRecordBuffer;

    private int _unreadRecordIndex = -1;

    private Record _lastRecord = null;

    private DrawingRecord _lastDrawingRecord = new DrawingRecord();
    private int _bofDepth;
    private boolean _lastRecordWasEOFLevelZero;


    public RecordFactoryInputStream(InputStream in, boolean shouldIncludeContinueRecords) {
        RecordInputStream rs = new RecordInputStream(in);
        List<Record> records = new ArrayList<Record>();
        StreamEncryptionInfo sei = new StreamEncryptionInfo(rs, records);
        if (sei.hasEncryption()) {
            rs = sei.createDecryptingStream(in);
        } else {

        }

        if (!records.isEmpty()) {
            _unreadRecordBuffer = new Record[records.size()];
            records.toArray(_unreadRecordBuffer);
            _unreadRecordIndex = 0;
        }
        _recStream = rs;
        _shouldIncludeContinueRecords = shouldIncludeContinueRecords;
        _lastRecord = sei.getLastRecord();


        _bofDepth = sei.hasBOFRecord() ? 1 : 0;
        _lastRecordWasEOFLevelZero = false;
    }


    public Record nextRecord() {
        Record r;
        r = getNextUnreadRecord();
        if (r != null) {

            return r;
        }
        while (true) {
            if (!_recStream.hasNextRecord()) {

                return null;
            }

            if (_lastRecordWasEOFLevelZero) {





                if (_recStream.getNextSid() != BOFRecord.sid) {
                    return null;
                }

            }


            _recStream.nextRecord();

            r = readNextRecord();
            if (r == null) {

                continue;
            }
            return r;
        }
    }


    private Record getNextUnreadRecord() {
        if (_unreadRecordBuffer != null) {
            int ix = _unreadRecordIndex;
            if (ix < _unreadRecordBuffer.length) {
                Record result = _unreadRecordBuffer[ix];
                _unreadRecordIndex = ix + 1;
                return result;
            }
            _unreadRecordIndex = -1;
            _unreadRecordBuffer = null;
        }
        return null;
    }


    private Record readNextRecord() {

        Record record = RecordFactory.createSingleRecord(_recStream);
        _lastRecordWasEOFLevelZero = false;


        if (_lastDrawingRecord != null
                && record.getSid() != ContinueRecord.sid
                && record.getSid() != ObjRecord.sid
                && record.getSid() != TextObjectRecord.sid) {
            _lastDrawingRecord = null;
        }

        if (record instanceof BOFRecord) {
            _bofDepth++;
            return record;
        }

        if (record instanceof EOFRecord) {
            _bofDepth--;
            if (_bofDepth < 1) {
                _lastRecordWasEOFLevelZero = true;
            }

            return record;
        }

        if (record instanceof DBCellRecord) {

            return null;
        }

        if (record instanceof RKRecord) {
            return RecordFactory.convertToNumberRecord((RKRecord) record);
        }

        if (record instanceof MulRKRecord) {
            Record[] records = RecordFactory.convertRKRecords((MulRKRecord) record);

            _unreadRecordBuffer = records;
            _unreadRecordIndex = 1;
            return records[0];
        }

        if (record.getSid() == DrawingGroupRecord.sid
                && _lastRecord instanceof DrawingGroupRecord) {
            DrawingGroupRecord lastDGRecord = (DrawingGroupRecord) _lastRecord;
            lastDGRecord.join((AbstractEscherHolderRecord) record);
            return null;
        }
        if (record.getSid() == ContinueRecord.sid) {
            ContinueRecord contRec = (ContinueRecord) record;

            if (_lastRecord instanceof ObjRecord || _lastRecord instanceof TextObjectRecord) {


                if (_lastDrawingRecord != null) {
                    _lastDrawingRecord.processContinueRecord(contRec.getData());
                    contRec.resetData();
                }



                if (_shouldIncludeContinueRecords) {
                    return record;
                }
                return null;
            }
            if (_lastRecord instanceof DrawingGroupRecord) {
                ((DrawingGroupRecord) _lastRecord).processContinueRecord(contRec.getData());
                return null;
            }
            if (_lastRecord instanceof DrawingRecord) {
                ((DrawingRecord) _lastRecord).processContinueRecord(contRec.getData());
                return null;
            }
            if (_lastRecord instanceof UnknownRecord) {


                return record;
            }
            if (_lastRecord instanceof EOFRecord) {


                return record;
            }
            throw new RecordFormatException("Unhandled Continue Record followining " + _lastRecord.getClass());
        }
        _lastRecord = record;
        if (record instanceof DrawingRecord) {
            _lastDrawingRecord = (DrawingRecord) record;
        }
        return record;
    }

    public void dispose() {
        _recStream = null;

        _unreadRecordBuffer = null;

        _lastRecord = null;
        _lastDrawingRecord = null;
    }


    private static final class StreamEncryptionInfo {
        private final int _initialRecordsSize;
        private final FilePassRecord _filePassRec;
        private final Record _lastRecord;
        private final boolean _hasBOFRecord;

        public StreamEncryptionInfo(RecordInputStream rs, List<Record> outputRecs) {
            Record rec;
            rs.nextRecord();
            int recSize = 4 + rs.remaining();
            rec = RecordFactory.createSingleRecord(rs);
            outputRecs.add(rec);
            FilePassRecord fpr = null;
            if (rec instanceof BOFRecord) {
                _hasBOFRecord = true;
                if (rs.hasNextRecord()) {
                    rs.nextRecord();
                    rec = RecordFactory.createSingleRecord(rs);
                    recSize += rec.getRecordSize();
                    outputRecs.add(rec);
                    if (rec instanceof FilePassRecord) {
                        fpr = (FilePassRecord) rec;
                        outputRecs.remove(outputRecs.size() - 1);

                        rec = outputRecs.get(0);
                    } else {

                        if (rec instanceof EOFRecord) {


                            throw new IllegalStateException("Nothing between BOF and EOF");
                        }
                    }
                }
            } else {



                _hasBOFRecord = false;
            }
            _initialRecordsSize = recSize;
            _filePassRec = fpr;
            _lastRecord = rec;
        }

        public RecordInputStream createDecryptingStream(InputStream original) {
            FilePassRecord fpr = _filePassRec;
            String userPassword = Biff8EncryptionKey.getCurrentUserPassword();

            Biff8EncryptionKey key;
            if (userPassword == null) {
                key = Biff8EncryptionKey.create(fpr.getDocId());
            } else {
                key = Biff8EncryptionKey.create(userPassword, fpr.getDocId());
            }
            if (!key.validate(fpr.getSaltData(), fpr.getSaltHash())) {

                throw new EncryptedDocumentException("Cannot process encrypted office files!");
            }
            return new RecordInputStream(original, key, _initialRecordsSize);
        }

        public boolean hasEncryption() {
            return _filePassRec != null;
        }


        public Record getLastRecord() {
            return _lastRecord;
        }


        public boolean hasBOFRecord() {
            return _hasBOFRecord;
        }
    }
}
