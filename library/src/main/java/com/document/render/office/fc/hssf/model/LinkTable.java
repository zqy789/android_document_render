

package com.document.render.office.fc.hssf.model;

import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.ErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.hssf.record.CRNCountRecord;
import com.document.render.office.fc.hssf.record.CRNRecord;
import com.document.render.office.fc.hssf.record.CountryRecord;
import com.document.render.office.fc.hssf.record.ExternSheetRecord;
import com.document.render.office.fc.hssf.record.ExternalNameRecord;
import com.document.render.office.fc.hssf.record.NameCommentRecord;
import com.document.render.office.fc.hssf.record.NameRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.SupBookRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



final class LinkTable {




    private final ExternSheetRecord _externSheetRecord;
    private final List<NameRecord> _definedNames;
    private final int _recordCount;
    private final WorkbookRecordList _workbookRecordList;
    private ExternalBookBlock[] _externalBookBlocks;
    public LinkTable(List inputList, int startIndex, WorkbookRecordList workbookRecordList, Map<String, NameCommentRecord> commentRecords) {

        _workbookRecordList = workbookRecordList;
        RecordStream rs = new RecordStream(inputList, startIndex);

        List<ExternalBookBlock> temp = new ArrayList<ExternalBookBlock>();
        while (rs.peekNextClass() == SupBookRecord.class) {
            temp.add(new ExternalBookBlock(rs));
        }

        _externalBookBlocks = new ExternalBookBlock[temp.size()];
        temp.toArray(_externalBookBlocks);
        temp.clear();

        if (_externalBookBlocks.length > 0) {

            if (rs.peekNextClass() != ExternSheetRecord.class) {

                _externSheetRecord = null;
            } else {
                _externSheetRecord = readExtSheetRecord(rs);
            }
        } else {
            _externSheetRecord = null;
        }

        _definedNames = new ArrayList<NameRecord>();


        while (true) {
            Class nextClass = rs.peekNextClass();
            if (nextClass == NameRecord.class) {
                NameRecord nr = (NameRecord) rs.getNext();
                _definedNames.add(nr);
            } else if (nextClass == NameCommentRecord.class) {
                NameCommentRecord ncr = (NameCommentRecord) rs.getNext();
                commentRecords.put(ncr.getNameText(), ncr);
            } else {
                break;
            }
        }

        _recordCount = rs.getCountRead();
        _workbookRecordList.getRecords().addAll(inputList.subList(startIndex, startIndex + _recordCount));
    }
    public LinkTable(int numberOfSheets, WorkbookRecordList workbookRecordList) {
        _workbookRecordList = workbookRecordList;
        _definedNames = new ArrayList<NameRecord>();
        _externalBookBlocks = new ExternalBookBlock[]{
                new ExternalBookBlock(numberOfSheets),
        };
        _externSheetRecord = new ExternSheetRecord();
        _recordCount = 2;



        SupBookRecord supbook = _externalBookBlocks[0].getExternalBookRecord();

        int idx = findFirstRecordLocBySid(CountryRecord.sid);
        if (idx < 0) {
            throw new RuntimeException("CountryRecord not found");
        }
        _workbookRecordList.add(idx + 1, _externSheetRecord);
        _workbookRecordList.add(idx + 1, supbook);
    }

    private static ExternSheetRecord readExtSheetRecord(RecordStream rs) {
        List<ExternSheetRecord> temp = new ArrayList<ExternSheetRecord>(2);
        while (rs.peekNextClass() == ExternSheetRecord.class) {
            temp.add((ExternSheetRecord) rs.getNext());
        }

        int nItems = temp.size();
        if (nItems < 1) {
            throw new RuntimeException("Expected an EXTERNSHEET record but got ("
                    + rs.peekNextClass().getName() + ")");
        }
        if (nItems == 1) {

            return temp.get(0);
        }


        ExternSheetRecord[] esrs = new ExternSheetRecord[nItems];
        temp.toArray(esrs);
        return ExternSheetRecord.combine(esrs);
    }

