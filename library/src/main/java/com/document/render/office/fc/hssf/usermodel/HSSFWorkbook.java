

package com.document.render.office.fc.hssf.usermodel;

import androidx.annotation.Keep;

import com.document.render.office.fc.POIDocument;
import com.document.render.office.fc.codec.DigestUtils;
import com.document.render.office.fc.ddf.EscherBSERecord;
import com.document.render.office.fc.ddf.EscherBitmapBlip;
import com.document.render.office.fc.ddf.EscherBlipRecord;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.hssf.OldExcelFormatException;
import com.document.render.office.fc.hssf.formula.FormulaShifter;
import com.document.render.office.fc.hssf.formula.SheetNameFormatter;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemFuncPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.UnionPtg;
import com.document.render.office.fc.hssf.formula.udf.AggregatingUDFFinder;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;
import com.document.render.office.fc.hssf.model.DrawingManager2;
import com.document.render.office.fc.hssf.model.InternalSheet;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.AbstractEscherHolderRecord;
import com.document.render.office.fc.hssf.record.BackupRecord;
import com.document.render.office.fc.hssf.record.DrawingGroupRecord;
import com.document.render.office.fc.hssf.record.EmbeddedObjectRefSubRecord;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.FontRecord;
import com.document.render.office.fc.hssf.record.LabelRecord;
import com.document.render.office.fc.hssf.record.LabelSSTRecord;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.record.RecalcIdRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordBase;
import com.document.render.office.fc.hssf.record.RecordFactory;
import com.document.render.office.fc.hssf.record.SSTRecord;
import com.document.render.office.fc.hssf.record.SubRecord;
import com.document.render.office.fc.hssf.record.UnknownRecord;
import com.document.render.office.fc.hssf.record.aggregates.RecordAggregate.RecordVisitor;
import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.hssf.util.CellReference;
import com.document.render.office.fc.poifs.filesystem.DirectoryNode;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;
import com.document.render.office.fc.ss.usermodel.IRow.MissingCellPolicy;
import com.document.render.office.fc.ss.util.WorkbookUtil;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;



public final class HSSFWorkbook extends POIDocument implements com.document.render.office.fc.ss.usermodel.Workbook {


    public final static int INITIAL_CAPACITY = 3;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");
    private static final int MAX_ROW = 0xFFFF;
    private static final short MAX_COLUMN = (short) 0x00FF;



    private static final int MAX_STYLES = 4030;

    private static final String[] WORKBOOK_DIR_ENTRY_NAMES = {"Workbook",
            "WORKBOOK",};


    protected List<HSSFSheet> _sheets;


    private InternalWorkbook workbook;


    private ArrayList<HSSFName> names;

    private Hashtable fonts;

    private boolean preserveNodes;

    private HSSFDataFormat formatter;



    private MissingCellPolicy missingCellPolicy = HSSFRow.RETURN_NULL_AND_BLANK;

    private UDFFinder _udfFinder = UDFFinder.DEFAULT;

    private HSSFPalette palette;


    public HSSFWorkbook() {
        this(InternalWorkbook.createWorkbook());
    }

    private HSSFWorkbook(InternalWorkbook book) {
        super((DirectoryNode) null);
        workbook = book;
        _sheets = new ArrayList<HSSFSheet>(INITIAL_CAPACITY);
        names = new ArrayList<HSSFName>(INITIAL_CAPACITY);
    }

    public HSSFWorkbook(POIFSFileSystem fs) throws IOException {
        this(fs, false);
    }


    public HSSFWorkbook(POIFSFileSystem fs, boolean preserveNodes) throws IOException {
        this(fs.getRoot(), fs, preserveNodes);
    }


    public HSSFWorkbook(DirectoryNode directory, POIFSFileSystem fs, boolean preserveNodes)
            throws IOException {
        this(directory, preserveNodes);
    }


