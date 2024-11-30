

package com.document.render.office.fc.hssf.formula;


import com.document.render.office.fc.hssf.formula.ptg.NamePtg;
import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;



public interface EvaluationWorkbook {
    String getSheetName(int sheetIndex);


    int getSheetIndex(EvaluationSheet sheet);


    int getSheetIndex(String sheetName);

    EvaluationSheet getSheet(int sheetIndex);


    ExternalSheet getExternalSheet(int externSheetIndex);

    int convertFromExternSheetIndex(int externSheetIndex);

    ExternalName getExternalName(int externSheetIndex, int externNameIndex);

    EvaluationName getName(NamePtg namePtg);

    EvaluationName getName(String name, int sheetIndex);

    String resolveNameXText(NameXPtg ptg);

    Ptg[] getFormulaTokens(EvaluationCell cell);

    UDFFinder getUDFFinder();

    class ExternalSheet {
        private final String _workbookName;
        private final String _sheetName;

        public ExternalSheet(String workbookName, String sheetName) {
            _workbookName = workbookName;
            _sheetName = sheetName;
        }

        public String getWorkbookName() {
            return _workbookName;
        }

        public String getSheetName() {
            return _sheetName;
        }
    }

    class ExternalName {
        private final String _nameName;
        private final int _nameNumber;
        private final int _ix;

        public ExternalName(String nameName, int nameNumber, int ix) {
            _nameName = nameName;
            _nameNumber = nameNumber;
            _ix = ix;
        }

        public String getName() {
            return _nameName;
        }

        public int getNumber() {
            return _nameNumber;
        }

        public int getIx() {
            return _ix;
        }
    }
}