    private static boolean isDuplicatedNames(NameRecord firstName, NameRecord lastName) {
        return lastName.getNameText().equalsIgnoreCase(firstName.getNameText())
                && isSameSheetNames(firstName, lastName);
    }

    private static boolean isSameSheetNames(NameRecord firstName, NameRecord lastName) {
        return lastName.getSheetNumber() == firstName.getSheetNumber();
    }

    private static int getSheetIndex(String[] sheetNames, String sheetName) {
        for (int i = 0; i < sheetNames.length; i++) {
            if (sheetNames[i].equals(sheetName)) {
                return i;
            }

        }
        throw new RuntimeException("External workbook does not contain sheet '" + sheetName + "'");
    }


    public int getRecordCount() {
        return _recordCount;
    }


    public NameRecord getSpecificBuiltinRecord(byte builtInCode, int sheetNumber) {

        Iterator iterator = _definedNames.iterator();
        while (iterator.hasNext()) {
            NameRecord record = (NameRecord) iterator.next();


            if (record.getBuiltInName() == builtInCode && record.getSheetNumber() == sheetNumber) {
                return record;
            }
        }

        return null;
    }

    public void removeBuiltinRecord(byte name, int sheetIndex) {


        NameRecord record = getSpecificBuiltinRecord(name, sheetIndex);
        if (record != null) {
            _definedNames.remove(record);
        }

    }

    public int getNumNames() {
        return _definedNames.size();
    }

    public NameRecord getNameRecord(int index) {
        return _definedNames.get(index);
    }

    public void addName(NameRecord name) {
        _definedNames.add(name);



        int idx = findFirstRecordLocBySid(ExternSheetRecord.sid);
        if (idx == -1) idx = findFirstRecordLocBySid(SupBookRecord.sid);
        if (idx == -1) idx = findFirstRecordLocBySid(CountryRecord.sid);
        int countNames = _definedNames.size();
        _workbookRecordList.add(idx + countNames, name);
    }

    public void removeName(int namenum) {
        _definedNames.remove(namenum);
    }


    public boolean nameAlreadyExists(NameRecord name) {

        for (int i = getNumNames() - 1; i >= 0; i--) {
            NameRecord rec = getNameRecord(i);
            if (rec != name) {
                if (isDuplicatedNames(name, rec))
                    return true;
            }
        }
        return false;
    }

    public String[] getExternalBookAndSheetName(int extRefIndex) {
        int ebIx = _externSheetRecord.getExtbookIndexFromRefIndex(extRefIndex);
        SupBookRecord ebr = _externalBookBlocks[ebIx].getExternalBookRecord();
        if (!ebr.isExternalReferences()) {
            return null;
        }

        int shIx = _externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
        String usSheetName = null;
        if (shIx >= 0) {
            usSheetName = ebr.getSheetNames()[shIx];
        }
        return new String[]{
                ebr.getURL(),
                usSheetName,
        };
    }

    public int getExternalSheetIndex(String workbookName, String sheetName) {
        SupBookRecord ebrTarget = null;
        int externalBookIndex = -1;
        for (int i = 0; i < _externalBookBlocks.length; i++) {
            SupBookRecord ebr = _externalBookBlocks[i].getExternalBookRecord();
            if (!ebr.isExternalReferences()) {
                continue;
            }
            if (workbookName.equals(ebr.getURL())) {
                ebrTarget = ebr;
                externalBookIndex = i;
                break;
            }
        }
        if (ebrTarget == null) {
            throw new RuntimeException("No external workbook with name '" + workbookName + "'");
        }
        int sheetIndex = getSheetIndex(ebrTarget.getSheetNames(), sheetName);

        int result = _externSheetRecord.getRefIxForSheet(externalBookIndex, sheetIndex);
        if (result < 0) {
            throw new RuntimeException("ExternSheetRecord does not contain combination ("
                    + externalBookIndex + ", " + sheetIndex + ")");
        }
        return result;
    }


