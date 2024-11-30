

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.record.chart.AreaFormatRecord;
import com.document.render.office.fc.hssf.record.chart.AreaRecord;
import com.document.render.office.fc.hssf.record.chart.AxisLineFormatRecord;
import com.document.render.office.fc.hssf.record.chart.AxisOptionsRecord;
import com.document.render.office.fc.hssf.record.chart.AxisParentRecord;
import com.document.render.office.fc.hssf.record.chart.AxisRecord;
import com.document.render.office.fc.hssf.record.chart.AxisUsedRecord;
import com.document.render.office.fc.hssf.record.chart.BarRecord;
import com.document.render.office.fc.hssf.record.chart.BeginRecord;
import com.document.render.office.fc.hssf.record.chart.CatLabRecord;
import com.document.render.office.fc.hssf.record.chart.CategorySeriesAxisRecord;
import com.document.render.office.fc.hssf.record.chart.ChartEndBlockRecord;
import com.document.render.office.fc.hssf.record.chart.ChartEndObjectRecord;
import com.document.render.office.fc.hssf.record.chart.ChartFRTInfoRecord;
import com.document.render.office.fc.hssf.record.chart.ChartRecord;
import com.document.render.office.fc.hssf.record.chart.ChartStartBlockRecord;
import com.document.render.office.fc.hssf.record.chart.ChartStartObjectRecord;
import com.document.render.office.fc.hssf.record.chart.ChartTitleFormatRecord;
import com.document.render.office.fc.hssf.record.chart.DatRecord;
import com.document.render.office.fc.hssf.record.chart.DataFormatRecord;
import com.document.render.office.fc.hssf.record.chart.DataLabelExtensionRecord;
import com.document.render.office.fc.hssf.record.chart.DefaultDataLabelTextPropertiesRecord;
import com.document.render.office.fc.hssf.record.chart.EndRecord;
import com.document.render.office.fc.hssf.record.chart.FontBasisRecord;
import com.document.render.office.fc.hssf.record.chart.FontIndexRecord;
import com.document.render.office.fc.hssf.record.chart.FrameRecord;
import com.document.render.office.fc.hssf.record.chart.LegendRecord;
import com.document.render.office.fc.hssf.record.chart.LineFormatRecord;
import com.document.render.office.fc.hssf.record.chart.LinkedDataRecord;
import com.document.render.office.fc.hssf.record.chart.NumberFormatIndexRecord;
import com.document.render.office.fc.hssf.record.chart.ObjectLinkRecord;
import com.document.render.office.fc.hssf.record.chart.PlotAreaRecord;
import com.document.render.office.fc.hssf.record.chart.PlotGrowthRecord;
import com.document.render.office.fc.hssf.record.chart.SeriesLabelsRecord;
import com.document.render.office.fc.hssf.record.chart.SeriesListRecord;
import com.document.render.office.fc.hssf.record.chart.SeriesRecord;
import com.document.render.office.fc.hssf.record.chart.SeriesTextRecord;
import com.document.render.office.fc.hssf.record.chart.SeriesToChartGroupRecord;
import com.document.render.office.fc.hssf.record.chart.SheetPropertiesRecord;
import com.document.render.office.fc.hssf.record.chart.TextRecord;
import com.document.render.office.fc.hssf.record.chart.TickRecord;
import com.document.render.office.fc.hssf.record.chart.UnitsRecord;
import com.document.render.office.fc.hssf.record.chart.ValueRangeRecord;
import com.document.render.office.fc.hssf.record.pivottable.DataItemRecord;
import com.document.render.office.fc.hssf.record.pivottable.ExtendedPivotTableViewFieldsRecord;
import com.document.render.office.fc.hssf.record.pivottable.PageItemRecord;
import com.document.render.office.fc.hssf.record.pivottable.StreamIDRecord;
import com.document.render.office.fc.hssf.record.pivottable.ViewDefinitionRecord;
import com.document.render.office.fc.hssf.record.pivottable.ViewFieldsRecord;
import com.document.render.office.fc.hssf.record.pivottable.ViewSourceRecord;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.AbstractReader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