    public HSSFWorkbook(DirectoryNode directory, boolean preserveNodes) throws IOException {
        super(directory);
        String workbookName = getWorkbookDirEntryName(directory);

        this.preserveNodes = preserveNodes;



        if (!preserveNodes) {
            this.directory = null;
        }

        _sheets = new ArrayList<HSSFSheet>(INITIAL_CAPACITY);
        names = new ArrayList<HSSFName>(INITIAL_CAPACITY);



        InputStream stream = directory.createDocumentInputStream(workbookName);

        List<Record> records = RecordFactory.createRecords(stream);

        workbook = InternalWorkbook.createWorkbook(records);
        setPropertiesFromWorkbook(workbook);
        int recOffset = workbook.getNumRecords();


        convertLabelRecords(records, recOffset);
        RecordStream rs = new RecordStream(records, recOffset);
        while (rs.hasNext()) {
            InternalSheet sheet = InternalSheet.createSheet(rs);
            _sheets.add(new HSSFSheet(this, sheet));
        }

        for (int i = 0; i < workbook.getNumNames(); ++i) {




        }
    }

    public HSSFWorkbook(InputStream s) throws IOException {
        this(s, true);
    }



    public HSSFWorkbook(InputStream s, boolean preserveNodes) throws IOException {
        this(new POIFSFileSystem(s), preserveNodes);
    }

    @Keep
    public static HSSFWorkbook create(InternalWorkbook book) {
        return new HSSFWorkbook(book);
    }

