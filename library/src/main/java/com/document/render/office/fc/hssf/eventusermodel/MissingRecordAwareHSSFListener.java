

package com.document.render.office.fc.hssf.eventusermodel;


import com.document.render.office.fc.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import com.document.render.office.fc.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import com.document.render.office.fc.hssf.eventusermodel.dummyrecord.MissingRowDummyRecord;
import com.document.render.office.fc.hssf.record.BOFRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.MulBlankRecord;
import com.document.render.office.fc.hssf.record.MulRKRecord;
import com.document.render.office.fc.hssf.record.NoteRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordFactory;
import com.document.render.office.fc.hssf.record.RowRecord;
import com.document.render.office.fc.hssf.record.SharedFormulaRecord;



public final class MissingRecordAwareHSSFListener implements HSSFListener {
    private HSSFListener childListener;




    private int lastRowRow;

    private int lastCellRow;
    private int lastCellColumn;


    public MissingRecordAwareHSSFListener(HSSFListener listener) {
        resetCounts();
        childListener = listener;
    }

    public void processRecord(Record record) {
        int thisRow;
        int thisColumn;
        CellValueRecordInterface[] expandedRecords = null;

        if (record instanceof CellValueRecordInterface) {
            CellValueRecordInterface valueRec = (CellValueRecordInterface) record;
            thisRow = valueRec.getRow();
            thisColumn = valueRec.getColumn();
        } else {
            thisRow = -1;
            thisColumn = -1;

            switch (record.getSid()) {


                case BOFRecord.sid:
                    BOFRecord bof = (BOFRecord) record;
                    if (bof.getType() == bof.TYPE_WORKBOOK || bof.getType() == bof.TYPE_WORKSHEET) {

                        resetCounts();
                    }
                    break;
                case RowRecord.sid:
                    RowRecord rowrec = (RowRecord) record;




                    if (lastRowRow + 1 < rowrec.getRowNumber()) {
                        for (int i = (lastRowRow + 1); i < rowrec.getRowNumber(); i++) {
                            MissingRowDummyRecord dr = new MissingRowDummyRecord(i);
                            childListener.processRecord(dr);
                        }
                    }


                    lastRowRow = rowrec.getRowNumber();
                    break;

                case SharedFormulaRecord.sid:



                    childListener.processRecord(record);
                    return;
                case MulBlankRecord.sid:



                    MulBlankRecord mbr = (MulBlankRecord) record;
                    expandedRecords = RecordFactory.convertBlankRecords(mbr);
                    break;
                case MulRKRecord.sid:


                    MulRKRecord mrk = (MulRKRecord) record;
                    expandedRecords = RecordFactory.convertRKRecords(mrk);
                    break;
                case NoteRecord.sid:
                    NoteRecord nrec = (NoteRecord) record;
                    thisRow = nrec.getRow();
                    thisColumn = nrec.getColumn();
                    break;
            }
        }


        if (expandedRecords != null && expandedRecords.length > 0) {
            thisRow = expandedRecords[0].getRow();
            thisColumn = expandedRecords[0].getColumn();
        }




        if (thisRow != lastCellRow && lastCellRow > -1) {
            for (int i = lastCellRow; i < thisRow; i++) {
                int cols = -1;
                if (i == lastCellRow) {
                    cols = lastCellColumn;
                }
                childListener.processRecord(new LastCellOfRowDummyRecord(i, cols));
            }
        }



        if (lastCellRow != -1 && lastCellColumn != -1 && thisRow == -1) {
            childListener.processRecord(new LastCellOfRowDummyRecord(lastCellRow, lastCellColumn));

            lastCellRow = -1;
            lastCellColumn = -1;
        }



        if (thisRow != lastCellRow) {
            lastCellColumn = -1;
        }



        if (lastCellColumn != thisColumn - 1) {
            for (int i = lastCellColumn + 1; i < thisColumn; i++) {
                childListener.processRecord(new MissingCellDummyRecord(thisRow, i));
            }
        }


        if (expandedRecords != null && expandedRecords.length > 0) {
            thisColumn = expandedRecords[expandedRecords.length - 1].getColumn();
        }



        if (thisColumn != -1) {
            lastCellColumn = thisColumn;
            lastCellRow = thisRow;
        }


        if (expandedRecords != null && expandedRecords.length > 0) {
            for (CellValueRecordInterface r : expandedRecords) {
                childListener.processRecord((Record) r);
            }
        } else {
            childListener.processRecord(record);
        }
    }

    private void resetCounts() {
        lastRowRow = -1;
        lastCellRow = -1;
        lastCellColumn = -1;
    }
}
