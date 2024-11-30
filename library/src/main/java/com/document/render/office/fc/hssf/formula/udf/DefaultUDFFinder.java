

package com.document.render.office.fc.hssf.formula.udf;

import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;

import java.util.HashMap;
import java.util.Map;



public final class DefaultUDFFinder implements UDFFinder {
    private final Map<String, FreeRefFunction> _functionsByName;

    public DefaultUDFFinder(String[] functionNames, FreeRefFunction[] functionImpls) {
        int nFuncs = functionNames.length;
        if (functionImpls.length != nFuncs) {
            throw new IllegalArgumentException(
                    "Mismatch in number of function names and implementations");
        }
        HashMap<String, FreeRefFunction> m = new HashMap<String, FreeRefFunction>(nFuncs * 3 / 2);
        for (int i = 0; i < functionImpls.length; i++) {
            m.put(functionNames[i].toUpperCase(), functionImpls[i]);
        }
        _functionsByName = m;
    }

    public FreeRefFunction findFunction(String name) {
        return _functionsByName.get(name.toUpperCase());
    }
}