    private static String getWorkbookDirEntryName(DirectoryNode directory) {

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



    private void setPropertiesFromWorkbook(InternalWorkbook book) {
        this.workbook = book;


    }



    private void convertLabelRecords(List records, int offset) {

        for (int k = offset; k < records.size(); k++) {
            Record rec = (Record) records.get(k);

            if (rec.getSid() == LabelRecord.sid) {
                LabelRecord oldrec = (LabelRecord) rec;

                records.remove(k);
                LabelSSTRecord newrec = new LabelSSTRecord();
                int stringid = workbook.addSSTString(new UnicodeString(oldrec.getValue()));

                newrec.setRow(oldrec.getRow());
                newrec.setColumn(oldrec.getColumn());
                newrec.setXFIndex(oldrec.getXFIndex());
                newrec.setSSTIndex(stringid);
                records.add(k, newrec);
            }
        }

    }


    public MissingCellPolicy getMissingCellPolicy() {
        return missingCellPolicy;
    }


    public void setMissingCellPolicy(MissingCellPolicy missingCellPolicy) {
        this.missingCellPolicy = missingCellPolicy;
    }



    public void setSheetOrder(String sheetname, int pos) {
        int oldSheetIndex = getSheetIndex(sheetname);
        _sheets.add(pos, _sheets.remove(oldSheetIndex));
        workbook.setSheetOrder(sheetname, pos);

        FormulaShifter shifter = FormulaShifter.createForSheetShift(oldSheetIndex, pos);
        for (HSSFSheet sheet : _sheets) {
            sheet.getSheet().updateFormulasAfterCellShift(shifter, -1);
        }

        workbook.updateNamesAfterCellShift(shifter);

    }

    private void validateSheetIndex(int index) {
        int lastSheetIx = _sheets.size() - 1;
        if (index < 0 || index > lastSheetIx) {
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range (0.."
                    + lastSheetIx + ")");
        }
    }

    public void setSelectedTabs(int[] indexes) {

        for (int i = 0; i < indexes.length; i++) {
            validateSheetIndex(indexes[i]);
        }
        int nSheets = _sheets.size();
        for (int i = 0; i < nSheets; i++) {
            boolean bSelect = false;
            for (int j = 0; j < indexes.length; j++) {
                if (indexes[j] == i) {
                    bSelect = true;
                    break;
                }

            }
            getSheetAt(i).setSelected(bSelect);
        }
        workbook.getWindowOne().setNumSelectedTabs((short) indexes.length);
    }


    public void setActiveSheet(int index) {

        validateSheetIndex(index);
        int nSheets = _sheets.size();
        for (int i = 0; i < nSheets; i++) {
            getSheetAt(i).setActive(i == index);
        }
        workbook.getWindowOne().setActiveSheetIndex(index);
    }


    public int getActiveSheetIndex() {
        return workbook.getWindowOne().getActiveSheetIndex();
    }


    public short getSelectedTab() {
        return (short) getActiveSheetIndex();
    }


    public void setSelectedTab(int index) {

        validateSheetIndex(index);
        int nSheets = _sheets.size();
        for (int i = 0; i < nSheets; i++) {
            getSheetAt(i).setSelected(i == index);
        }
        workbook.getWindowOne().setNumSelectedTabs((short) 1);
    }


    public void setSelectedTab(short index) {
        setSelectedTab((int) index);
    }


    public int getFirstVisibleTab() {
        return workbook.getWindowOne().getFirstVisibleTab();
    }


    public void setFirstVisibleTab(int index) {
        workbook.getWindowOne().setFirstVisibleTab(index);
    }


    public short getDisplayedTab() {
        return (short) getFirstVisibleTab();
    }


    public void setDisplayedTab(short index) {
        setFirstVisibleTab(index);
    }


    public void setSheetName(int sheetIx, String name) {
        if (name == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }

        if (workbook.doesContainsSheetName(name, sheetIx)) {
            throw new IllegalArgumentException(
                    "The workbook already contains a sheet with this name");
        }
        validateSheetIndex(sheetIx);
        workbook.setSheetName(sheetIx, name);
    }


    public String getSheetName(int sheetIndex) {
        validateSheetIndex(sheetIndex);
        return workbook.getSheetName(sheetIndex);
    }

    public boolean isHidden() {
        return workbook.getWindowOne().getHidden();
    }

    public void setHidden(boolean hiddenFlag) {
        workbook.getWindowOne().setHidden(hiddenFlag);
    }

    public boolean isSheetHidden(int sheetIx) {
        validateSheetIndex(sheetIx);
        return workbook.isSheetHidden(sheetIx);
    }

    public boolean isSheetVeryHidden(int sheetIx) {
        validateSheetIndex(sheetIx);
        return workbook.isSheetVeryHidden(sheetIx);
    }

    public void setSheetHidden(int sheetIx, boolean hidden) {
        validateSheetIndex(sheetIx);
        workbook.setSheetHidden(sheetIx, hidden);
    }

    public void setSheetHidden(int sheetIx, int hidden) {
        validateSheetIndex(sheetIx);
        WorkbookUtil.validateSheetState(hidden);
        workbook.setSheetHidden(sheetIx, hidden);
    }


    public int getSheetIndex(String name) {
        return workbook.getSheetIndex(name);
    }


    public int getSheetIndex(com.document.render.office.fc.ss.usermodel.Sheet sheet) {
        for (int i = 0; i < _sheets.size(); i++) {
            if (_sheets.get(i) == sheet) {
                return i;
            }
        }
        return -1;
    }


    public int getExternalSheetIndex(int internalSheetIndex) {
        return workbook.checkExternSheet(internalSheetIndex);
    }


    public String findSheetNameFromExternSheet(int externSheetIndex) {

        return workbook.findSheetNameFromExternSheet(externSheetIndex);
    }


    public String resolveNameXText(int refIndex, int definedNameIndex) {

        return workbook.resolveNameXText(refIndex, definedNameIndex);
    }



    public HSSFSheet createSheet() {
        HSSFSheet sheet = new HSSFSheet(this);

        _sheets.add(sheet);
        workbook.setSheetName(_sheets.size() - 1, "Sheet" + (_sheets.size() - 1));
        boolean isOnlySheet = _sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }



    public HSSFSheet cloneSheet(int sheetIndex) {
        validateSheetIndex(sheetIndex);
        HSSFSheet srcSheet = _sheets.get(sheetIndex);
        String srcName = workbook.getSheetName(sheetIndex);
        HSSFSheet clonedSheet = srcSheet.cloneSheet(this);
        clonedSheet.setSelected(false);
        clonedSheet.setActive(false);

        String name = getUniqueSheetName(srcName);
        int newSheetIndex = _sheets.size();
        _sheets.add(clonedSheet);
        workbook.setSheetName(newSheetIndex, name);


        int filterDbNameIndex = findExistingBuiltinNameRecordIdx(sheetIndex,
                NameRecord.BUILTIN_FILTER_DB);
        if (filterDbNameIndex != -1) {



        }

        workbook.cloneDrawings(clonedSheet.getSheet());

        return clonedSheet;
    }

    private String getUniqueSheetName(String srcName) {
        int uniqueIndex = 2;
        String baseName = srcName;
        int bracketPos = srcName.lastIndexOf('(');
        if (bracketPos > 0 && srcName.endsWith(")")) {
            String suffix = srcName.substring(bracketPos + 1, srcName.length() - ")".length());
            try {
                uniqueIndex = Integer.parseInt(suffix.trim());
                uniqueIndex++;
                baseName = srcName.substring(0, bracketPos).trim();
            } catch (NumberFormatException e) {

            }
        }
        while (true) {

            String index = Integer.toString(uniqueIndex++);
            String name;
            if (baseName.length() + index.length() + 2 < 31) {
                name = baseName + " (" + index + ")";
            } else {
                name = baseName.substring(0, 31 - index.length() - 2) + "(" + index + ")";
            }


            if (workbook.getSheetIndex(name) == -1) {
                return name;
            }
        }
    }


    public HSSFSheet createSheet(String sheetname) {
        if (sheetname == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }

        if (workbook.doesContainsSheetName(sheetname, _sheets.size()))
            throw new IllegalArgumentException("The workbook already contains a sheet of this name");

        HSSFSheet sheet = new HSSFSheet(this);

        workbook.setSheetName(_sheets.size(), sheetname);
        _sheets.add(sheet);
        boolean isOnlySheet = _sheets.size() == 1;
        sheet.setSelected(isOnlySheet);
        sheet.setActive(isOnlySheet);
        return sheet;
    }



    public int getNumberOfSheets() {
        return _sheets.size();
    }

    public int getSheetIndexFromExternSheetIndex(int externSheetNumber) {
        return workbook.getSheetIndexFromExternSheetIndex(externSheetNumber);
    }

    private HSSFSheet[] getSheets() {
        HSSFSheet[] result = new HSSFSheet[_sheets.size()];
        _sheets.toArray(result);
        return result;
    }



    public HSSFSheet getSheetAt(int index) {
        validateSheetIndex(index);
        return (HSSFSheet) _sheets.get(index);
    }


    public boolean isEmpty() {
        return _sheets.size() == 0;
    }



    public HSSFSheet getSheet(String name) {
        HSSFSheet retval = null;

        for (int k = 0; k < _sheets.size(); k++) {
            String sheetname = workbook.getSheetName(k);

            if (sheetname.equalsIgnoreCase(name)) {
                retval = (HSSFSheet) _sheets.get(k);
            }
        }
        return retval;
    }


    public void removeSheetAt(int index) {
        validateSheetIndex(index);
        boolean wasActive = getSheetAt(index).isActive();
        boolean wasSelected = getSheetAt(index).isSelected();

        _sheets.remove(index);
        workbook.removeSheet(index);


        int nSheets = _sheets.size();
        if (nSheets < 1) {

            return;
        }

        int newSheetIndex = index;
        if (newSheetIndex >= nSheets) {
            newSheetIndex = nSheets - 1;
        }
        if (wasActive) {
            setActiveSheet(newSheetIndex);
        }

        if (wasSelected) {
            boolean someOtherSheetIsStillSelected = false;
            for (int i = 0; i < nSheets; i++) {
                if (getSheetAt(i).isSelected()) {
                    someOtherSheetIsStillSelected = true;
                    break;
                }
            }
            if (!someOtherSheetIsStillSelected) {
                setSelectedTab(newSheetIndex);
            }
        }
    }



    public boolean getBackupFlag() {
        BackupRecord backupRecord = workbook.getBackupRecord();

        return (backupRecord.getBackup() == 0) ? false : true;
    }



    public void setBackupFlag(boolean backupValue) {
        BackupRecord backupRecord = workbook.getBackupRecord();

        backupRecord.setBackup(backupValue ? (short) 1 : (short) 0);
    }


    public void setRepeatingRowsAndColumns(int sheetIndex, int startColumn, int endColumn,
                                           int startRow, int endRow) {

        if (startColumn == -1 && endColumn != -1)
            throw new IllegalArgumentException("Invalid column range specification");
        if (startRow == -1 && endRow != -1)
            throw new IllegalArgumentException("Invalid row range specification");
        if (startColumn < -1 || startColumn >= MAX_COLUMN)
            throw new IllegalArgumentException("Invalid column range specification");
        if (endColumn < -1 || endColumn >= MAX_COLUMN)
            throw new IllegalArgumentException("Invalid column range specification");
        if (startRow < -1 || startRow > MAX_ROW)
            throw new IllegalArgumentException("Invalid row range specification");
        if (endRow < -1 || endRow > MAX_ROW)
            throw new IllegalArgumentException("Invalid row range specification");
        if (startColumn > endColumn)
            throw new IllegalArgumentException("Invalid column range specification");
        if (startRow > endRow)
            throw new IllegalArgumentException("Invalid row range specification");

        HSSFSheet sheet = getSheetAt(sheetIndex);
        short externSheetIndex = getWorkbook().checkExternSheet(sheetIndex);

        boolean settingRowAndColumn = startColumn != -1 && endColumn != -1 && startRow != -1
                && endRow != -1;
        boolean removingRange = startColumn == -1 && endColumn == -1 && startRow == -1
                && endRow == -1;

        int rowColHeaderNameIndex = findExistingBuiltinNameRecordIdx(sheetIndex,
                NameRecord.BUILTIN_PRINT_TITLE);
        if (removingRange) {
            if (rowColHeaderNameIndex >= 0) {
                workbook.removeName(rowColHeaderNameIndex);
            }
            return;
        }
        boolean isNewRecord;
        NameRecord nameRecord;
        if (rowColHeaderNameIndex < 0) {

            nameRecord = workbook.createBuiltInName(NameRecord.BUILTIN_PRINT_TITLE, sheetIndex + 1);
            isNewRecord = true;
        } else {
            nameRecord = workbook.getNameRecord(rowColHeaderNameIndex);
            isNewRecord = false;
        }

        List temp = new ArrayList();

        if (settingRowAndColumn) {
            final int exprsSize = 2 * 11 + 1;
            temp.add(new MemFuncPtg(exprsSize));
        }
        if (startColumn >= 0) {
            Area3DPtg colArea = new Area3DPtg(0, MAX_ROW, startColumn, endColumn, false, false,
                    false, false, externSheetIndex);
            temp.add(colArea);
        }
        if (startRow >= 0) {
            Area3DPtg rowArea = new Area3DPtg(startRow, endRow, 0, MAX_COLUMN, false, false, false,
                    false, externSheetIndex);
            temp.add(rowArea);
        }
        if (settingRowAndColumn) {
            temp.add(UnionPtg.instance);
        }
        Ptg[] ptgs = new Ptg[temp.size()];
        temp.toArray(ptgs);
        nameRecord.setNameDefinition(ptgs);

        if (isNewRecord) {



        }

        HSSFPrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setValidSettings(false);

        sheet.setActive(true);
    }

    private int findExistingBuiltinNameRecordIdx(int sheetIndex, byte builtinCode) {
        for (int defNameIndex = 0; defNameIndex < names.size(); defNameIndex++) {
            NameRecord r = workbook.getNameRecord(defNameIndex);
            if (r == null) {
                throw new RuntimeException("Unable to find all defined names to iterate over");
            }
            if (!r.isBuiltInName() || r.getBuiltInName() != builtinCode) {
                continue;
            }
            if (r.getSheetNumber() - 1 == sheetIndex) {
                return defNameIndex;
            }
        }
        return -1;
    }



    public HSSFFont createFont() {
        FontRecord font = workbook.createNewFont();
        short fontindex = (short) (getNumberOfFonts() - 1);

        if (fontindex > 3) {
            fontindex++;
        }
        if (fontindex == Short.MAX_VALUE) {
            throw new IllegalArgumentException("Maximum number of fonts was exceeded");
        }



        return getFontAt(fontindex);
    }


    public HSSFFont findFont(short boldWeight, short color, short fontHeight, String name,
                             boolean italic, boolean strikeout, short typeOffset, byte underline) {
        for (short i = 0; i <= getNumberOfFonts(); i++) {

            if (i == 4)
                continue;

            HSSFFont hssfFont = getFontAt(i);
            if (hssfFont.getBoldweight() == boldWeight && hssfFont.getColor() == color
                    && hssfFont.getFontHeight() == fontHeight && hssfFont.getFontName().equals(name)
                    && hssfFont.getItalic() == italic && hssfFont.getStrikeout() == strikeout
                    && hssfFont.getTypeOffset() == typeOffset && hssfFont.getUnderline() == underline) {
                return hssfFont;
            }
        }

        return null;
    }



    public short getNumberOfFonts() {
        return (short) workbook.getNumberOfFontRecords();
    }


    public HSSFFont getFontAt(short idx) {
        if (fonts == null)
            fonts = new Hashtable();




        Short sIdx = Short.valueOf(idx);
        if (fonts.containsKey(sIdx)) {
            return (HSSFFont) fonts.get(sIdx);
        }

        FontRecord font = workbook.getFontRecordAt(idx);
        HSSFFont retval = new HSSFFont(idx, font);
        fonts.put(sIdx, retval);

        return retval;
    }


    protected void resetFontCache() {
        fonts = new Hashtable();
    }



    public HSSFCellStyle createCellStyle() {
        if (workbook.getNumExFormats() == MAX_STYLES) {
            throw new IllegalStateException("The maximum number of cell styles was exceeded. "
                    + "You can define up to 4000 styles in a .xls workbook");
        }
        ExtendedFormatRecord xfr = workbook.createCellXF();
        short index = (short) (getNumCellStyles() - 1);
        HSSFCellStyle style = new HSSFCellStyle(index, xfr, this);

        return style;
    }



    public short getNumCellStyles() {
        return (short) workbook.getNumExFormats();
    }


    public HSSFCellStyle getCellStyleAt(short idx) {
        ExtendedFormatRecord xfr = workbook.getExFormatAt(idx);
        if (xfr != null) {
            return new HSSFCellStyle(idx, xfr, this);
        } else {
            return null;
        }
    }



    public void write(OutputStream stream) throws IOException {
        byte[] bytes = getBytes();
        POIFSFileSystem fs = new POIFSFileSystem();



        List<String> excepts = new ArrayList<String>(1);


        fs.createDocument(new ByteArrayInputStream(bytes), "Workbook");


        writeProperties(fs, excepts);

        if (preserveNodes) {

            excepts.add("Workbook");


            excepts.add("WORKBOOK");


            copyNodes(this.directory, fs.getRoot(), excepts);



            fs.getRoot().setStorageClsid(this.directory.getStorageClsid());
        }
        fs.writeFilesystem(stream);
    }


    public byte[] getBytes() {


        HSSFSheet[] sheets = getSheets();
        int nSheets = sheets.length;



        for (int i = 0; i < nSheets; i++) {
            sheets[i].getSheet().preSerialize();
        }

        int totalsize = workbook.getSize();


        SheetRecordCollector[] srCollectors = new SheetRecordCollector[nSheets];
        for (int k = 0; k < nSheets; k++) {
            workbook.setSheetBof(k, totalsize);
            SheetRecordCollector src = new SheetRecordCollector();
            sheets[k].getSheet().visitContainedRecords(src, totalsize);
            totalsize += src.getTotalSize();
            srCollectors[k] = src;
        }

        byte[] retval = new byte[totalsize];
        int pos = workbook.serialize(0, retval);

        for (int k = 0; k < nSheets; k++) {
            SheetRecordCollector src = srCollectors[k];
            int serializedSize = src.serialize(pos, retval);
            if (serializedSize != src.getTotalSize()) {



                throw new IllegalStateException("Actual serialized sheet size (" + serializedSize
                        + ") differs from pre-calculated size (" + src.getTotalSize() + ") for sheet ("
                        + k + ")");

            }
            pos += serializedSize;
        }
        return retval;
    }


    public int addSSTString(String string) {
        return workbook.addSSTString(new UnicodeString(string));
    }


    public int getSSTUniqueStringSize() {
        return workbook.getSSTUniqueStringSize();
    }


    public String getSSTString(int index) {
        return workbook.getSSTString(index).getString();
    }

    public InternalWorkbook getWorkbook() {
        return workbook;
    }

    public int getNumberOfNames() {
        int result = names.size();
        return result;
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
        return getWorkbook().getNameRecord(nameIndex);
    }


    public String getNameName(int index) {
        String result = getNameAt(index).getNameName();

        return result;
    }


    public void setPrintArea(int sheetIndex, String reference) {
        NameRecord name = workbook.getSpecificBuiltinRecord(NameRecord.BUILTIN_PRINT_AREA,
                sheetIndex + 1);

        if (name == null) {
            name = workbook.createBuiltInName(NameRecord.BUILTIN_PRINT_AREA, sheetIndex + 1);

        }
        String[] parts = COMMA_PATTERN.split(reference);
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            SheetNameFormatter.appendFormat(sb, getSheetName(sheetIndex));
            sb.append("!");
            sb.append(parts[i]);
        }


    }


