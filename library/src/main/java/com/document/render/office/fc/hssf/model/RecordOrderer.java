

package com.document.render.office.fc.hssf.model;

import com.document.render.office.fc.hssf.record.ArrayRecord;
import com.document.render.office.fc.hssf.record.BOFRecord;
import com.document.render.office.fc.hssf.record.BlankRecord;
import com.document.render.office.fc.hssf.record.BoolErrRecord;
import com.document.render.office.fc.hssf.record.CalcCountRecord;
import com.document.render.office.fc.hssf.record.CalcModeRecord;
import com.document.render.office.fc.hssf.record.DVALRecord;
import com.document.render.office.fc.hssf.record.DateWindow1904Record;
import com.document.render.office.fc.hssf.record.DefaultColWidthRecord;
import com.document.render.office.fc.hssf.record.DefaultRowHeightRecord;
import com.document.render.office.fc.hssf.record.DeltaRecord;
import com.document.render.office.fc.hssf.record.DimensionsRecord;
import com.document.render.office.fc.hssf.record.DrawingRecord;
import com.document.render.office.fc.hssf.record.DrawingSelectionRecord;
import com.document.render.office.fc.hssf.record.EOFRecord;
import com.document.render.office.fc.hssf.record.FeatRecord;
import com.document.render.office.fc.hssf.record.FormulaRecord;
import com.document.render.office.fc.hssf.record.GridsetRecord;
import com.document.render.office.fc.hssf.record.GutsRecord;
import com.document.render.office.fc.hssf.record.HyperlinkRecord;
import com.document.render.office.fc.hssf.record.IndexRecord;
import com.document.render.office.fc.hssf.record.IterationRecord;
import com.document.render.office.fc.hssf.record.LabelRecord;
import com.document.render.office.fc.hssf.record.LabelSSTRecord;
import com.document.render.office.fc.hssf.record.NumberRecord;
import com.document.render.office.fc.hssf.record.ObjRecord;
import com.document.render.office.fc.hssf.record.PaneRecord;
import com.document.render.office.fc.hssf.record.PrecisionRecord;
import com.document.render.office.fc.hssf.record.PrintGridlinesRecord;
import com.document.render.office.fc.hssf.record.PrintHeadersRecord;
import com.document.render.office.fc.hssf.record.RKRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordBase;
import com.document.render.office.fc.hssf.record.RefModeRecord;
import com.document.render.office.fc.hssf.record.RowRecord;
import com.document.render.office.fc.hssf.record.SCLRecord;
import com.document.render.office.fc.hssf.record.SaveRecalcRecord;
import com.document.render.office.fc.hssf.record.SelectionRecord;
import com.document.render.office.fc.hssf.record.SharedFormulaRecord;
import com.document.render.office.fc.hssf.record.TableRecord;
import com.document.render.office.fc.hssf.record.TextObjectRecord;
import com.document.render.office.fc.hssf.record.UncalcedRecord;
import com.document.render.office.fc.hssf.record.UnknownRecord;
import com.document.render.office.fc.hssf.record.WindowOneRecord;
import com.document.render.office.fc.hssf.record.WindowTwoRecord;
import com.document.render.office.fc.hssf.record.aggregates.ColumnInfoRecordsAggregate;
import com.document.render.office.fc.hssf.record.aggregates.ConditionalFormattingTable;
import com.document.render.office.fc.hssf.record.aggregates.DataValidityTable;
import com.document.render.office.fc.hssf.record.aggregates.MergedCellsTable;
import com.document.render.office.fc.hssf.record.aggregates.PageSettingsBlock;
import com.document.render.office.fc.hssf.record.aggregates.WorksheetProtectionBlock;
import com.document.render.office.fc.hssf.record.pivottable.ViewDefinitionRecord;

import java.util.List;



final class RecordOrderer {



    private RecordOrderer() {

    }


    public static void addNewSheetRecord(List<RecordBase> sheetRecords, RecordBase newRecord) {
        int index = findSheetInsertPos(sheetRecords, newRecord.getClass());
        sheetRecords.add(index, newRecord);
    }

