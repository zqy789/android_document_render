
package com.document.render.office.fc.xls;

import android.net.Uri;

import com.document.render.bean.DocSourceType;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.model.InternalSheet;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.BoolErrRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.hssf.record.NumberRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordFactory;
import com.document.render.office.fc.poifs.filesystem.DirectoryNode;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;
import com.document.render.office.ss.model.XLSModel.ACell;
import com.document.render.office.ss.model.XLSModel.AWorkbook;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.IControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;


public class XLSReader extends SSReader {

    private String filePath;
    private int docSourceType;


    public XLSReader(IControl control, String filePath, int docSourceType) {
        this.control = control;
        this.filePath = filePath;
        this.docSourceType = docSourceType;
    }


    public Object getModel() throws Exception {
        InputStream is = null;
        switch (docSourceType) {
            case DocSourceType.URL:
                URL url = new URL(filePath);
                is = url.openStream();
                break;
            case DocSourceType.URI:
                Uri uri = Uri.parse(filePath);
                is = control.getActivity().getContentResolver().openInputStream(uri);
                break;
            case DocSourceType.PATH:
                is = new FileInputStream(filePath);
                break;
            case DocSourceType.ASSETS:
                is = control.getActivity().getAssets().open(filePath);
                break;
        }
        Workbook book = new AWorkbook(is, this);

        return book;
    }


    public boolean searchContent(File file, String key) throws Exception {
        try {
            key = key.toLowerCase();

            FileInputStream is = new FileInputStream(file.getAbsolutePath());
            DirectoryNode directory = new POIFSFileSystem(is).getRoot();

            String workbookName = AWorkbook.getWorkbookDirEntryName(directory);



            InputStream stream = directory.createDocumentInputStream(workbookName);

            List<Record> records = RecordFactory.createRecords(stream, this);

            InternalWorkbook workbook = InternalWorkbook.createWorkbook(records, this);


            int numSheets = workbook.getNumSheets();
            int sheetIndex = 0;
            while (sheetIndex < numSheets) {
                if (workbook.getSheetName(sheetIndex++).toLowerCase().contains(key)) {
                    return true;
                }
            }


            int size = workbook.getSSTUniqueStringSize();
            for (int i = 0; i < size; i++) {
                checkAbortReader();
                if (workbook.getSSTString(i).getString().toLowerCase().contains(key)) {
                    return true;
                }
            }


            int recOffset = workbook.getNumRecords();
            RecordStream rs = new RecordStream(records, recOffset);
            sheetIndex = 0;
            while (rs.hasNext()) {
                InternalSheet internalSheet = InternalSheet.createSheet(rs, this);
                if (search_Sheet(internalSheet, key)) {
                    return true;
                }
            }


            for (int i = 0; i < workbook.getNumNames(); ++i) {
                NameRecord nameRecord = workbook.getNameRecord(i);
                if (nameRecord.getNameText().toLowerCase().contains(key)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean search_Sheet(InternalSheet sheet, String key) {
        Iterator<CellValueRecordInterface> iter = sheet.getCellValueIterator();


        while (iter.hasNext()) {
            CellValueRecordInterface cval = iter.next();
            checkAbortReader();

            if (search_Cell(cval, key)) {
                return true;
            }
        }

        return false;
    }

    private boolean search_Cell(CellValueRecordInterface cval, String key) {
        short cellType = (short) ACell.determineType(cval);

        switch (cellType) {
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(((NumberRecord) cval).getValue()).contains(key);
            case Cell.CELL_TYPE_STRING:
            case Cell.CELL_TYPE_BLANK:
            case Cell.CELL_TYPE_FORMULA:
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(((BoolErrRecord) cval).getBooleanValue()).toLowerCase().contains(key);

            case Cell.CELL_TYPE_ERROR:
                return ErrorEval.getText(((BoolErrRecord) cval).getErrorValue()).toLowerCase().contains(key);

        }

        return false;
    }

    private void checkAbortReader() {
        if (abortReader) {
            throw new AbortReaderError("abort Reader");
        }
    }


    public void dispose() {
        super.dispose();
        filePath = null;
    }
}
