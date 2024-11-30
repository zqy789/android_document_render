
package com.document.render.office.fc.xls.Reader;

import android.os.Message;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackageRelationship;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipCollection;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipTypes;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.xls.SSReader;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IReader;
import com.document.render.office.system.ReaderHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class WorkbookReader {


    private static final int WINDOWWIDTH = 2;

    ;
    private static WorkbookReader reader = new WorkbookReader();

    private ZipPackage zipPackage;
    private Workbook book;
    private SSReader iReader;

    private Map<Integer, String> sheetIndexList;

    private Map<String, String> sheetNameList;




















    private int tempIndex;

    private PackageRelationshipCollection worksheetRelCollection, chartsheetRelCollection;


    public static WorkbookReader instance() {
        return reader;
    }


    public void read(ZipPackage zipPackage, PackagePart packagePart,
                     Workbook book, SSReader iReader) throws Exception {
        this.zipPackage = zipPackage;
        this.book = book;
        this.iReader = iReader;


        getSheetsProp(packagePart);


        String id;
        for (int i = 0; i < sheetIndexList.size(); i++) {
            Sheet sheet = new Sheet();
            sheet.setWorkbook(book);

            id = sheetIndexList.get(i);
            sheet.setSheetName(sheetNameList.get(id));

            book.addSheet(i, sheet);
        }


        worksheetRelCollection = packagePart.getRelationshipsByType(
                PackageRelationshipTypes.WORKSHEET_PART);

        chartsheetRelCollection = packagePart.getRelationshipsByType(
                PackageRelationshipTypes.CHARTSHEET_PART);



        class WorkbookReaderHandler extends ReaderHandler {
            private WorkbookReader reader;

            private IControl control;

            public WorkbookReaderHandler(IControl control, WorkbookReader reader) {
                this.reader = reader;
                this.control = control;
            }

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MainConstant.HANDLER_MESSAGE_SUCCESS:
                        new SheetThread(control, reader, (Integer) msg.obj).start();
                        break;

                    case MainConstant.HANDLER_MESSAGE_ERROR:
                    case MainConstant.HANDLER_MESSAGE_DISPOSE: {
                        dispose();
                        reader = null;
                    }
                    break;
                }
            }
        }

        WorkbookReaderHandler handler = new WorkbookReaderHandler(iReader.getControl(), this);
        book.setReaderHandler(handler);

        Message msg = new Message();
        msg.what = MainConstant.HANDLER_MESSAGE_SUCCESS;
        msg.obj = (Integer) 0;

        handler.handleMessage(msg);
    }


    private void readSheetInSlideWindow(IControl control, int currentsheet) throws Exception {
        synchronized (book) {

            iReader.abortCurrentReading();

            Thread.sleep(50);


            for (int i = currentsheet - WINDOWWIDTH; i <= currentsheet + WINDOWWIDTH; i++) {
                if (i >= 0 && book.getSheet(i) != null && !book.getSheet(i).isAccomplished()) {
                    book.getSheet(i).setState(Sheet.State_Reading);
                }
            }
        }


        synchronized (book) {

            if (currentsheet >= 0 && book.getSheet(currentsheet) != null && !book.getSheet(currentsheet).isAccomplished()) {
                readSheet(control, currentsheet);
            }


            for (int i = currentsheet - WINDOWWIDTH; i <= currentsheet + WINDOWWIDTH; i++) {
                if (i >= 0 && book.getSheet(i) != null && !book.getSheet(i).isAccomplished()) {
                    readSheet(control, i);
                }
            }
        }


    }


    private void readSheet(IControl control, int index) throws Exception {
        PackageRelationship rel = worksheetRelCollection.getRelationshipByID(sheetIndexList.get(index));
        short sheetType = Sheet.TYPE_WORKSHEET;
        if (rel == null) {
            rel = chartsheetRelCollection.getRelationshipByID(sheetIndexList.get(index));
            sheetType = Sheet.TYPE_CHARTSHEET;
        }
        if (rel == null) {
            return;
        }
        PackagePart sheetPart = zipPackage.getPart(rel.getTargetURI());

        if (sheetPart != null) {

            {
                book.getSheet(index).setSheetType(sheetType);
                SheetReader.instance().getSheet(control, zipPackage, book.getSheet(index), sheetPart, iReader);
            }

        }
    }


    private void getSheetsProp(PackagePart documentPart) throws Exception {
        if (sheetIndexList != null) {
            sheetIndexList.clear();
        } else {
            sheetIndexList = new HashMap<Integer, String>(5);
        }

        if (sheetNameList != null) {
            sheetNameList.clear();
        } else {
            sheetNameList = new HashMap<String, String>(5);
        }

        tempIndex = 0;

        SAXReader saxreader = new SAXReader();
        try {
            WorkBookSaxHandler handler = new WorkBookSaxHandler();
            saxreader.addHandler("/workbook/workbookPr", handler);
            saxreader.addHandler("/workbook/sheets/sheet", handler);

            InputStream in = documentPart.getInputStream();
            saxreader.read(in);
            in.close();
        } finally {
            saxreader.resetHandlers();
        }
    }


    public boolean searchContent(ZipPackage zipPackage, IReader iReader, PackagePart packagePart, String key) throws Exception {

        if (searchContent_SheetName(packagePart, key)) {
            return true;
        } else {

            this.zipPackage = zipPackage;


            worksheetRelCollection = packagePart.getRelationshipsByType(
                    PackageRelationshipTypes.WORKSHEET_PART);
            for (int i = 0; i < worksheetRelCollection.size(); i++) {
                if (searchContent_Sheet(iReader, worksheetRelCollection.getRelationship(i), key)) {
                    dispose();
                    return true;
                }
            }

            return false;
        }
    }


    private boolean searchContent_SheetName(PackagePart documentPart, String key) throws Exception {
        SAXReader saxreader = new SAXReader();
        InputStream in = documentPart.getInputStream();
        Document poiXls = saxreader.read(in);
        in.close();



        Element root = poiXls.getRootElement();
        Element sheetsElement = root.element("sheets");

        @SuppressWarnings("unchecked")
        Iterator<Element> iter = sheetsElement.elementIterator();
        Element ele;
        while (iter.hasNext()) {
            ele = iter.next();
            if (ele.attributeValue("name").toLowerCase().contains(key)) {
                return true;
            }
        }

        return false;
    }


    private boolean searchContent_Sheet(IReader iReader, PackageRelationship sheetsRel, String key) throws Exception {
        PackagePart sheetPart = zipPackage.getPart(sheetsRel.getTargetURI());
        if (sheetPart != null) {
            return SheetReader.instance().searchContent(zipPackage, iReader, sheetPart, key);
        }

        return false;
    }

    public void dispose() {
        zipPackage = null;

        book = null;
        iReader = null;

        if (sheetNameList != null) {
            sheetNameList.clear();
            sheetNameList = null;
        }

        if (sheetIndexList != null) {
            sheetIndexList.clear();
            sheetIndexList = null;
        }

        if (worksheetRelCollection != null) {
            worksheetRelCollection.clear();
            worksheetRelCollection = null;
        }

        if (chartsheetRelCollection != null) {
            chartsheetRelCollection.clear();
            chartsheetRelCollection = null;
        }
    }

    class SheetThread extends Thread {
        private WorkbookReader reader;
        private int sheetIndex;

        ;
        private IControl control;
        public SheetThread(IControl control, WorkbookReader reader, int sheetIndex) {
            this.reader = reader;
            this.sheetIndex = sheetIndex;
            this.control = control;
        }

        public void run() {
            try {
                reader.readSheetInSlideWindow(control, sheetIndex);
            } catch (OutOfMemoryError e) {
                control.getSysKit().getErrorKit().writerLog(e, true);
                reader.dispose();
            } catch (Exception e) {
                control.getSysKit().getErrorKit().writerLog(e, true);
                reader.dispose();
            } finally {
                reader = null;
            }
        }
    }


    class WorkBookSaxHandler implements ElementHandler {


        public void onStart(ElementPath elementPath) {

        }


        public void onEnd(ElementPath elementPath) {
            if (iReader.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            Element elem = elementPath.getCurrent();
            String name = elem.getName();
            if (name.equals("sheet")) {
                String id;
                String sheetName;
                id = elem.attributeValue("id");
                sheetName = elem.attributeValue("name");

                sheetIndexList.put(tempIndex, id);
                sheetNameList.put(id, sheetName);
                tempIndex++;
            } else if (name.equals("workbookPr")) {
                boolean usingDate1904 = false;
                if (elem.attributeValue("date1904") != null) {
                    usingDate1904 = (Integer.parseInt(elem.attributeValue("date1904")) != 0);
                }
                book.setUsing1904DateWindowing(usingDate1904);
            }

            elem.detach();
        }

    }
}