    private static int findSheetInsertPos(List<RecordBase> records, Class<? extends RecordBase> recClass) {
        if (recClass == DataValidityTable.class) {
            return findDataValidationTableInsertPos(records);
        }
        if (recClass == MergedCellsTable.class) {
            return findInsertPosForNewMergedRecordTable(records);
        }
        if (recClass == ConditionalFormattingTable.class) {
            return findInsertPosForNewCondFormatTable(records);
        }
        if (recClass == GutsRecord.class) {
            return getGutsRecordInsertPos(records);
        }
        if (recClass == PageSettingsBlock.class) {
            return getPageBreakRecordInsertPos(records);
        }
        if (recClass == WorksheetProtectionBlock.class) {
            return getWorksheetProtectionBlockInsertPos(records);
        }
        throw new RuntimeException("Unexpected record class (" + recClass.getName() + ")");
    }


    private static int getWorksheetProtectionBlockInsertPos(List<RecordBase> records) {
        int i = getDimensionsIndex(records);
        while (i > 0) {
            i--;
            Object rb = records.get(i);
            if (!isProtectionSubsequentRecord(rb)) {
                return i + 1;
            }
        }
        throw new IllegalStateException("did not find insert pos for protection block");
    }



    private static boolean isProtectionSubsequentRecord(Object rb) {
        if (rb instanceof ColumnInfoRecordsAggregate) {
            return true;
        }
        if (rb instanceof Record) {
            Record record = (Record) rb;
            switch (record.getSid()) {
                case DefaultColWidthRecord.sid:
                case UnknownRecord.SORT_0090:
                    return true;
            }
        }
        return false;
    }

