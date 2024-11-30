

package com.document.render.office.fc.hssf.model;

import com.document.render.office.fc.ddf.EscherBSERecord;
import com.document.render.office.fc.ddf.EscherBoolProperty;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherDgRecord;
import com.document.render.office.fc.ddf.EscherDggRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherRGBProperty;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.ddf.EscherSpRecord;
import com.document.render.office.fc.ddf.EscherSplitMenuColorsRecord;
import com.document.render.office.fc.hssf.formula.EvaluationWorkbook.ExternalName;
import com.document.render.office.fc.hssf.formula.EvaluationWorkbook.ExternalSheet;
import com.document.render.office.fc.hssf.formula.FormulaShifter;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.hssf.formula.ptg.OperandPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;
import com.document.render.office.fc.hssf.record.BOFRecord;
import com.document.render.office.fc.hssf.record.BackupRecord;
import com.document.render.office.fc.hssf.record.BookBoolRecord;
import com.document.render.office.fc.hssf.record.BoundSheetRecord;
import com.document.render.office.fc.hssf.record.CodepageRecord;
import com.document.render.office.fc.hssf.record.CountryRecord;
import com.document.render.office.fc.hssf.record.DSFRecord;
import com.document.render.office.fc.hssf.record.DateWindow1904Record;
import com.document.render.office.fc.hssf.record.DrawingGroupRecord;
import com.document.render.office.fc.hssf.record.EOFRecord;
import com.document.render.office.fc.hssf.record.EscherAggregate;
import com.document.render.office.fc.hssf.record.ExtSSTRecord;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.ExternSheetRecord;
import com.document.render.office.fc.hssf.record.FileSharingRecord;
import com.document.render.office.fc.hssf.record.FnGroupCountRecord;
import com.document.render.office.fc.hssf.record.FontRecord;
import com.document.render.office.fc.hssf.record.FormatRecord;
import com.document.render.office.fc.hssf.record.HideObjRecord;
import com.document.render.office.fc.hssf.record.HyperlinkRecord;
import com.document.render.office.fc.hssf.record.InterfaceEndRecord;
import com.document.render.office.fc.hssf.record.InterfaceHdrRecord;
import com.document.render.office.fc.hssf.record.MMSRecord;
import com.document.render.office.fc.hssf.record.NameCommentRecord;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.hssf.record.PaletteRecord;
import com.document.render.office.fc.hssf.record.PasswordRecord;
import com.document.render.office.fc.hssf.record.PasswordRev4Record;
import com.document.render.office.fc.hssf.record.PrecisionRecord;
import com.document.render.office.fc.hssf.record.ProtectRecord;
import com.document.render.office.fc.hssf.record.ProtectionRev4Record;
import com.document.render.office.fc.hssf.record.RecalcIdRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RefreshAllRecord;
import com.document.render.office.fc.hssf.record.SSTRecord;
import com.document.render.office.fc.hssf.record.StyleRecord;
import com.document.render.office.fc.hssf.record.SupBookRecord;
import com.document.render.office.fc.hssf.record.TabIdRecord;
import com.document.render.office.fc.hssf.record.UseSelFSRecord;
import com.document.render.office.fc.hssf.record.WindowOneRecord;
import com.document.render.office.fc.hssf.record.WindowProtectRecord;
import com.document.render.office.fc.hssf.record.WriteAccessRecord;
import com.document.render.office.fc.hssf.record.WriteProtectRecord;
import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.hssf.util.HSSFColor;
import com.document.render.office.fc.ss.usermodel.BuiltinFormats;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.AbstractReader;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;



@Internal
public final class InternalWorkbook {

    private static final int MAX_SENSITIVE_SHEET_NAME_LEN = 31;

    private static final POILogger log = POILogFactory.getLogger(InternalWorkbook.class);
    private static final int DEBUG = POILogger.DEBUG;


    private final static short CODEPAGE = 0x04B0;


    private final WorkbookRecordList records;

    private final List<BoundSheetRecord> boundsheets;
    private final List<FormatRecord> formats;
    private final List<HyperlinkRecord> hyperlinks;

    private final Map<String, NameCommentRecord> commentRecords;

    protected SSTRecord sst;
    private LinkTable linkTable;

    private int numxfs;

    private int numfonts;

    private int maxformatid;

    private boolean uses1904datewindowing;
    private DrawingManager2 drawingManager;
    private List<EscherBSERecord> escherBSERecords;
    private WindowOneRecord windowOne;
    private FileSharingRecord fileShare;
    private WriteAccessRecord writeAccess;
    private WriteProtectRecord writeProtect;

    private InternalWorkbook() {
        records = new WorkbookRecordList();

        boundsheets = new ArrayList<BoundSheetRecord>();
        formats = new ArrayList<FormatRecord>();
        hyperlinks = new ArrayList<HyperlinkRecord>();
        numxfs = 0;
        numfonts = 0;
        maxformatid = -1;
        uses1904datewindowing = false;
        escherBSERecords = new ArrayList<EscherBSERecord>();
        commentRecords = new LinkedHashMap<String, NameCommentRecord>();
    }


    public static InternalWorkbook createWorkbook(List<Record> recs) {
        return createWorkbook(recs, null);
    }


