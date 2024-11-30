

package com.document.render.office.fc.hssf.formula.udf;


import com.document.render.office.fc.hssf.formula.atp.AnalysisToolPak;
import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;



public interface UDFFinder {
    public static final UDFFinder DEFAULT = new AggregatingUDFFinder(AnalysisToolPak.instance);


    FreeRefFunction findFunction(String name);
}
