

package com.document.render.office.fc.hssf.formula.udf;


import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class AggregatingUDFFinder implements UDFFinder {

    private final Collection<UDFFinder> _usedToolPacks;

    public AggregatingUDFFinder(UDFFinder... usedToolPacks) {
        _usedToolPacks = new ArrayList<UDFFinder>(usedToolPacks.length);
        _usedToolPacks.addAll(Arrays.asList(usedToolPacks));
    }


    public FreeRefFunction findFunction(String name) {
        FreeRefFunction evaluatorForFunction;
        for (UDFFinder pack : _usedToolPacks) {
            evaluatorForFunction = pack.findFunction(name);
            if (evaluatorForFunction != null) {
                return evaluatorForFunction;
            }
        }
        return null;
    }


    public void add(UDFFinder toolPack) {
        _usedToolPacks.add(toolPack);
    }
}