    public int getIndexToInternalSheet(int extRefIndex) {
        return _externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
    }

    public int getSheetIndexFromExternSheetIndex(int extRefIndex) {
        if (extRefIndex >= _externSheetRecord.getNumOfRefs()) {
            return -1;
        }
        return _externSheetRecord.getFirstSheetIndexFromRefIndex(extRefIndex);
    }

    public int checkExternSheet(int sheetIndex) {
        int thisWbIndex = -1;
        for (int i = 0; i < _externalBookBlocks.length; i++) {
            SupBookRecord ebr = _externalBookBlocks[i].getExternalBookRecord();
            if (ebr.isInternalReferences()) {
                thisWbIndex = i;
                break;
            }
        }
        if (thisWbIndex < 0) {
            throw new RuntimeException("Could not find 'internal references' EXTERNALBOOK");
        }


        int i = _externSheetRecord.getRefIxForSheet(thisWbIndex, sheetIndex);
        if (i >= 0) {
            return i;
        }

        return _externSheetRecord.addRef(thisWbIndex, sheetIndex, sheetIndex);
    }


    private int findFirstRecordLocBySid(short sid) {
        int index = 0;
        for (Iterator iterator = _workbookRecordList.iterator(); iterator.hasNext(); ) {
            Record record = (Record) iterator.next();

            if (record.getSid() == sid) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public String resolveNameXText(int refIndex, int definedNameIndex) {
        int extBookIndex = _externSheetRecord.getExtbookIndexFromRefIndex(refIndex);
        return _externalBookBlocks[extBookIndex].getNameText(definedNameIndex);
    }

    public int resolveNameXIx(int refIndex, int definedNameIndex) {
        int extBookIndex = _externSheetRecord.getExtbookIndexFromRefIndex(refIndex);
        return _externalBookBlocks[extBookIndex].getNameIx(definedNameIndex);
    }

    public NameXPtg getNameXPtg(String name) {

        for (int i = 0; i < _externalBookBlocks.length; i++) {
            int definedNameIndex = _externalBookBlocks[i].getIndexOfName(name);
            if (definedNameIndex < 0) {
                continue;
            }

            int sheetRefIndex = findRefIndexFromExtBookIndex(i);
            if (sheetRefIndex >= 0) {
                return new NameXPtg(sheetRefIndex, definedNameIndex);
            }
        }
        return null;
    }


    public NameXPtg addNameXPtg(String name) {
        int extBlockIndex = -1;
        ExternalBookBlock extBlock = null;


        for (int i = 0; i < _externalBookBlocks.length; i++) {
            SupBookRecord ebr = _externalBookBlocks[i].getExternalBookRecord();
            if (ebr.isAddInFunctions()) {
                extBlock = _externalBookBlocks[i];
                extBlockIndex = i;
                break;
            }
        }

        if (extBlock == null) {
            extBlock = new ExternalBookBlock();

            ExternalBookBlock[] tmp = new ExternalBookBlock[_externalBookBlocks.length + 1];
            System.arraycopy(_externalBookBlocks, 0, tmp, 0, _externalBookBlocks.length);
            tmp[tmp.length - 1] = extBlock;
            _externalBookBlocks = tmp;

            extBlockIndex = _externalBookBlocks.length - 1;


            int idx = findFirstRecordLocBySid(ExternSheetRecord.sid);
            _workbookRecordList.add(idx, extBlock.getExternalBookRecord());



            _externSheetRecord.addRef(_externalBookBlocks.length - 1, -2, -2);
        }


        ExternalNameRecord extNameRecord = new ExternalNameRecord();
        extNameRecord.setText(name);

        extNameRecord.setParsedExpression(new Ptg[]{ErrPtg.REF_INVALID});

        int nameIndex = extBlock.addExternalName(extNameRecord);
        int supLinkIndex = 0;


        for (Iterator iterator = _workbookRecordList.iterator(); iterator.hasNext(); supLinkIndex++) {
            Record record = (Record) iterator.next();
            if (record instanceof SupBookRecord) {
                if (((SupBookRecord) record).isAddInFunctions()) break;
            }
        }
        int numberOfNames = extBlock.getNumberOfNames();

        _workbookRecordList.add(supLinkIndex + numberOfNames, extNameRecord);
        int ix = _externSheetRecord.getRefIxForSheet(extBlockIndex, -2 );
        return new NameXPtg(ix, nameIndex);
    }

    private int findRefIndexFromExtBookIndex(int extBookIndex) {
        return _externSheetRecord.findRefIndexFromExtBookIndex(extBookIndex);
    }

    private static final class CRNBlock {

        private final CRNCountRecord _countRecord;
        private final CRNRecord[] _crns;

        public CRNBlock(RecordStream rs) {
            _countRecord = (CRNCountRecord) rs.getNext();
            int nCRNs = _countRecord.getNumberOfCRNs();
            CRNRecord[] crns = new CRNRecord[nCRNs];
            for (int i = 0; i < crns.length; i++) {
                crns[i] = (CRNRecord) rs.getNext();
            }
            _crns = crns;
        }

        public CRNRecord[] getCrns() {
            return _crns.clone();
        }
    }

    private static final class ExternalBookBlock {
        private final SupBookRecord _externalBookRecord;
        private final CRNBlock[] _crnBlocks;
        private ExternalNameRecord[] _externalNameRecords;

        public ExternalBookBlock(RecordStream rs) {
            _externalBookRecord = (SupBookRecord) rs.getNext();
            List<Object> temp = new ArrayList<Object>();
            while (rs.peekNextClass() == ExternalNameRecord.class) {
                temp.add(rs.getNext());
            }
            _externalNameRecords = new ExternalNameRecord[temp.size()];
            temp.toArray(_externalNameRecords);

            temp.clear();

            while (rs.peekNextClass() == CRNCountRecord.class) {
                temp.add(new CRNBlock(rs));
            }
            _crnBlocks = new CRNBlock[temp.size()];
            temp.toArray(_crnBlocks);
        }


        public ExternalBookBlock(int numberOfSheets) {
            _externalBookRecord = SupBookRecord.createInternalReferences((short) numberOfSheets);
            _externalNameRecords = new ExternalNameRecord[0];
            _crnBlocks = new CRNBlock[0];
        }


        public ExternalBookBlock() {
            _externalBookRecord = SupBookRecord.createAddInFunctions();
            _externalNameRecords = new ExternalNameRecord[0];
            _crnBlocks = new CRNBlock[0];
        }

        public SupBookRecord getExternalBookRecord() {
            return _externalBookRecord;
        }

        public String getNameText(int definedNameIndex) {
            return _externalNameRecords[definedNameIndex].getText();
        }

        public int getNameIx(int definedNameIndex) {
            return _externalNameRecords[definedNameIndex].getIx();
        }


        public int getIndexOfName(String name) {
            for (int i = 0; i < _externalNameRecords.length; i++) {
                if (_externalNameRecords[i].getText().equalsIgnoreCase(name)) {
                    return i;
                }
            }
            return -1;
        }

        public int getNumberOfNames() {
            return _externalNameRecords.length;
        }

        public int addExternalName(ExternalNameRecord rec) {
            ExternalNameRecord[] tmp = new ExternalNameRecord[_externalNameRecords.length + 1];
            System.arraycopy(_externalNameRecords, 0, tmp, 0, _externalNameRecords.length);
            tmp[tmp.length - 1] = rec;
            _externalNameRecords = tmp;
            return _externalNameRecords.length - 1;
        }
    }
}
