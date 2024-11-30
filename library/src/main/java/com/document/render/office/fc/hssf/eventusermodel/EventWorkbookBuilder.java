
package com.document.render.office.fc.hssf.eventusermodel;

import com.document.render.office.fc.hssf.model.HSSFFormulaParser;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.BoundSheetRecord;
import com.document.render.office.fc.hssf.record.EOFRecord;
import com.document.render.office.fc.hssf.record.ExternSheetRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.SSTRecord;
import com.document.render.office.fc.hssf.record.SupBookRecord;
import com.document.render.office.fc.hssf.usermodel.HSSFWorkbook;

import java.util.ArrayList;
import java.util.List;



public class EventWorkbookBuilder {



    public static InternalWorkbook createStubWorkbook(ExternSheetRecord[] externs,
                                                      BoundSheetRecord[] bounds, SSTRecord sst) {
        List wbRecords = new ArrayList();


        if (bounds != null) {
            for (int i = 0; i < bounds.length; i++) {
                wbRecords.add(bounds[i]);
            }
        }
        if (sst != null) {
            wbRecords.add(sst);
        }



        if (externs != null) {
            wbRecords.add(SupBookRecord.createInternalReferences(
                    (short) externs.length));
            for (int i = 0; i < externs.length; i++) {
                wbRecords.add(externs[i]);
            }
        }


        wbRecords.add(EOFRecord.instance);

        return InternalWorkbook.createWorkbook(wbRecords);
    }


    public static InternalWorkbook createStubWorkbook(ExternSheetRecord[] externs,
                                                      BoundSheetRecord[] bounds) {
        return createStubWorkbook(externs, bounds, null);
    }



    public static class SheetRecordCollectingListener implements HSSFListener {
        private HSSFListener childListener;
        private List boundSheetRecords = new ArrayList();
        private List externSheetRecords = new ArrayList();
        private SSTRecord sstRecord = null;

        public SheetRecordCollectingListener(HSSFListener childListener) {
            this.childListener = childListener;
        }


        public BoundSheetRecord[] getBoundSheetRecords() {
            return (BoundSheetRecord[]) boundSheetRecords.toArray(
                    new BoundSheetRecord[boundSheetRecords.size()]
            );
        }

        public ExternSheetRecord[] getExternSheetRecords() {
            return (ExternSheetRecord[]) externSheetRecords.toArray(
                    new ExternSheetRecord[externSheetRecords.size()]
            );
        }

        public SSTRecord getSSTRecord() {
            return sstRecord;
        }

        public HSSFWorkbook getStubHSSFWorkbook() {
            return HSSFWorkbook.create(getStubWorkbook());
        }

        public InternalWorkbook getStubWorkbook() {
            return createStubWorkbook(
                    getExternSheetRecords(), getBoundSheetRecords(),
                    getSSTRecord()
            );
        }



        public void processRecord(Record record) {

            processRecordInternally(record);


            childListener.processRecord(record);
        }


        public void processRecordInternally(Record record) {
            if (record instanceof BoundSheetRecord) {
                boundSheetRecords.add(record);
            } else if (record instanceof ExternSheetRecord) {
                externSheetRecords.add(record);
            } else if (record instanceof SSTRecord) {
                sstRecord = (SSTRecord) record;
            }
        }
    }
}