    public static InternalWorkbook createWorkbook(List<Record> recs, AbstractReader iAbortListener) {
        InternalWorkbook retval = new InternalWorkbook();
        List<Record> records = new ArrayList<Record>(recs.size() / 3);
        retval.records.setRecords(records);

        int k;
        for (k = 0; k < recs.size(); k++) {
            if (iAbortListener != null && iAbortListener.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            Record rec = recs.get(k);

            if (rec.getSid() == EOFRecord.sid) {
                records.add(rec);
                break;
            }
            switch (rec.getSid()) {

                case BoundSheetRecord.sid:
                    retval.boundsheets.add((BoundSheetRecord) rec);
                    retval.records.setBspos(k);
                    break;

                case SSTRecord.sid:
                    retval.sst = (SSTRecord) rec;
                    break;

                case FontRecord.sid:
                    retval.records.setFontpos(k);
                    retval.numfonts++;
                    break;

                case ExtendedFormatRecord.sid:
                    retval.records.setXfpos(k);
                    retval.numxfs++;
                    break;

                case TabIdRecord.sid:
                    retval.records.setTabpos(k);
                    break;

                case ProtectRecord.sid:
                    retval.records.setProtpos(k);
                    break;

                case BackupRecord.sid:
                    retval.records.setBackuppos(k);
                    break;
                case ExternSheetRecord.sid:
                    throw new RuntimeException("Extern sheet is part of LinkTable");
                case NameRecord.sid:
                case SupBookRecord.sid:

                    retval.linkTable = new LinkTable(recs, k, retval.records, retval.commentRecords);
                    k += retval.linkTable.getRecordCount() - 1;
                    continue;
                case FormatRecord.sid:
                    retval.formats.add((FormatRecord) rec);
                    retval.maxformatid = retval.maxformatid >= ((FormatRecord) rec).getIndexCode()
                            ? retval.maxformatid : ((FormatRecord) rec).getIndexCode();
                    break;
                case DateWindow1904Record.sid:
                    retval.uses1904datewindowing = ((DateWindow1904Record) rec).getWindowing() == 1;
                    break;
                case PaletteRecord.sid:
                    retval.records.setPalettepos(k);
                    break;
                case WindowOneRecord.sid:
                    retval.windowOne = (WindowOneRecord) rec;
                    break;
                case WriteAccessRecord.sid:
                    retval.writeAccess = (WriteAccessRecord) rec;
                    break;
                case WriteProtectRecord.sid:
                    retval.writeProtect = (WriteProtectRecord) rec;
                    break;
                case FileSharingRecord.sid:
                    retval.fileShare = (FileSharingRecord) rec;
                    break;

                case NameCommentRecord.sid:
                    final NameCommentRecord ncr = (NameCommentRecord) rec;
                    retval.commentRecords.put(ncr.getNameText(), ncr);
                default:
            }
            records.add(rec);
        }








        for (; k < recs.size(); k++) {
            if (iAbortListener != null && iAbortListener.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }
            Record rec = recs.get(k);
            switch (rec.getSid()) {
                case HyperlinkRecord.sid:
                    retval.hyperlinks.add((HyperlinkRecord) rec);
                    break;
            }
        }

        if (retval.windowOne == null) {
            retval.windowOne = createWindowOne();
        }
        return retval;
    }


    public static InternalWorkbook createWorkbook() {
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "creating new workbook from scratch");
        InternalWorkbook retval = new InternalWorkbook();
        List<Record> records = new ArrayList<Record>(30);
        retval.records.setRecords(records);
        List<FormatRecord> formats = retval.formats;

        records.add(createBOF());
        records.add(new InterfaceHdrRecord(CODEPAGE));
        records.add(createMMS());
        records.add(InterfaceEndRecord.instance);
        records.add(createWriteAccess());
        records.add(createCodepage());
        records.add(createDSF());
        records.add(createTabId());
        retval.records.setTabpos(records.size() - 1);
        records.add(createFnGroupCount());
        records.add(createWindowProtect());
        records.add(createProtect());
        retval.records.setProtpos(records.size() - 1);
        records.add(createPassword());
        records.add(createProtectionRev4());
        records.add(createPasswordRev4());
        retval.windowOne = createWindowOne();
        records.add(retval.windowOne);
        records.add(createBackup());
        retval.records.setBackuppos(records.size() - 1);
        records.add(createHideObj());
        records.add(createDateWindow1904());
        records.add(createPrecision());
        records.add(createRefreshAll());
        records.add(createBookBool());
        records.add(createFont());
        records.add(createFont());
        records.add(createFont());
        records.add(createFont());
        retval.records.setFontpos(records.size() - 1);
        retval.numfonts = 4;


        for (int i = 0; i <= 7; i++) {
            FormatRecord rec = createFormat(i);
            retval.maxformatid = retval.maxformatid >= rec.getIndexCode() ? retval.maxformatid
                    : rec.getIndexCode();
            formats.add(rec);
            records.add(rec);
        }

        for (int k = 0; k < 21; k++) {
            records.add(retval.createExtendedFormat(k));
            retval.numxfs++;
        }
        retval.records.setXfpos(records.size() - 1);
        for (int k = 0; k < 6; k++) {
            records.add(retval.createStyle(k));
        }
        records.add(retval.createUseSelFS());

        int nBoundSheets = 1;
        for (int k = 0; k < nBoundSheets; k++) {
            BoundSheetRecord bsr = createBoundSheet(k);

            records.add(bsr);
            retval.boundsheets.add(bsr);
            retval.records.setBspos(records.size() - 1);
        }
        records.add(retval.createCountry());
        for (int k = 0; k < nBoundSheets; k++) {
            retval.getOrCreateLinkTable().checkExternSheet(k);
        }
        retval.sst = new SSTRecord();
        records.add(retval.sst);
        records.add(retval.createExtendedSST());

        records.add(EOFRecord.instance);
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "exit create new workbook from scratch");
        return retval;
    }

    private static BOFRecord createBOF() {
        BOFRecord retval = new BOFRecord();

        retval.setVersion((short) 0x600);
        retval.setType(BOFRecord.TYPE_WORKBOOK);
        retval.setBuild((short) 0x10d3);
        retval.setBuildYear((short) 1996);
        retval.setHistoryBitMask(0x41);
        retval.setRequiredVersion(0x6);
        return retval;
    }

    private static MMSRecord createMMS() {
        MMSRecord retval = new MMSRecord();

        retval.setAddMenuCount((byte) 0);
        retval.setDelMenuCount((byte) 0);
        return retval;
    }


    private static WriteAccessRecord createWriteAccess() {
        WriteAccessRecord retval = new WriteAccessRecord();

        try {
            retval.setUsername(System.getProperty("user.name"));
        } catch (AccessControlException e) {


            retval.setUsername("POI");
        }
        return retval;
    }

    private static CodepageRecord createCodepage() {
        CodepageRecord retval = new CodepageRecord();

        retval.setCodepage(CODEPAGE);
        return retval;
    }

    private static DSFRecord createDSF() {
        return new DSFRecord(false);
    }


    private static TabIdRecord createTabId() {
        return new TabIdRecord();
    }


    private static FnGroupCountRecord createFnGroupCount() {
        FnGroupCountRecord retval = new FnGroupCountRecord();

        retval.setCount((short) 14);
        return retval;
    }


    private static WindowProtectRecord createWindowProtect() {


        return new WindowProtectRecord(false);
    }


    private static ProtectRecord createProtect() {


        return new ProtectRecord(false);
    }


    private static PasswordRecord createPassword() {
        return new PasswordRecord(0x0000);
    }


    private static ProtectionRev4Record createProtectionRev4() {
        return new ProtectionRev4Record(false);
    }


    private static PasswordRev4Record createPasswordRev4() {
        return new PasswordRev4Record(0x0000);
    }


    private static WindowOneRecord createWindowOne() {
        WindowOneRecord retval = new WindowOneRecord();

        retval.setHorizontalHold((short) 0x168);
        retval.setVerticalHold((short) 0x10e);
        retval.setWidth((short) 0x3a5c);
        retval.setHeight((short) 0x23be);
        retval.setOptions((short) 0x38);
        retval.setActiveSheetIndex(0x0);
        retval.setFirstVisibleTab(0x0);
        retval.setNumSelectedTabs((short) 1);
        retval.setTabWidthRatio((short) 0x258);
        return retval;
    }


    private static BackupRecord createBackup() {
        BackupRecord retval = new BackupRecord();

        retval.setBackup((short) 0);
        return retval;
    }


    private static HideObjRecord createHideObj() {
        HideObjRecord retval = new HideObjRecord();
        retval.setHideObj((short) 0);
        return retval;
    }


    private static DateWindow1904Record createDateWindow1904() {
        DateWindow1904Record retval = new DateWindow1904Record();

        retval.setWindowing((short) 0);
        return retval;
    }


    private static PrecisionRecord createPrecision() {
        PrecisionRecord retval = new PrecisionRecord();
        retval.setFullPrecision(true);
        return retval;
    }


    private static RefreshAllRecord createRefreshAll() {
        return new RefreshAllRecord(false);
    }


    private static BookBoolRecord createBookBool() {
        BookBoolRecord retval = new BookBoolRecord();
        retval.setSaveLinkValues((short) 0);
        return retval;
    }


    private static FontRecord createFont() {
        FontRecord retval = new FontRecord();

        retval.setFontHeight((short) 0xc8);
        retval.setAttributes((short) 0x0);
        retval.setColorPaletteIndex((short) 0x7fff);
        retval.setBoldWeight((short) 0x190);
        retval.setFontName("Arial");
        return retval;
    }


    private static FormatRecord createFormat(int id) {



        switch (id) {
            case 0:
                return new FormatRecord(5, BuiltinFormats.getBuiltinFormat(5));
            case 1:
                return new FormatRecord(6, BuiltinFormats.getBuiltinFormat(6));
            case 2:
                return new FormatRecord(7, BuiltinFormats.getBuiltinFormat(7));
            case 3:
                return new FormatRecord(8, BuiltinFormats.getBuiltinFormat(8));
            case 4:
                return new FormatRecord(0x2a, BuiltinFormats.getBuiltinFormat(0x2a));
            case 5:
                return new FormatRecord(0x29, BuiltinFormats.getBuiltinFormat(0x29));
            case 6:
                return new FormatRecord(0x2c, BuiltinFormats.getBuiltinFormat(0x2c));
            case 7:
                return new FormatRecord(0x2b, BuiltinFormats.getBuiltinFormat(0x2b));
        }
        throw new IllegalArgumentException("Unexpected id " + id);
    }


    private static ExtendedFormatRecord createExtendedFormat(int id) {
        ExtendedFormatRecord retval = new ExtendedFormatRecord();

        switch (id) {

            case 0:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 1:
                retval.setFontIndex((short) 1);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 2:
                retval.setFontIndex((short) 1);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 3:
                retval.setFontIndex((short) 2);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 4:
                retval.setFontIndex((short) 2);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 5:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 6:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 7:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 8:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 9:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 10:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 11:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 12:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 13:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 14:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff400);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;


            case 15:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0);
                retval.setCellOptions((short) 0x1);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0x0);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;


            case 16:
                retval.setFontIndex((short) 1);
                retval.setFormatIndex((short) 0x2b);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff800);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 17:
                retval.setFontIndex((short) 1);
                retval.setFormatIndex((short) 0x29);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff800);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 18:
                retval.setFontIndex((short) 1);
                retval.setFormatIndex((short) 0x2c);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff800);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 19:
                retval.setFontIndex((short) 1);
                retval.setFormatIndex((short) 0x2a);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff800);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 20:
                retval.setFontIndex((short) 1);
                retval.setFormatIndex((short) 0x9);
                retval.setCellOptions((short) 0xfffffff5);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0xfffff800);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;


            case 21:
                retval.setFontIndex((short) 5);
                retval.setFormatIndex((short) 0x0);
                retval.setCellOptions((short) 0x1);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0x800);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 22:
                retval.setFontIndex((short) 6);
                retval.setFormatIndex((short) 0x0);
                retval.setCellOptions((short) 0x1);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0x5c00);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 23:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0x31);
                retval.setCellOptions((short) 0x1);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0x5c00);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 24:
                retval.setFontIndex((short) 0);
                retval.setFormatIndex((short) 0x8);
                retval.setCellOptions((short) 0x1);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0x5c00);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;

            case 25:
                retval.setFontIndex((short) 6);
                retval.setFormatIndex((short) 0x8);
                retval.setCellOptions((short) 0x1);
                retval.setAlignmentOptions((short) 0x20);
                retval.setIndentionOptions((short) 0x5c00);
                retval.setBorderOptions((short) 0);
                retval.setPaletteOptions((short) 0);
                retval.setAdtlPaletteOptions((short) 0);
                retval.setFillPaletteOptions((short) 0x20c0);
                break;
        }
        return retval;
    }


    private static ExtendedFormatRecord createExtendedFormat() {
        ExtendedFormatRecord retval = new ExtendedFormatRecord();

        retval.setFontIndex((short) 0);
        retval.setFormatIndex((short) 0x0);
        retval.setCellOptions((short) 0x1);
        retval.setAlignmentOptions((short) 0x20);
        retval.setIndentionOptions((short) 0);
        retval.setBorderOptions((short) 0);
        retval.setPaletteOptions((short) 0);
        retval.setAdtlPaletteOptions((short) 0);
        retval.setFillPaletteOptions((short) 0x20c0);
        retval.setTopBorderPaletteIdx(HSSFColor.BLACK.index);
        retval.setBottomBorderPaletteIdx(HSSFColor.BLACK.index);
        retval.setLeftBorderPaletteIdx(HSSFColor.BLACK.index);
        retval.setRightBorderPaletteIdx(HSSFColor.BLACK.index);
        return retval;
    }


    private static StyleRecord createStyle(int id) {
        StyleRecord retval = new StyleRecord();

        switch (id) {

            case 0:
                retval.setXFIndex(0x010);
                retval.setBuiltinStyle(3);
                retval.setOutlineStyleLevel((byte) 0xffffffff);
                break;

            case 1:
                retval.setXFIndex(0x011);
                retval.setBuiltinStyle(6);
                retval.setOutlineStyleLevel((byte) 0xffffffff);
                break;

            case 2:
                retval.setXFIndex(0x012);
                retval.setBuiltinStyle(4);
                retval.setOutlineStyleLevel((byte) 0xffffffff);
                break;

            case 3:
                retval.setXFIndex(0x013);
                retval.setBuiltinStyle(7);
                retval.setOutlineStyleLevel((byte) 0xffffffff);
                break;

            case 4:
                retval.setXFIndex(0x000);
                retval.setBuiltinStyle(0);
                retval.setOutlineStyleLevel((byte) 0xffffffff);
                break;

            case 5:
                retval.setXFIndex(0x014);
                retval.setBuiltinStyle(5);
                retval.setOutlineStyleLevel((byte) 0xffffffff);
                break;
        }
        return retval;
    }


    private static PaletteRecord createPalette() {
        return new PaletteRecord();
    }


    private static UseSelFSRecord createUseSelFS() {
        return new UseSelFSRecord(false);
    }


    private static BoundSheetRecord createBoundSheet(int id) {
        return new BoundSheetRecord("Sheet" + (id + 1));
    }


    private static CountryRecord createCountry() {
        CountryRecord retval = new CountryRecord();

        retval.setDefaultCountry((short) 1);


        if (Locale.getDefault().toString().equals("ru_RU")) {
            retval.setCurrentCountry((short) 7);
        } else {
            retval.setCurrentCountry((short) 1);
        }

        return retval;
    }


    private static ExtSSTRecord createExtendedSST() {
        ExtSSTRecord retval = new ExtSSTRecord();
        retval.setNumStringsPerBucket((short) 0x8);
        return retval;
    }


    public NameRecord getSpecificBuiltinRecord(byte name, int sheetNumber) {
        return getOrCreateLinkTable().getSpecificBuiltinRecord(name, sheetNumber);
    }


    public void removeBuiltinRecord(byte name, int sheetIndex) {
        linkTable.removeBuiltinRecord(name, sheetIndex);

    }

    public int getNumRecords() {
        return records.size();
    }



    public FontRecord getFontRecordAt(int idx) {
        int index = idx;

        if (index > 4) {
            index -= 1;
        }
        if (index > (numfonts - 1)) {
            throw new ArrayIndexOutOfBoundsException("There are only " + numfonts
                    + " font records, you asked for " + idx);
        }
        FontRecord retval = (FontRecord) records
                .get((records.getFontpos() - (numfonts - 1)) + index);

        return retval;
    }


    public int getFontIndex(FontRecord font) {
        for (int i = 0; i <= numfonts; i++) {
            FontRecord thisFont = (FontRecord) records.get((records.getFontpos() - (numfonts - 1))
                    + i);
            if (thisFont == font) {

                if (i > 3) {
                    return (i + 1);
                }
                return i;
            }
        }
        throw new IllegalArgumentException("Could not find that font!");
    }


























    public FontRecord createNewFont() {
        FontRecord rec = createFont();

        records.add(records.getFontpos() + 1, rec);
        records.setFontpos(records.getFontpos() + 1);
        numfonts++;
        return rec;
    }


    public void removeFontRecord(FontRecord rec) {
        records.remove(rec);
        numfonts--;
    }



    public int getNumberOfFontRecords() {
        return numfonts;
    }



    public void setSheetBof(int sheetIndex, int pos) {
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "setting bof for sheetnum =", Integer.valueOf(sheetIndex), " at pos=",
                    Integer.valueOf(pos));
        checkSheets(sheetIndex);
        getBoundSheetRec(sheetIndex).setPositionOfBof(pos);
    }

    private BoundSheetRecord getBoundSheetRec(int sheetIndex) {
        return boundsheets.get(sheetIndex);
    }



    public BackupRecord getBackupRecord() {
        return (BackupRecord) records.get(records.getBackuppos());
    }


    public void setSheetName(int sheetnum, String sheetname) {
        checkSheets(sheetnum);


        if (sheetname.length() > 31)
            sheetname = sheetname.substring(0, 31);

        BoundSheetRecord sheet = boundsheets.get(sheetnum);
        sheet.setSheetname(sheetname);
    }


    public boolean doesContainsSheetName(String name, int excludeSheetIdx) {
        String aName = name;
        if (aName.length() > MAX_SENSITIVE_SHEET_NAME_LEN) {
            aName = aName.substring(0, MAX_SENSITIVE_SHEET_NAME_LEN);
        }
        for (int i = 0; i < boundsheets.size(); i++) {
            BoundSheetRecord boundSheetRecord = getBoundSheetRec(i);
            if (excludeSheetIdx == i) {
                continue;
            }
            String bName = boundSheetRecord.getSheetname();
            if (bName.length() > MAX_SENSITIVE_SHEET_NAME_LEN) {
                bName = bName.substring(0, MAX_SENSITIVE_SHEET_NAME_LEN);
            }
            if (aName.equalsIgnoreCase(bName)) {
                return true;
            }
        }
        return false;
    }



    public void setSheetOrder(String sheetname, int pos) {
        int sheetNumber = getSheetIndex(sheetname);

        boundsheets.add(pos, boundsheets.remove(sheetNumber));
    }


    public String getSheetName(int sheetIndex) {
        return getBoundSheetRec(sheetIndex).getSheetname();
    }


    public boolean isSheetHidden(int sheetnum) {
        return getBoundSheetRec(sheetnum).isHidden();
    }


    public boolean isSheetVeryHidden(int sheetnum) {
        return getBoundSheetRec(sheetnum).isVeryHidden();
    }


    public void setSheetHidden(int sheetnum, boolean hidden) {
        getBoundSheetRec(sheetnum).setHidden(hidden);
    }


    public void setSheetHidden(int sheetnum, int hidden) {
        BoundSheetRecord bsr = getBoundSheetRec(sheetnum);
        boolean h = false;
        boolean vh = false;
        if (hidden == 0) {
        } else if (hidden == 1) {
            h = true;
        } else if (hidden == 2) {
            vh = true;
        } else {
            throw new IllegalArgumentException("Invalid hidden flag " + hidden
                    + " given, must be 0, 1 or 2");
        }
        bsr.setHidden(h);
        bsr.setVeryHidden(vh);
    }


    public int getSheetIndex(String name) {
        int retval = -1;

        for (int k = 0; k < boundsheets.size(); k++) {
            String sheet = getSheetName(k);

            if (sheet.equalsIgnoreCase(name)) {
                retval = k;
                break;
            }
        }
        return retval;
    }


    private void checkSheets(int sheetnum) {
        if ((boundsheets.size()) <= sheetnum) {
            if ((boundsheets.size() + 1) <= sheetnum) {
                throw new RuntimeException("Sheet number out of bounds!");
            }
            BoundSheetRecord bsr = createBoundSheet(sheetnum);

            records.add(records.getBspos() + 1, bsr);
            records.setBspos(records.getBspos() + 1);
            boundsheets.add(bsr);
            getOrCreateLinkTable().checkExternSheet(sheetnum);
            fixTabIdRecord();
        } else {


            if (records.getTabpos() > 0) {
                TabIdRecord tir = (TabIdRecord) records.get(records.getTabpos());
                if (tir._tabids.length < boundsheets.size()) {
                    fixTabIdRecord();
                }
            }
        }
    }


    public void removeSheet(int sheetIndex) {
        if (boundsheets.size() > sheetIndex) {
            records.remove(records.getBspos() - (boundsheets.size() - 1) + sheetIndex);
            boundsheets.remove(sheetIndex);
            fixTabIdRecord();
        }








        int sheetNum1Based = sheetIndex + 1;
        for (int i = 0; i < getNumNames(); i++) {
            NameRecord nr = getNameRecord(i);

            if (nr.getSheetNumber() == sheetNum1Based) {

                nr.setSheetNumber(0);
            } else if (nr.getSheetNumber() > sheetNum1Based) {


                nr.setSheetNumber(nr.getSheetNumber() - 1);
            }
        }
    }


    private void fixTabIdRecord() {
        TabIdRecord tir = (TabIdRecord) records.get(records.getTabpos());
        short[] tia = new short[boundsheets.size()];

        for (short k = 0; k < tia.length; k++) {
            tia[k] = k;
        }
        tir.setTabIdArray(tia);
    }



    public int getNumSheets() {
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "getNumSheets=", Integer.valueOf(boundsheets.size()));
        return boundsheets.size();
    }



    public int getNumExFormats() {
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "getXF=", Integer.valueOf(numxfs));
        return numxfs;
    }



    public ExtendedFormatRecord getExFormatAt(int index) {
        int xfptr = records.getXfpos() - (numxfs - 1);

        xfptr += index;
        Record roc = records.get(xfptr);
        if (roc instanceof ExtendedFormatRecord) {
            return (ExtendedFormatRecord) roc;
        } else {
            return null;
        }
    }


    public void removeExFormatRecord(ExtendedFormatRecord rec) {
        records.remove(rec);
        numxfs--;
    }



    public ExtendedFormatRecord createCellXF() {
        ExtendedFormatRecord xf = createExtendedFormat();

        records.add(records.getXfpos() + 1, xf);
        records.setXfpos(records.getXfpos() + 1);
        numxfs++;
        return xf;
    }


    public StyleRecord getStyleRecord(int xfIndex) {


        for (int i = records.getXfpos(); i < records.size(); i++) {
            Record r = records.get(i);
            if (r instanceof ExtendedFormatRecord) {
                continue;
            }
            if (!(r instanceof StyleRecord)) {
                continue;
            }
            StyleRecord sr = (StyleRecord) r;
            if (sr.getXFIndex() == xfIndex) {
                return sr;
            }
        }
        return null;
    }


    public StyleRecord createStyleRecord(int xfIndex) {


        StyleRecord newSR = new StyleRecord();
        newSR.setXFIndex(xfIndex);


        int addAt = -1;
        for (int i = records.getXfpos(); i < records.size() && addAt == -1; i++) {
            Record r = records.get(i);
            if (r instanceof ExtendedFormatRecord || r instanceof StyleRecord) {

            } else {
                addAt = i;
            }
        }
        if (addAt == -1) {
            throw new IllegalStateException("No XF Records found!");
        }
        records.add(addAt, newSR);

        return newSR;
    }



    public int addSSTString(UnicodeString string) {
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "insert to sst string='", string);
        if (sst == null) {
            insertSST();
        }
        return sst.addString(string);
    }


    public int getSSTUniqueStringSize() {
        return sst.getNumUniqueStrings();
    }



    public UnicodeString getSSTString(int str) {
        if (sst == null) {
            insertSST();
        }
        UnicodeString retval = sst.getString(str);

        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "Returning SST for index=", Integer.valueOf(str), " String= ", retval);
        return retval;
    }



    public void insertSST() {
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "creating new SST via insertSST!");
        sst = new SSTRecord();
        records.add(records.size() - 1, createExtendedSST());
        records.add(records.size() - 2, sst);
    }



    public int serialize(int offset, byte[] data) {
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "Serializing Workbook with offsets");

        int pos = 0;

        SSTRecord sst = null;
        int sstPos = 0;
        boolean wroteBoundSheets = false;
        for (int k = 0; k < records.size(); k++) {

            Record record = records.get(k);
            int len = 0;
            if (record instanceof SSTRecord) {
                sst = (SSTRecord) record;
                sstPos = pos;
            }
            if (record.getSid() == ExtSSTRecord.sid && sst != null) {
                record = sst.createExtSSTRecord(sstPos + offset);
            }
            if (record instanceof BoundSheetRecord) {
                if (!wroteBoundSheets) {
                    for (int i = 0; i < boundsheets.size(); i++) {
                        len += getBoundSheetRec(i).serialize(pos + offset + len, data);
                    }
                    wroteBoundSheets = true;
                }
            } else {
                len = record.serialize(pos + offset, data);
            }




            pos += len;
        }
        if (log.check(POILogger.DEBUG))
            log.log(DEBUG, "Exiting serialize workbook");
        return pos;
    }

    public int getSize() {
        int retval = 0;

        SSTRecord sst = null;
        for (int k = 0; k < records.size(); k++) {
            Record record = records.get(k);
            if (record instanceof SSTRecord)
                sst = (SSTRecord) record;
            if (record.getSid() == ExtSSTRecord.sid && sst != null)
                retval += sst.calcExtSSTRecordSize();
            else
                retval += record.getRecordSize();
        }
        return retval;
    }


    private LinkTable getOrCreateLinkTable() {
        if (linkTable == null) {
            linkTable = new LinkTable((short) getNumSheets(), records);
        }
        return linkTable;
    }


    public String findSheetNameFromExternSheet(int externSheetIndex) {

        int indexToSheet = linkTable.getIndexToInternalSheet(externSheetIndex);
        if (indexToSheet < 0) {


            return "";
        }
        if (indexToSheet >= boundsheets.size()) {

            return "";
        }
        return getSheetName(indexToSheet);
    }

    public ExternalSheet getExternalSheet(int externSheetIndex) {
        String[] extNames = linkTable.getExternalBookAndSheetName(externSheetIndex);
        if (extNames == null) {
            return null;
        }
        return new ExternalSheet(extNames[0], extNames[1]);
    }

    public ExternalName getExternalName(int externSheetIndex, int externNameIndex) {
        String nameName = linkTable.resolveNameXText(externSheetIndex, externNameIndex);
        if (nameName == null) {
            return null;
        }
        int ix = linkTable.resolveNameXIx(externSheetIndex, externNameIndex);
        return new ExternalName(nameName, externNameIndex, ix);
    }


    public int getSheetIndexFromExternSheetIndex(int externSheetNumber) {
        return linkTable.getSheetIndexFromExternSheetIndex(externSheetNumber);
    }


    public short checkExternSheet(int sheetNumber) {
        return (short) getOrCreateLinkTable().checkExternSheet(sheetNumber);
    }

    public int getExternalSheetIndex(String workbookName, String sheetName) {
        return getOrCreateLinkTable().getExternalSheetIndex(workbookName, sheetName);
    }


    public int getNumNames() {
        if (linkTable == null) {
            return 0;
        }
        return linkTable.getNumNames();
    }


    public NameRecord getNameRecord(int index) {
        return linkTable.getNameRecord(index);
    }


    public NameCommentRecord getNameCommentRecord(final NameRecord nameRecord) {
        return commentRecords.get(nameRecord.getNameText());
    }


    public NameRecord createName() {
        return addName(new NameRecord());
    }


    public NameRecord addName(NameRecord name) {

        LinkTable linkTable = getOrCreateLinkTable();
        linkTable.addName(name);

        return name;
    }


    public NameRecord createBuiltInName(byte builtInName, int sheetNumber) {
        if (sheetNumber < 0 || sheetNumber + 1 > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Sheet number [" + sheetNumber + "]is not valid ");
        }

        NameRecord name = new NameRecord(builtInName, sheetNumber);

        if (linkTable.nameAlreadyExists(name)) {
            throw new RuntimeException("Builtin (" + builtInName + ") already exists for sheet ("
                    + sheetNumber + ")");
        }
        addName(name);
        return name;
    }


    public void removeName(int nameIndex) {

        if (linkTable.getNumNames() > nameIndex) {
            int idx = findFirstRecordLocBySid(NameRecord.sid);
            records.remove(idx + nameIndex);
            linkTable.removeName(nameIndex);
        }
    }


    public void updateNameCommentRecordCache(final NameCommentRecord commentRecord) {
        if (commentRecords.containsValue(commentRecord)) {
            for (Entry<String, NameCommentRecord> entry : commentRecords.entrySet()) {
                if (entry.getValue().equals(commentRecord)) {
                    commentRecords.remove(entry.getKey());
                    break;
                }
            }
        }
        commentRecords.put(commentRecord.getNameText(), commentRecord);
    }


    public short getFormat(String format, boolean createIfNotFound) {
        for (FormatRecord r : formats) {
            if (r.getFormatString().equals(format)) {
                return (short) r.getIndexCode();
            }
        }

        if (createIfNotFound) {
            return (short) createFormat(format);
        }

        return -1;
    }


    public List<FormatRecord> getFormats() {
        return formats;
    }


    public int createFormat(String formatString) {

        maxformatid = maxformatid >= 0xa4 ? maxformatid + 1 : 0xa4;
        FormatRecord rec = new FormatRecord(maxformatid, formatString);

        int pos = 0;
        while (pos < records.size() && records.get(pos).getSid() != FormatRecord.sid)
            pos++;
        pos += formats.size();
        formats.add(rec);
        records.add(pos, rec);
        return maxformatid;
    }


    public Record findFirstRecordBySid(short sid) {
        for (Record record : records) {
            if (record.getSid() == sid) {
                return record;
            }
        }
        return null;
    }


    public int findFirstRecordLocBySid(short sid) {
        int index = 0;
        for (Record record : records) {
            if (record.getSid() == sid) {
                return index;
            }
            index++;
        }
        return -1;
    }


    public Record findNextRecordBySid(short sid, int pos) {
        int matches = 0;
        for (Record record : records) {
            if (record.getSid() == sid) {
                if (matches++ == pos)
                    return record;
            }
        }
        return null;
    }

    public List<HyperlinkRecord> getHyperlinks() {
        return hyperlinks;
    }

    public List<Record> getRecords() {
        return records.getRecords();
    }


    public boolean isUsing1904DateWindowing() {
        return uses1904datewindowing;
    }


    public PaletteRecord getCustomPalette() {
        PaletteRecord palette;
        int palettePos = records.getPalettepos();
        if (palettePos != -1) {
            Record rec = records.get(palettePos);
            if (rec instanceof PaletteRecord) {
                palette = (PaletteRecord) rec;
            } else
                throw new RuntimeException("InternalError: Expected PaletteRecord but got a '"
                        + rec + "'");
        } else {
            palette = createPalette();

            records.add(1, palette);
            records.setPalettepos(1);
        }
        return palette;
    }


    public DrawingManager2 findDrawingGroup() {
        if (drawingManager != null) {

            return drawingManager;
        }



        for (Record r : records) {
            if (r instanceof DrawingGroupRecord) {
                DrawingGroupRecord dg = (DrawingGroupRecord) r;
                dg.processChildRecords();

                EscherContainerRecord cr = dg.getEscherContainer();
                if (cr == null) {
                    continue;
                }

                EscherDggRecord dgg = null;
                EscherContainerRecord bStore = null;
                for (Iterator<EscherRecord> it = cr.getChildIterator(); it.hasNext(); ) {
                    EscherRecord er = it.next();
                    if (er instanceof EscherDggRecord) {
                        dgg = (EscherDggRecord) er;
                    } else if (er.getRecordId() == EscherContainerRecord.BSTORE_CONTAINER) {
                        bStore = (EscherContainerRecord) er;
                    }
                }

                if (dgg != null) {
                    drawingManager = new DrawingManager2(dgg);
                    if (bStore != null) {
                        for (EscherRecord bs : bStore.getChildRecords()) {
                            if (bs instanceof EscherBSERecord)
                                escherBSERecords.add((EscherBSERecord) bs);
                        }
                    }
                    return drawingManager;
                }
            }
        }


        int dgLoc = findFirstRecordLocBySid(DrawingGroupRecord.sid);


        if (dgLoc != -1) {
            DrawingGroupRecord dg = (DrawingGroupRecord) records.get(dgLoc);
            EscherDggRecord dgg = null;
            EscherContainerRecord bStore = null;
            for (EscherRecord er : dg.getEscherRecords()) {
                if (er instanceof EscherDggRecord) {
                    dgg = (EscherDggRecord) er;
                } else if (er.getRecordId() == EscherContainerRecord.BSTORE_CONTAINER) {
                    bStore = (EscherContainerRecord) er;
                }
            }

            if (dgg != null) {
                drawingManager = new DrawingManager2(dgg);
                if (bStore != null) {
                    for (EscherRecord bs : bStore.getChildRecords()) {
                        if (bs instanceof EscherBSERecord)
                            escherBSERecords.add((EscherBSERecord) bs);
                    }
                }
            }
        }
        return drawingManager;
    }


    public void createDrawingGroup() {
        if (drawingManager == null) {
            EscherContainerRecord dggContainer = new EscherContainerRecord();
            EscherDggRecord dgg = new EscherDggRecord();
            EscherOptRecord opt = new EscherOptRecord();
            EscherSplitMenuColorsRecord splitMenuColors = new EscherSplitMenuColorsRecord();

            dggContainer.setRecordId((short) 0xF000);
            dggContainer.setOptions((short) 0x000F);
            dgg.setRecordId(EscherDggRecord.RECORD_ID);
            dgg.setOptions((short) 0x0000);
            dgg.setShapeIdMax(1024);
            dgg.setNumShapesSaved(0);
            dgg.setDrawingsSaved(0);
            dgg.setFileIdClusters(new EscherDggRecord.FileIdCluster[]{});
            drawingManager = new DrawingManager2(dgg);
            EscherContainerRecord bstoreContainer = null;
            if (escherBSERecords.size() > 0) {
                bstoreContainer = new EscherContainerRecord();
                bstoreContainer.setRecordId(EscherContainerRecord.BSTORE_CONTAINER);
                bstoreContainer.setOptions((short) ((escherBSERecords.size() << 4) | 0xF));
                for (EscherRecord escherRecord : escherBSERecords) {
                    bstoreContainer.addChildRecord(escherRecord);
                }
            }
            opt.setRecordId((short) 0xF00B);
            opt.setOptions((short) 0x0033);
            opt.addEscherProperty(new EscherBoolProperty(
                    EscherProperties.TEXT__SIZE_TEXT_TO_FIT_SHAPE, 524296));
            opt.addEscherProperty(new EscherRGBProperty(EscherProperties.FILL__FILLCOLOR,
                    0x08000041));
            opt.addEscherProperty(new EscherRGBProperty(EscherProperties.LINESTYLE__COLOR,
                    134217792));
            splitMenuColors.setRecordId((short) 0xF11E);
            splitMenuColors.setOptions((short) 0x0040);
            splitMenuColors.setColor1(0x0800000D);
            splitMenuColors.setColor2(0x0800000C);
            splitMenuColors.setColor3(0x08000017);
            splitMenuColors.setColor4(0x100000F7);

            dggContainer.addChildRecord(dgg);
            if (bstoreContainer != null)
                dggContainer.addChildRecord(bstoreContainer);
            dggContainer.addChildRecord(opt);
            dggContainer.addChildRecord(splitMenuColors);

            int dgLoc = findFirstRecordLocBySid(DrawingGroupRecord.sid);
            if (dgLoc == -1) {
                DrawingGroupRecord drawingGroup = new DrawingGroupRecord();
                drawingGroup.addEscherRecord(dggContainer);
                int loc = findFirstRecordLocBySid(CountryRecord.sid);

                getRecords().add(loc + 1, drawingGroup);
            } else {
                DrawingGroupRecord drawingGroup = new DrawingGroupRecord();
                drawingGroup.addEscherRecord(dggContainer);
                getRecords().set(dgLoc, drawingGroup);
            }

        }
    }

    public WindowOneRecord getWindowOne() {
        return windowOne;
    }

    public EscherBSERecord getBSERecord(int pictureIndex) {
        int index = pictureIndex - 1;
        if (index >= 0 && index < escherBSERecords.size()) {
            return escherBSERecords.get(index);
        }
        return null;
    }

    public int addBSERecord(EscherBSERecord e) {
        createDrawingGroup();


        escherBSERecords.add(e);

        int dgLoc = findFirstRecordLocBySid(DrawingGroupRecord.sid);
        DrawingGroupRecord drawingGroup = (DrawingGroupRecord) getRecords().get(dgLoc);

        EscherContainerRecord dggContainer = (EscherContainerRecord) drawingGroup.getEscherRecord(0);
        EscherContainerRecord bstoreContainer;
        if (dggContainer.getChild(1).getRecordId() == EscherContainerRecord.BSTORE_CONTAINER) {
            bstoreContainer = (EscherContainerRecord) dggContainer.getChild(1);
        } else {
            bstoreContainer = new EscherContainerRecord();
            bstoreContainer.setRecordId(EscherContainerRecord.BSTORE_CONTAINER);
            List<EscherRecord> childRecords = dggContainer.getChildRecords();
            childRecords.add(1, bstoreContainer);
            dggContainer.setChildRecords(childRecords);
        }
        bstoreContainer.setOptions((short) ((escherBSERecords.size() << 4) | 0xF));

        bstoreContainer.addChildRecord(e);

        return escherBSERecords.size();
    }

    public DrawingManager2 getDrawingManager() {
        return drawingManager;
    }

    public WriteProtectRecord getWriteProtect() {
        if (writeProtect == null) {
            writeProtect = new WriteProtectRecord();
            int i = 0;
            for (i = 0; i < records.size() && !(records.get(i) instanceof BOFRecord); i++) {
            }
            records.add(i + 1, writeProtect);
        }
        return this.writeProtect;
    }

    public WriteAccessRecord getWriteAccess() {
        if (writeAccess == null) {
            writeAccess = createWriteAccess();
            int i = 0;
            for (i = 0; i < records.size() && !(records.get(i) instanceof InterfaceEndRecord); i++) {
            }
            records.add(i + 1, writeAccess);
        }
        return writeAccess;
    }

    public FileSharingRecord getFileSharing() {
        if (fileShare == null) {
            fileShare = new FileSharingRecord();
            int i = 0;
            for (i = 0; i < records.size() && !(records.get(i) instanceof WriteAccessRecord); i++) {
            }
            records.add(i + 1, fileShare);
        }
        return fileShare;
    }


    public boolean isWriteProtected() {
        if (fileShare == null) {
            return false;
        }
        FileSharingRecord frec = getFileSharing();
        return frec.getReadOnly() == 1;
    }


    public void writeProtectWorkbook(String password, String username) {
        int protIdx = -1;
        FileSharingRecord frec = getFileSharing();
        WriteAccessRecord waccess = getWriteAccess();
        WriteProtectRecord wprotect = getWriteProtect();
        frec.setReadOnly((short) 1);
        frec.setPassword(FileSharingRecord.hashPassword(password));
        frec.setUsername(username);
        waccess.setUsername(username);
    }


    public void unwriteProtectWorkbook() {
        records.remove(fileShare);
        records.remove(writeProtect);
        fileShare = null;
        writeProtect = null;
    }


    public String resolveNameXText(int refIndex, int definedNameIndex) {
        return linkTable.resolveNameXText(refIndex, definedNameIndex);
    }


    public NameXPtg getNameXPtg(String name, UDFFinder udf) {
        LinkTable lnk = getOrCreateLinkTable();
        NameXPtg xptg = lnk.getNameXPtg(name);

        if (xptg == null && udf.findFunction(name) != null) {


            xptg = lnk.addNameXPtg(name);
        }
        return xptg;
    }


    public void cloneDrawings(InternalSheet sheet) {

        findDrawingGroup();

        if (drawingManager == null) {

            return;
        }


        int aggLoc = sheet.aggregateDrawingRecords(drawingManager, false);
        if (aggLoc != -1) {
            EscherAggregate agg = (EscherAggregate) sheet.findFirstRecordBySid(EscherAggregate.sid);
            EscherContainerRecord escherContainer = agg.getEscherContainer();
            if (escherContainer == null) {
                return;
            }

            EscherDggRecord dgg = drawingManager.getDgg();


            int dgId = drawingManager.findNewDrawingGroupId();
            dgg.addCluster(dgId, 0);
            dgg.setDrawingsSaved(dgg.getDrawingsSaved() + 1);

            EscherDgRecord dg = null;
            for (Iterator<EscherRecord> it = escherContainer.getChildIterator(); it.hasNext(); ) {
                EscherRecord er = it.next();
                if (er instanceof EscherDgRecord) {
                    dg = (EscherDgRecord) er;

                    dg.setOptions((short) (dgId << 4));
                } else if (er instanceof EscherContainerRecord) {

                    EscherContainerRecord cp = (EscherContainerRecord) er;
                    for (Iterator<EscherRecord> spIt = cp.getChildRecords().iterator(); spIt
                            .hasNext(); ) {
                        EscherContainerRecord shapeContainer = (EscherContainerRecord) spIt.next();
                        for (EscherRecord shapeChildRecord : shapeContainer.getChildRecords()) {
                            int recordId = shapeChildRecord.getRecordId();
                            if (recordId == EscherSpRecord.RECORD_ID) {
                                EscherSpRecord sp = (EscherSpRecord) shapeChildRecord;
                                int shapeId = drawingManager.allocateShapeId((short) dgId, dg);

                                dg.setNumShapes(dg.getNumShapes() - 1);
                                sp.setShapeId(shapeId);
                            } else if (recordId == EscherOptRecord.RECORD_ID) {
                                EscherOptRecord opt = (EscherOptRecord) shapeChildRecord;
                                EscherSimpleProperty prop = (EscherSimpleProperty) opt
                                        .lookup(EscherProperties.BLIP__BLIPTODISPLAY);
                                if (prop != null) {
                                    int pictureIndex = prop.getPropertyValue();

                                    EscherBSERecord bse = getBSERecord(pictureIndex);
                                    bse.setRef(bse.getRef() + 1);
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public NameRecord cloneFilter(int filterDbNameIndex, int newSheetIndex) {
        NameRecord origNameRecord = getNameRecord(filterDbNameIndex);

        int newExtSheetIx = checkExternSheet(newSheetIndex);
        Ptg[] ptgs = origNameRecord.getNameDefinition();
        for (int i = 0; i < ptgs.length; i++) {
            Ptg ptg = ptgs[i];

            if (ptg instanceof Area3DPtg) {
                Area3DPtg a3p = (Area3DPtg) ((OperandPtg) ptg).copy();
                a3p.setExternSheetIndex(newExtSheetIx);
                ptgs[i] = a3p;
            } else if (ptg instanceof Ref3DPtg) {
                Ref3DPtg r3p = (Ref3DPtg) ((OperandPtg) ptg).copy();
                r3p.setExternSheetIndex(newExtSheetIx);
                ptgs[i] = r3p;
            }
        }
        NameRecord newNameRecord = createBuiltInName(NameRecord.BUILTIN_FILTER_DB,
                newSheetIndex + 1);
        newNameRecord.setNameDefinition(ptgs);
        newNameRecord.setHidden(true);
        return newNameRecord;

    }


    public void updateNamesAfterCellShift(FormulaShifter shifter) {
        for (int i = 0; i < getNumNames(); ++i) {
            NameRecord nr = getNameRecord(i);
            Ptg[] ptgs = nr.getNameDefinition();
            if (shifter.adjustFormula(ptgs, nr.getSheetNumber())) {
                nr.setNameDefinition(ptgs);
            }
        }
    }


    public RecalcIdRecord getRecalcId() {
        RecalcIdRecord record = (RecalcIdRecord) findFirstRecordBySid(RecalcIdRecord.sid);
        if (record == null) {
            record = new RecalcIdRecord();

            int pos = findFirstRecordLocBySid(CountryRecord.sid);
            records.add(pos + 1, record);
        }
        return record;
    }
}