public final class RecordFactory {
    private static final int NUM_RECORDS = 512;
    private static final Class<?>[] CONSTRUCTOR_ARGS = {RecordInputStream.class,};
    
    @SuppressWarnings("unchecked")
    private static final Class<? extends Record>[] recordClasses = new Class[]{ArrayRecord.class,
            AutoFilterInfoRecord.class, BackupRecord.class, BlankRecord.class, BOFRecord.class,
            BookBoolRecord.class, BoolErrRecord.class, BottomMarginRecord.class,
            BoundSheetRecord.class, CalcCountRecord.class, CalcModeRecord.class, CFHeaderRecord.class,
            CFRuleRecord.class, ChartRecord.class, ChartTitleFormatRecord.class, CodepageRecord.class,
            ColumnInfoRecord.class, ContinueRecord.class, CountryRecord.class, CRNCountRecord.class,
            CRNRecord.class, DateWindow1904Record.class, DBCellRecord.class,
            DefaultColWidthRecord.class, DefaultRowHeightRecord.class, DeltaRecord.class,
            DimensionsRecord.class, DrawingGroupRecord.class, DrawingRecord.class,
            DrawingSelectionRecord.class, DSFRecord.class, DVALRecord.class, DVRecord.class,
            EOFRecord.class, ExtendedFormatRecord.class, ExternalNameRecord.class,
            ExternSheetRecord.class, ExtSSTRecord.class, FeatRecord.class, FeatHdrRecord.class,
            FilePassRecord.class, FileSharingRecord.class, FnGroupCountRecord.class, FontRecord.class,
            FooterRecord.class, FormatRecord.class, FormulaRecord.class, GridsetRecord.class,
            GutsRecord.class, HCenterRecord.class, HeaderRecord.class, HeaderFooterRecord.class,
            HideObjRecord.class, HorizontalPageBreakRecord.class, HyperlinkRecord.class,
            IndexRecord.class, InterfaceEndRecord.class, InterfaceHdrRecord.class,
            IterationRecord.class, LabelRecord.class, LabelSSTRecord.class, LeftMarginRecord.class,
            LegendRecord.class, MergeCellsRecord.class, MMSRecord.class, MulBlankRecord.class,
            MulRKRecord.class, NameRecord.class, NameCommentRecord.class, NoteRecord.class,
            NumberRecord.class, ObjectProtectRecord.class, ObjRecord.class, PaletteRecord.class,
            PaneRecord.class, PasswordRecord.class, PasswordRev4Record.class, PrecisionRecord.class,
            PrintGridlinesRecord.class, PrintHeadersRecord.class, PrintSetupRecord.class,
            ProtectionRev4Record.class, ProtectRecord.class, RecalcIdRecord.class, RefModeRecord.class,
            RefreshAllRecord.class, RightMarginRecord.class, RKRecord.class, RowRecord.class,
            SaveRecalcRecord.class, ScenarioProtectRecord.class, SelectionRecord.class,
            SeriesRecord.class, SeriesTextRecord.class, SharedFormulaRecord.class, SSTRecord.class,
            StringRecord.class, StyleRecord.class, SupBookRecord.class, TabIdRecord.class,
            TableRecord.class, TableStylesRecord.class, TextObjectRecord.class, TopMarginRecord.class,
            UncalcedRecord.class, UseSelFSRecord.class, UserSViewBegin.class, UserSViewEnd.class,
            ValueRangeRecord.class, VCenterRecord.class,
            VerticalPageBreakRecord.class,
            WindowOneRecord.class,
            WindowProtectRecord.class,
            WindowTwoRecord.class,
            WriteAccessRecord.class,
            WriteProtectRecord.class,
            WSBoolRecord.class,

            
            BeginRecord.class, ChartFRTInfoRecord.class, ChartStartBlockRecord.class,
            ChartEndBlockRecord.class,
            
            ChartStartObjectRecord.class, ChartEndObjectRecord.class, CatLabRecord.class,
            DataFormatRecord.class, EndRecord.class,
            LinkedDataRecord.class,
            SeriesToChartGroupRecord.class,    

            AreaFormatRecord.class, AreaRecord.class, AxisLineFormatRecord.class, AxisOptionsRecord.class,
            AxisParentRecord.class, AxisRecord.class, AxisUsedRecord.class, BarRecord.class,
            CategorySeriesAxisRecord.class,
            DatRecord.class, DefaultDataLabelTextPropertiesRecord.class, FontBasisRecord.class,
            FontIndexRecord.class, FrameRecord.class, LineFormatRecord.class,
            NumberFormatIndexRecord.class, PlotAreaRecord.class, PlotGrowthRecord.class,
             SeriesLabelsRecord.class, SeriesListRecord.class,
            SheetPropertiesRecord.class, TickRecord.class, UnitsRecord.class,

            
            DataItemRecord.class, ExtendedPivotTableViewFieldsRecord.class, PageItemRecord.class,
            StreamIDRecord.class, ViewDefinitionRecord.class, ViewFieldsRecord.class,
            ViewSourceRecord.class, DataLabelExtensionRecord.class, TextRecord.class,
            ObjectLinkRecord.class,};
    
