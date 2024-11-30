

package com.document.render.office.fc.hssf.formula;


public interface IStabilityClassifier {


    IStabilityClassifier TOTALLY_IMMUTABLE = new IStabilityClassifier() {
        public boolean isCellFinal(int sheetIndex, int rowIndex, int columnIndex) {
            return true;
        }
    };


    boolean isCellFinal(int sheetIndex, int rowIndex, int columnIndex);
}