    private static int getPageBreakRecordInsertPos(List<RecordBase> records) {
        int dimensionsIndex = getDimensionsIndex(records);
        int i = dimensionsIndex - 1;
        while (i > 0) {
            i--;
            Object rb = records.get(i);
            if (isPageBreakPriorRecord(rb)) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find insert point for GUTS");
    }

    private static boolean isPageBreakPriorRecord(Object rb) {
        if (rb instanceof Record) {
            Record record = (Record) rb;
            switch (record.getSid()) {
                case BOFRecord.sid:
                case IndexRecord.sid:

                case UncalcedRecord.sid:
                case CalcCountRecord.sid:
                case CalcModeRecord.sid:
                case PrecisionRecord.sid:
                case RefModeRecord.sid:
                case DeltaRecord.sid:
                case IterationRecord.sid:
                case DateWindow1904Record.sid:
                case SaveRecalcRecord.sid:

                case PrintHeadersRecord.sid:
                case PrintGridlinesRecord.sid:
                case GridsetRecord.sid:
                case DefaultRowHeightRecord.sid:
                case UnknownRecord.SHEETPR_0081:
                    return true;

            }
        }
        return false;
    }


    private static int findInsertPosForNewCondFormatTable(List<RecordBase> records) {

        for (int i = records.size() - 2; i >= 0; i--) {
            Object rb = records.get(i);
            if (rb instanceof MergedCellsTable) {
                return i + 1;
            }
            if (rb instanceof DataValidityTable) {
                continue;
            }

            Record rec = (Record) rb;
            switch (rec.getSid()) {
                case WindowTwoRecord.sid:
                case SCLRecord.sid:
                case PaneRecord.sid:
                case SelectionRecord.sid:
                case UnknownRecord.STANDARDWIDTH_0099:

                case UnknownRecord.LABELRANGES_015F:
                case UnknownRecord.PHONETICPR_00EF:

                    return i + 1;


            }
        }
        throw new RuntimeException("Did not find Window2 record");
    }

    private static int findInsertPosForNewMergedRecordTable(List<RecordBase> records) {
        for (int i = records.size() - 2; i >= 0; i--) {
            Object rb = records.get(i);
            if (!(rb instanceof Record)) {


                continue;
            }
            Record rec = (Record) rb;
            switch (rec.getSid()) {

                case WindowTwoRecord.sid:
                case SCLRecord.sid:
                case PaneRecord.sid:
                case SelectionRecord.sid:

                case UnknownRecord.STANDARDWIDTH_0099:
                    return i + 1;
            }
        }
        throw new RuntimeException("Did not find Window2 record");
    }



    private static int findDataValidationTableInsertPos(List<RecordBase> records) {
        int i = records.size() - 1;
        if (!(records.get(i) instanceof EOFRecord)) {
            throw new IllegalStateException("Last sheet record should be EOFRecord");
        }
        while (i > 0) {
            i--;
            RecordBase rb = records.get(i);
            if (isDVTPriorRecord(rb)) {
                Record nextRec = (Record) records.get(i + 1);
                if (!isDVTSubsequentRecord(nextRec.getSid())) {
                    throw new IllegalStateException("Unexpected (" + nextRec.getClass().getName()
                            + ") found after (" + rb.getClass().getName() + ")");
                }
                return i + 1;
            }
            Record rec = (Record) rb;
            if (!isDVTSubsequentRecord(rec.getSid())) {
                throw new IllegalStateException("Unexpected (" + rec.getClass().getName()
                        + ") while looking for DV Table insert pos");
            }
        }
        return 0;
    }


    private static boolean isDVTPriorRecord(RecordBase rb) {
        if (rb instanceof MergedCellsTable || rb instanceof ConditionalFormattingTable) {
            return true;
        }
        short sid = ((Record) rb).getSid();
        switch (sid) {
            case WindowTwoRecord.sid:
            case UnknownRecord.SCL_00A0:
            case PaneRecord.sid:
            case SelectionRecord.sid:
            case UnknownRecord.STANDARDWIDTH_0099:

            case UnknownRecord.LABELRANGES_015F:
            case UnknownRecord.PHONETICPR_00EF:

            case HyperlinkRecord.sid:
            case UnknownRecord.QUICKTIP_0800:

            case UnknownRecord.CODENAME_1BA:
                return true;
        }
        return false;
    }

    private static boolean isDVTSubsequentRecord(short sid) {
        switch (sid) {
            case UnknownRecord.SHEETEXT_0862:
            case UnknownRecord.SHEETPROTECTION_0867:
            case FeatRecord.sid:
            case EOFRecord.sid:
                return true;
        }
        return false;
    }


    private static int getDimensionsIndex(List<RecordBase> records) {
        int nRecs = records.size();
        for (int i = 0; i < nRecs; i++) {
            if (records.get(i) instanceof DimensionsRecord) {
                return i;
            }
        }

        throw new RuntimeException("DimensionsRecord not found");
    }

    private static int getGutsRecordInsertPos(List<RecordBase> records) {
        int dimensionsIndex = getDimensionsIndex(records);
        int i = dimensionsIndex - 1;
        while (i > 0) {
            i--;
            RecordBase rb = records.get(i);
            if (isGutsPriorRecord(rb)) {
                return i + 1;
            }
        }
        throw new RuntimeException("Did not find insert point for GUTS");
    }

    private static boolean isGutsPriorRecord(RecordBase rb) {
        if (rb instanceof Record) {
            Record record = (Record) rb;
            switch (record.getSid()) {
                case BOFRecord.sid:
                case IndexRecord.sid:

                case UncalcedRecord.sid:
                case CalcCountRecord.sid:
                case CalcModeRecord.sid:
                case PrecisionRecord.sid:
                case RefModeRecord.sid:
                case DeltaRecord.sid:
                case IterationRecord.sid:
                case DateWindow1904Record.sid:
                case SaveRecalcRecord.sid:

                case PrintHeadersRecord.sid:
                case PrintGridlinesRecord.sid:
                case GridsetRecord.sid:
                    return true;

            }
        }
        return false;
    }


    public static boolean isEndOfRowBlock(int sid) {
        switch (sid) {
            case ViewDefinitionRecord.sid:

            case DrawingRecord.sid:
            case DrawingSelectionRecord.sid:
            case ObjRecord.sid:
            case TextObjectRecord.sid:

            case GutsRecord.sid:
            case WindowOneRecord.sid:

            case WindowTwoRecord.sid:
                return true;

            case DVALRecord.sid:
                return true;
            case EOFRecord.sid:

                throw new RuntimeException("Found EOFRecord before WindowTwoRecord was encountered");
        }
        return PageSettingsBlock.isComponentRecord(sid);
    }


    public static boolean isRowBlockRecord(int sid) {
        switch (sid) {
            case RowRecord.sid:

            case BlankRecord.sid:
            case BoolErrRecord.sid:
            case FormulaRecord.sid:
            case LabelRecord.sid:
            case LabelSSTRecord.sid:
            case NumberRecord.sid:
            case RKRecord.sid:

            case ArrayRecord.sid:
            case SharedFormulaRecord.sid:
            case TableRecord.sid:
                return true;
        }
        return false;
    }
}