    private static final Map<Integer, I_RecordCreator> _recordCreatorsById = recordsToMap(recordClasses);
    private static short[] _allKnownRecordSIDs;

    
    public static Class<? extends Record> getRecordClass(int sid) {
        I_RecordCreator rc = _recordCreatorsById.get(Integer.valueOf(sid));
        if (rc == null) {
            return null;
        }
        return rc.getRecordClass();
    }

    
    public static Record[] createRecord(RecordInputStream in) {

        Record record = createSingleRecord(in);
        if (record instanceof DBCellRecord) {
            
            return new Record[]{null,};
        }
        if (record instanceof RKRecord) {
            return new Record[]{convertToNumberRecord((RKRecord) record),};
        }
        if (record instanceof MulRKRecord) {
            return convertRKRecords((MulRKRecord) record);
        }
        return new Record[]{record,};
    }

    public static Record createSingleRecord(RecordInputStream in) {
        I_RecordCreator constructor = _recordCreatorsById.get(Integer.valueOf(in.getSid()));

        if (constructor == null) {
            return new UnknownRecord(in);
        }

        return constructor.create(in);
    }

    
    public static NumberRecord convertToNumberRecord(RKRecord rk) {
        NumberRecord num = new NumberRecord();

        num.setColumn(rk.getColumn());
        num.setRow(rk.getRow());
        num.setXFIndex(rk.getXFIndex());
        num.setValue(rk.getRKNumber());
        return num;
    }

    
    public static NumberRecord[] convertRKRecords(MulRKRecord mrk) {
        NumberRecord[] mulRecs = new NumberRecord[mrk.getNumColumns()];
        for (int k = 0; k < mrk.getNumColumns(); k++) {
            NumberRecord nr = new NumberRecord();

            nr.setColumn((short) (k + mrk.getFirstColumn()));
            nr.setRow(mrk.getRow());
            nr.setXFIndex(mrk.getXFAt(k));
            nr.setValue(mrk.getRKNumberAt(k));
            mulRecs[k] = nr;
        }
        return mulRecs;
    }

    
    public static BlankRecord[] convertBlankRecords(MulBlankRecord mbk) {
        BlankRecord[] mulRecs = new BlankRecord[mbk.getNumColumns()];
        for (int k = 0; k < mbk.getNumColumns(); k++) {
            BlankRecord br = new BlankRecord();

            br.setColumn((short) (k + mbk.getFirstColumn()));
            br.setRow(mbk.getRow());
            br.setXFIndex(mbk.getXFAt(k));
            mulRecs[k] = br;
        }
        return mulRecs;
    }

    
    public static short[] getAllKnownRecordSIDs() {
        if (_allKnownRecordSIDs == null) {
            short[] results = new short[_recordCreatorsById.size()];
            int i = 0;

            for (Iterator<Integer> iterator = _recordCreatorsById.keySet().iterator(); iterator
                    .hasNext(); ) {
                Integer sid = iterator.next();

                results[i++] = sid.shortValue();
            }
            Arrays.sort(results);
            _allKnownRecordSIDs = results;
        }

        return _allKnownRecordSIDs.clone();
    }

    
    private static Map<Integer, I_RecordCreator> recordsToMap(Class<? extends Record>[] records) {
        Map<Integer, I_RecordCreator> result = new HashMap<Integer, I_RecordCreator>();
        Set<Class<?>> uniqueRecClasses = new HashSet<Class<?>>(records.length * 3 / 2);

        for (int i = 0; i < records.length; i++) {

            Class<? extends Record> recClass = records[i];
            if (!Record.class.isAssignableFrom(recClass)) {
                throw new RuntimeException("Invalid record sub-class (" + recClass.getName() + ")");
            }
            if (Modifier.isAbstract(recClass.getModifiers())) {
                throw new RuntimeException("Invalid record class (" + recClass.getName()
                        + ") - must not be abstract");
            }
            if (!uniqueRecClasses.add(recClass)) {
                throw new RuntimeException("duplicate record class (" + recClass.getName() + ")");
            }

            int sid;
            try {
                sid = recClass.getField("sid").getShort(null);
            } catch (Exception illegalArgumentException) {
                throw new RecordFormatException("Unable to determine record types");
            }
            Integer key = Integer.valueOf(sid);
            if (result.containsKey(key)) {
                Class<?> prevClass = result.get(key).getRecordClass();
                throw new RuntimeException("duplicate record sid 0x"
                        + Integer.toHexString(sid).toUpperCase() + " for classes ("
                        + recClass.getName() + ") and (" + prevClass.getName() + ")");
            }
            result.put(key, getRecordCreator(recClass));
        }
        
        return result;
    }