    public void setPrintArea(int sheetIndex, int startColumn, int endColumn, int startRow,
                             int endRow) {


        CellReference cell = new CellReference(startRow, startColumn, true, true);
        String reference = cell.formatAsString();

        cell = new CellReference(endRow, endColumn, true, true);
        reference = reference + ":" + cell.formatAsString();

        setPrintArea(sheetIndex, reference);
    }


    public String getPrintArea(int sheetIndex) {
        NameRecord name = workbook.getSpecificBuiltinRecord(NameRecord.BUILTIN_PRINT_AREA,
                sheetIndex + 1);

        if (name == null) {
            return null;
        }


        return null;
    }


    public void removePrintArea(int sheetIndex) {
        getWorkbook().removeBuiltinRecord(NameRecord.BUILTIN_PRINT_AREA, sheetIndex + 1);
    }


    public HSSFName createName() {







        return null;
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

    public void removeName(int index) {
        names.remove(index);
        workbook.removeName(index);
    }


    public HSSFDataFormat createDataFormat() {
        if (formatter == null)
            formatter = new HSSFDataFormat(workbook);
        return formatter;
    }

    public void removeName(String name) {
        int index = getNameIndex(name);

        removeName(index);
    }

    public HSSFPalette getCustomPalette() {
        if (palette == null) {
            palette = new HSSFPalette(workbook.getCustomPalette());
        }
        return palette;
    }


    public void insertChartRecord() {
        int loc = workbook.findFirstRecordLocBySid(SSTRecord.sid);
        byte[] data = {(byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0xF0, (byte) 0x52, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0xF0, (byte) 0x18,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x08, (byte) 0x00, (byte) 0x00,
                (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x33,
                (byte) 0x00, (byte) 0x0B, (byte) 0xF0, (byte) 0x12, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xBF, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x81,
                (byte) 0x01, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0xC0, (byte) 0x01,
                (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x40, (byte) 0x00, (byte) 0x1E,
                (byte) 0xF1, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x00,
                (byte) 0x00, (byte) 0x08, (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x17,
                (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0xF7, (byte) 0x00, (byte) 0x00, (byte) 0x10,};
        UnknownRecord r = new UnknownRecord((short) 0x00EB, data);
        workbook.getRecords().add(loc, r);
    }


    public void dumpDrawingGroupRecords(boolean fat) {
        DrawingGroupRecord r = (DrawingGroupRecord) workbook
                .findFirstRecordBySid(DrawingGroupRecord.sid);
        r.decode();
        List escherRecords = r.getEscherRecords();
        PrintWriter w = new PrintWriter(System.out);
        for (Iterator iterator = escherRecords.iterator(); iterator.hasNext(); ) {
            EscherRecord escherRecord = (EscherRecord) iterator.next();
            if (fat)
                System.out.println(escherRecord.toString());
            else
                escherRecord.display(w, 0);
        }
        w.flush();
    }

    void initDrawings() {
        DrawingManager2 mgr = workbook.findDrawingGroup();
        if (mgr != null) {
            for (int i = 0; i < getNumberOfSheets(); i++) {
                getSheetAt(i).getDrawingPatriarch();
            }
        } else {
            workbook.createDrawingGroup();
        }
    }


    public int addPicture(byte[] pictureData, int format) {
        initDrawings();

        byte[] uid = DigestUtils.md5(pictureData);
        EscherBitmapBlip blipRecord = new EscherBitmapBlip();
        blipRecord.setRecordId((short) (EscherBitmapBlip.RECORD_ID_START + format));
        switch (format) {
            case PICTURE_TYPE_EMF:
                blipRecord.setOptions(HSSFPictureData.MSOBI_EMF);
                break;
            case PICTURE_TYPE_WMF:
                blipRecord.setOptions(HSSFPictureData.MSOBI_WMF);
                break;
            case PICTURE_TYPE_PICT:
                blipRecord.setOptions(HSSFPictureData.MSOBI_PICT);
                break;
            case PICTURE_TYPE_PNG:
                blipRecord.setOptions(HSSFPictureData.MSOBI_PNG);
                break;
            case HSSFWorkbook.PICTURE_TYPE_JPEG:
                blipRecord.setOptions(HSSFPictureData.MSOBI_JPEG);
                break;
            case HSSFWorkbook.PICTURE_TYPE_DIB:
                blipRecord.setOptions(HSSFPictureData.MSOBI_DIB);
                break;
        }

        blipRecord.setUID(uid);
        blipRecord.setMarker((byte) 0xFF);
        blipRecord.setPictureData(pictureData);

        EscherBSERecord r = new EscherBSERecord();
        r.setRecordId(EscherBSERecord.RECORD_ID);
        r.setOptions((short) (0x0002 | (format << 4)));
        r.setBlipTypeMacOS((byte) format);
        r.setBlipTypeWin32((byte) format);
        r.setUid(uid);
        r.setTag((short) 0xFF);
        r.setSize(pictureData.length + 25);
        r.setRef(1);
        r.setOffset(0);
        r.setBlipRecord(blipRecord);

        return workbook.addBSERecord(r);
    }


    public List<HSSFPictureData> getAllPictures() {

        List<HSSFPictureData> pictures = new ArrayList<HSSFPictureData>();
        Iterator<Record> recordIter = workbook.getRecords().iterator();
        while (recordIter.hasNext()) {
            Record r = recordIter.next();
            if (r instanceof AbstractEscherHolderRecord) {
                ((AbstractEscherHolderRecord) r).decode();
                List<EscherRecord> escherRecords = ((AbstractEscherHolderRecord) r)
                        .getEscherRecords();
                searchForPictures(escherRecords, pictures);
            }
        }
        return pictures;
    }


    private void searchForPictures(List<EscherRecord> escherRecords, List<HSSFPictureData> pictures) {
        for (EscherRecord escherRecord : escherRecords) {

            if (escherRecord instanceof EscherBSERecord) {
                EscherBlipRecord blip = ((EscherBSERecord) escherRecord).getBlipRecord();
                if (blip != null) {

                    HSSFPictureData picture = new HSSFPictureData(blip);
                    pictures.add(picture);
                }

            }


            searchForPictures(escherRecord.getChildRecords(), pictures);
        }

    }


    public boolean isWriteProtected() {
        return this.workbook.isWriteProtected();
    }


    public void writeProtectWorkbook(String password, String username) {
        this.workbook.writeProtectWorkbook(password, username);
    }


    public void unwriteProtectWorkbook() {
        this.workbook.unwriteProtectWorkbook();
    }


    public List<HSSFObjectData> getAllEmbeddedObjects() {
        List<HSSFObjectData> objects = new ArrayList<HSSFObjectData>();
        for (int i = 0; i < getNumberOfSheets(); i++) {
            getAllEmbeddedObjects(getSheetAt(i).getSheet().getRecords(), objects);
        }
        return objects;
    }


    private void getAllEmbeddedObjects(List<RecordBase> records, List<HSSFObjectData> objects) {
        for (RecordBase obj : records) {
            if (obj instanceof ObjRecord) {


                Iterator<SubRecord> subRecordIter = ((ObjRecord) obj).getSubRecords().iterator();
                while (subRecordIter.hasNext()) {
                    SubRecord sub = subRecordIter.next();
                    if (sub instanceof EmbeddedObjectRefSubRecord) {
                        objects.add(new HSSFObjectData((ObjRecord) obj, directory));
                    }
                }
            }
        }
    }

    public HSSFCreationHelper getCreationHelper() {
        return new HSSFCreationHelper(this);
    }


    UDFFinder getUDFFinder() {
        return _udfFinder;
    }


    public void addToolPack(UDFFinder toopack) {
        AggregatingUDFFinder udfs = (AggregatingUDFFinder) _udfFinder;
        udfs.add(toopack);
    }


    public boolean getForceFormulaRecalculation() {
        InternalWorkbook iwb = getWorkbook();
        RecalcIdRecord recalc = (RecalcIdRecord) iwb.findFirstRecordBySid(RecalcIdRecord.sid);
        return recalc != null && recalc.getEngineId() != 0;
    }


    public void setForceFormulaRecalculation(boolean value) {
        InternalWorkbook iwb = getWorkbook();
        RecalcIdRecord recalc = iwb.getRecalcId();
        recalc.setEngineId(0);
    }


    public boolean isUsing1904DateWindowing() {
        return workbook.isUsing1904DateWindowing();
    }


    private static final class SheetRecordCollector implements RecordVisitor {

        private List _list;
        private int _totalSize;

        public SheetRecordCollector() {
            _totalSize = 0;
            _list = new ArrayList(128);
        }

        public int getTotalSize() {
            return _totalSize;
        }

        public void visitRecord(Record r) {
            _list.add(r);
            _totalSize += r.getRecordSize();
        }

        public int serialize(int offset, byte[] data) {
            int result = 0;
            int nRecs = _list.size();
            for (int i = 0; i < nRecs; i++) {
                Record rec = (Record) _list.get(i);
                result += rec.serialize(offset + result, data);
            }
            return result;
        }
    }

}
