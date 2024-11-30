

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;


public interface TwoDEval extends ValueEval {


    ValueEval getValue(int rowIndex, int columnIndex);

    int getWidth();

    int getHeight();


    boolean isRow();


    boolean isColumn();


    TwoDEval getRow(int rowIndex);


    TwoDEval getColumn(int columnIndex);



    boolean isSubTotal(int rowIndex, int columnIndex);

}