    private static I_RecordCreator getRecordCreator(Class<? extends Record> recClass) {
        try {
            Constructor<? extends Record> constructor;
            constructor = recClass.getConstructor(CONSTRUCTOR_ARGS);
            return new ReflectionConstructorRecordCreator(constructor);
        } catch (NoSuchMethodException e) {
            
        }
        try {
            Method m = recClass.getDeclaredMethod("create", CONSTRUCTOR_ARGS);
            return new ReflectionMethodRecordCreator(m);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find constructor or create method for ("
                    + recClass.getName() + ").");
        }
    }

    
    public static List<Record> createRecords(InputStream in) throws RecordFormatException {
        return createRecords(in, null);
    }

    
    public static List<Record> createRecords(InputStream in, AbstractReader iAbortListener) throws RecordFormatException {

        List<Record> records = new ArrayList<Record>(NUM_RECORDS);

        RecordFactoryInputStream recStream = new RecordFactoryInputStream(in, true);

        Record record;
        while ((record = recStream.nextRecord()) != null) {
            if (iAbortListener != null && iAbortListener.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }
            records.add(record);
        }

        recStream.dispose();
        recStream = null;

        return records;
    }

    private interface I_RecordCreator {
        Record create(RecordInputStream in);

        Class<? extends Record> getRecordClass();
    }

    private static final class ReflectionConstructorRecordCreator implements I_RecordCreator {

        private final Constructor<? extends Record> _c;

        public ReflectionConstructorRecordCreator(Constructor<? extends Record> c) {
            _c = c;
        }

        @Keep
        public Record create(RecordInputStream in) {
            Object[] args = {in,};
            try {
                return _c.newInstance(args);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RecordFormatException("Unable to construct record instance",
                        e.getTargetException());
            }
        }

        public Class<? extends Record> getRecordClass() {
            return _c.getDeclaringClass();
        }
    }

    
    private static final class ReflectionMethodRecordCreator implements I_RecordCreator {

        private final Method _m;

        public ReflectionMethodRecordCreator(Method m) {
            _m = m;
        }

        @Keep
        public Record create(RecordInputStream in) {
            Object[] args = {in,};
            try {
                return (Record) _m.invoke(null, args);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RecordFormatException("Unable to construct record instance",
                        e.getTargetException());
            }
        }

        @SuppressWarnings("unchecked")
        public Class<? extends Record> getRecordClass() {
            return (Class<? extends Record>) _m.getDeclaringClass();
        }
    }
}
