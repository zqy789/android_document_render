

package com.document.render.office.fc.hssf.formula.function;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


final class FunctionDataBuilder {
    private final Map _functionDataByName;
    private final Map _functionDataByIndex;

    private final Set _mutatingFunctionIndexes;
    private int _maxFunctionIndex;

    public FunctionDataBuilder(int sizeEstimate) {
        _maxFunctionIndex = -1;
        _functionDataByName = new HashMap(sizeEstimate * 3 / 2);
        _functionDataByIndex = new HashMap(sizeEstimate * 3 / 2);
        _mutatingFunctionIndexes = new HashSet();
    }

    public void add(int functionIndex, String functionName, int minParams, int maxParams,
                    byte returnClassCode, byte[] parameterClassCodes, boolean hasFootnote) {
        FunctionMetadata fm = new FunctionMetadata(functionIndex, functionName, minParams, maxParams,
                returnClassCode, parameterClassCodes);

        Integer indexKey = Integer.valueOf(functionIndex);


        if (functionIndex > _maxFunctionIndex) {
            _maxFunctionIndex = functionIndex;
        }

        FunctionMetadata prevFM;
        prevFM = (FunctionMetadata) _functionDataByName.get(functionName);
        if (prevFM != null) {
            if (!hasFootnote || !_mutatingFunctionIndexes.contains(indexKey)) {
                throw new RuntimeException("Multiple entries for function name '" + functionName + "'");
            }
            _functionDataByIndex.remove(Integer.valueOf(prevFM.getIndex()));
        }
        prevFM = (FunctionMetadata) _functionDataByIndex.get(indexKey);
        if (prevFM != null) {
            if (!hasFootnote || !_mutatingFunctionIndexes.contains(indexKey)) {
                throw new RuntimeException("Multiple entries for function index (" + functionIndex + ")");
            }
            _functionDataByName.remove(prevFM.getName());
        }
        if (hasFootnote) {
            _mutatingFunctionIndexes.add(indexKey);
        }
        _functionDataByIndex.put(indexKey, fm);
        _functionDataByName.put(functionName, fm);
    }

    public FunctionMetadataRegistry build() {

        FunctionMetadata[] jumbledArray = new FunctionMetadata[_functionDataByName.size()];
        _functionDataByName.values().toArray(jumbledArray);
        FunctionMetadata[] fdIndexArray = new FunctionMetadata[_maxFunctionIndex + 1];
        for (int i = 0; i < jumbledArray.length; i++) {
            FunctionMetadata fd = jumbledArray[i];
            fdIndexArray[fd.getIndex()] = fd;
        }

        return new FunctionMetadataRegistry(fdIndexArray, _functionDataByName);
    }
}
