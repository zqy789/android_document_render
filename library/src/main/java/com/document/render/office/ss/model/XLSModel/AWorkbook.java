
package com.document.render.office.ss.model.XLSModel;

import android.os.Message;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.fc.hssf.OldExcelFormatException;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;
import com.document.render.office.fc.hssf.model.InternalSheet;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.FontRecord;
import com.document.render.office.fc.hssf.record.LabelRecord;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.hssf.record.PaletteRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordFactory;
import com.document.render.office.fc.hssf.usermodel.HSSFDataFormat;
import com.document.render.office.fc.hssf.usermodel.HSSFName;
import com.document.render.office.fc.poifs.filesystem.DirectoryNode;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;
import com.document.render.office.fc.xls.SSReader;
import com.document.render.office.simpletext.font.Font;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.util.ColorUtil;
import com.document.render.office.system.AbstractReader;
import com.document.render.office.system.IControl;
import com.document.render.office.system.ReaderHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class AWorkbook extends Workbook implements com.document.render.office.fc.ss.usermodel.Workbook {



    public final static int INITIAL_CAPACITY = 3;

    ;
    public final static int AUTOMATIC_COLOR = 0x40;

    private static final String[] WORKBOOK_DIR_ENTRY_NAMES = {"Workbook",
            "WORKBOOK",};

    private UDFFinder _udfFinder = UDFFinder.DEFAULT;
    private InternalWorkbook workbook;


    private ArrayList<HSSFName> names;
    private int currentSheet;
    private SSReader iAbortListener;

    public AWorkbook(InputStream s, SSReader iAbortListener) throws IOException {
        super(true);

        this.iAbortListener = iAbortListener;

        DirectoryNode directory = new POIFSFileSystem(s).getRoot();

        String workbookName = getWorkbookDirEntryName(directory);



        InputStream stream = directory.createDocumentInputStream(workbookName);

        List<Record> records = RecordFactory.createRecords(stream, iAbortListener);

        workbook = InternalWorkbook.createWorkbook(records, iAbortListener);

        int recOffset = workbook.getNumRecords();

        int size = workbook.getSSTUniqueStringSize();
        for (int i = 0; i < size; i++) {
            addSharedString(i, workbook.getSSTString(i));
        }

        convertLabelRecords(records, recOffset);

        isUsing1904DateWindowing = workbook.isUsing1904DateWindowing();


        PaletteRecord palette = workbook.getCustomPalette();
        int index = PaletteRecord.FIRST_COLOR_INDEX;
        addColor(index++, ColorUtil.rgb(0, 0, 0));

        byte[] color = palette.getColor(index);
        while (color != null) {
            addColor(index++, ColorUtil.rgb(color[0], color[1], color[2]));
            color = palette.getColor(index);
        }


        processCellStyle(workbook);

        RecordStream rs = new RecordStream(records, recOffset);
        int sheetIndex = 0;
        while (rs.hasNext()) {
            InternalSheet internalSheet = InternalSheet.createSheet(rs, iAbortListener);
            ASheet sheet = new ASheet(this, internalSheet);
            sheet.setSheetName(workbook.getSheetName(sheetIndex));
            if (internalSheet.isChartSheet()) {
                sheet.setSheetType(Sheet.TYPE_CHARTSHEET);
            }

            sheets.put(sheetIndex++, sheet);
        }
        records.clear();
        records = null;

        names = new ArrayList<HSSFName>(INITIAL_CAPACITY);
        for (int i = 0; i < workbook.getNumNames(); ++i) {
            NameRecord nameRecord = workbook.getNameRecord(i);
            HSSFName name = new HSSFName(this, nameRecord,
                    workbook.getNameCommentRecord(nameRecord));

            names.add(name);
        }


        processSheet();

    }

    public static String getWorkbookDirEntryName(DirectoryNode directory) {

        String[] potentialNames = WORKBOOK_DIR_ENTRY_NAMES;
        for (int i = 0; i < potentialNames.length; i++) {
            String wbName = potentialNames[i];
            try {
                directory.getEntry(wbName);
                return wbName;
            } catch (FileNotFoundException e) {

            }
        }


        try {
            directory.getEntry("Book");
            throw new OldExcelFormatException(
                    "The supplied spreadsheet seems to be Excel 5.0/7.0 (BIFF5) format. "
                            + "POI only supports BIFF8 format (from Excel versions 97/2000/XP/2003)");
        } catch (FileNotFoundException e) {

        }

        throw new IllegalArgumentException(
                "The supplied POIFSFileSystem does not contain a BIFF8 'Workbook' entry. "
                        + "Is it really an excel file?");
    }


    private void processShapesBySheetIndex(IControl control, int sheetIndex) {
        ASheet sheet = (ASheet) sheets.get(sheetIndex);
        try {
            if (sheet.getState() != Sheet.State_Accomplished) {
                sheet.processSheetShapes(control);

                sheet.setState(Sheet.State_Accomplished);
            }
        } catch (Exception e) {
            sheet.setState(Sheet.State_Accomplished);
        }

    }


    private void processSheet() {
        class WorkbookReaderHandler extends ReaderHandler {
            private AWorkbook book;

            public WorkbookReaderHandler(AWorkbook book) {
                this.book = book;
            }

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MainConstant.HANDLER_MESSAGE_SUCCESS:
                        currentSheet = (Integer) msg.obj;
                        if (sheets.get(currentSheet).getState() != Sheet.State_Accomplished) {
                            new ShapesThread(book, sheets, currentSheet, iAbortListener).start();
                        }

                        break;

                    case MainConstant.HANDLER_MESSAGE_ERROR:
                    case MainConstant.HANDLER_MESSAGE_DISPOSE:
                        book = null;
                        break;
                }
            }
        }

        readerHandler = new WorkbookReaderHandler(this);

        Message msg = new Message();
        msg.what = MainConstant.HANDLER_MESSAGE_SUCCESS;
        msg.obj = (Integer) 0;

        readerHandler.handleMessage(msg);
    }



    private void convertLabelRecords(List records, int offset) {

        for (int k = offset; k < records.size(); k++) {
            Record rec = (Record) records.get(k);

            if (rec.getSid() == LabelRecord.sid) {
                LabelRecord oldrec = (LabelRecord) rec;
                sharedString.put(sharedString.size(), oldrec.getValue());
            }
        }
    }


    private void processCellStyle(InternalWorkbook workbook) {
        processFont(workbook);

        short styleIndex = 0;
        short cellStyleCnt = (short) workbook.getNumExFormats();
        ExtendedFormatRecord format;
        while (styleIndex < cellStyleCnt) {
            format = workbook.getExFormatAt(styleIndex);
            if (format == null) {
                continue;
            }

            CellStyle style = new CellStyle();

            style.setIndex(styleIndex);

            style.setNumberFormatID(format.getFormatIndex());

            style.setFormatCode(HSSFDataFormat.getFormatCode(workbook, format.getFormatIndex()));

            style.setFontIndex(format.getFontIndex());

            style.setHidden(format.isHidden());

            style.setLocked(format.isLocked());

            style.setWrapText(format.getWrapText());

            style.setHorizontalAlign(format.getAlignment());

            style.setVerticalAlign(format.getVerticalAlignment());

            style.setRotation(format.getRotation());

            style.setIndent(format.getIndent());


            style.setBorderLeft(format.getBorderLeft());
            short colorIndex = format.getLeftBorderPaletteIdx();
            if (colorIndex == AUTOMATIC_COLOR) {
                colorIndex = PaletteRecord.FIRST_COLOR_INDEX;
            }
            style.setBorderLeftColorIdx(colorIndex);


            style.setBorderRight(format.getBorderRight());
            colorIndex = format.getRightBorderPaletteIdx();
            if (colorIndex == AUTOMATIC_COLOR) {
                colorIndex = PaletteRecord.FIRST_COLOR_INDEX;
            }
            style.setBorderRightColorIdx(colorIndex);


            style.setBorderTop(format.getBorderTop());
            colorIndex = format.getTopBorderPaletteIdx();
            if (colorIndex == AUTOMATIC_COLOR) {
                colorIndex = PaletteRecord.FIRST_COLOR_INDEX;
            }
            style.setBorderTopColorIdx(colorIndex);


            style.setBorderBottom(format.getBorderBottom());
            colorIndex = format.getBottomBorderPaletteIdx();
            if (colorIndex == AUTOMATIC_COLOR) {
                colorIndex = PaletteRecord.FIRST_COLOR_INDEX;
            }
            style.setBorderBottomColorIdx(colorIndex);



            colorIndex = format.getFillBackground();
            style.setBgColor(getColor(colorIndex));


            colorIndex = format.getFillForeground();
            if (colorIndex == AUTOMATIC_COLOR) {
                colorIndex = PaletteRecord.FIRST_COLOR_INDEX + 1;
            }
            style.setFgColor(getColor(colorIndex));


            style.setFillPatternType((byte) (format.getAdtlFillPattern() - 1));

            addCellStyle(styleIndex++, style);
        }
    }


    private void processFont(InternalWorkbook workbook) {
        int numFont = workbook.getNumberOfFontRecords();
        if (numFont <= 4) {
            numFont -= 1;
        }

        int idx = 0;
        while (idx <= numFont) {
            FontRecord fontRec = workbook.getFontRecordAt(idx);

            Font font = new Font();

            font.setIndex(idx);

            font.setName(fontRec.getFontName());

            font.setFontSize((short) (fontRec.getFontHeight() / 20));

            short index = fontRec.getColorPaletteIndex();
            if (index == 32767) {
                index = PaletteRecord.FIRST_COLOR_INDEX;
            }
            font.setColorIndex(index);

            font.setItalic(fontRec.isItalic());

            font.setBold(fontRec.getBoldWeight() > Font.BOLDWEIGHT_NORMAL);

            font.setSuperSubScript((byte) fontRec.getSuperSubScript());

            font.setStrikeline(fontRec.isStruckout());

            font.setUnderline(fontRec.getUnderline());

            addFont(idx++, font);
        }

    }


    public InternalWorkbook getInternalWorkbook() {
        return workbook;
    }


    public UDFFinder getUDFFinder() {
        return _udfFinder;
    }


    public int getNumberOfSheets() {
        return sheets.size();
    }


    public ASheet getSheetAt(int index) {
        if (index < 0 || index >= sheets.size()) {
            return null;
        }
        return (ASheet) sheets.get(index);
    }


    public int getSheetIndex(String name) {
        return workbook.getSheetIndex(name);
    }


    public int getSheetIndex(Sheet sheet) {
        for (int i = 0; i < sheets.size(); i++) {
            if (sheets.get(i) == sheet) {
                return i;
            }
        }
        return -1;
    }

    public int getNumberOfNames() {
        int result = names.size();
        return result;
    }

    public int getNameIndex(String name) {

        for (int k = 0; k < names.size(); k++) {
            String nameName = getNameName(k);

            if (nameName.equalsIgnoreCase(name)) {
                return k;
            }
        }
        return -1;
    }

    public HSSFName getName(String name) {
        int nameIndex = getNameIndex(name);
        if (nameIndex < 0) {
            return null;
        }
        return (HSSFName) names.get(nameIndex);
    }

    public HSSFName getNameAt(int nameIndex) {
        int nNames = names.size();
        if (nNames < 1) {
            throw new IllegalStateException("There are no defined names in this workbook");
        }
        if (nameIndex < 0 || nameIndex > nNames) {
            throw new IllegalArgumentException("Specified name index " + nameIndex
                    + " is outside the allowable range (0.." + (nNames - 1) + ").");
        }
        return (HSSFName) names.get(nameIndex);
    }

    public NameRecord getNameRecord(int nameIndex) {
        return workbook.getNameRecord(nameIndex);
    }


    public String getNameName(int index) {
        String result = getNameAt(index).getNameName();

        return result;
    }


    public AbstractReader getAbstractReader() {
        return iAbortListener;
    }


    public void dispose() {
        destroy();

        workbook = null;

        if (names != null && names.size() > 0) {
            Iterator<HSSFName> iter = names.iterator();
            while (iter.hasNext()) {
                iter.next().dispose();
            }

            names.clear();
            names = null;
        }
        _udfFinder = null;
        iAbortListener = null;
    }

    static class ShapesThread extends Thread {
        private AWorkbook book;
        private Map<Integer, Sheet> sheets;

        ;
        private SSReader iAbortListener;
        private int sheetIndex;
        private IControl control;
        public ShapesThread(AWorkbook book, Map<Integer, Sheet> sheets, int sheetIndex, SSReader iAbortListener) {
            this.book = book;
            this.sheets = sheets;
            this.sheetIndex = sheetIndex;
            this.iAbortListener = iAbortListener;
            this.control = iAbortListener.getControl();
        }

        public void run() {
            try {
                if (sheetIndex >= 0 && iAbortListener != null) {
                    iAbortListener.abortCurrentReading();
                    sleep(50);

                    ((ASheet) book.getSheet(sheetIndex)).processSheet(iAbortListener);

                    processOtherSheets();
                }
            } catch (OutOfMemoryError e) {
                book.dispose();
                iAbortListener.dispose();
                iAbortListener.getControl().getSysKit().getErrorKit().writerLog(e, true);
            } catch (Exception e) {
                book.dispose();
                iAbortListener.dispose();
                iAbortListener.getControl().getSysKit().getErrorKit().writerLog(e, true);
            } finally {
                book = null;
                sheets = null;

                iAbortListener = null;
                control = null;
            }

        }

        private void processOtherSheets() {
            Iterator<Integer> iter = sheets.keySet().iterator();
            while (iter.hasNext()) {
                ((ASheet) book.getSheet(iter.next())).processSheet(iAbortListener);
            }

            iter = sheets.keySet().iterator();
            while (iter.hasNext()) {
                book.processShapesBySheetIndex(control, iter.next());
            }
        }
    }
}
