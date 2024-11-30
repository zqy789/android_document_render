

package com.document.render.office.fc.hssf.formula.function;

import java.util.Map;
import java.util.Set;


public final class FunctionMetadataRegistry {

    public static final String FUNCTION_NAME_IF = "IF";

    public static final int FUNCTION_INDEX_IF = 1;
    public static final short FUNCTION_INDEX_SUM = 4;
    public static final int FUNCTION_INDEX_CHOOSE = 100;
    public static final short FUNCTION_INDEX_INDIRECT = 148;
    public static final short FUNCTION_INDEX_EXTERNAL = 255;

    private static FunctionMetadataRegistry _instance;

    private final FunctionMetadata[] _functionDataByIndex;
    private final Map<String, FunctionMetadata> _functionDataByName;

     FunctionMetadataRegistry(FunctionMetadata[] functionDataByIndex, Map<String, FunctionMetadata> functionDataByName) {
        _functionDataByIndex = functionDataByIndex;
        _functionDataByName = functionDataByName;
    }

    private static FunctionMetadataRegistry getInstance() {
        if (_instance == null) {
            _instance = FunctionMetadataReader.createRegistry();
        }
        return _instance;
    }

    public static FunctionMetadata getFunctionByIndex(int index) {
        return getInstance().getFunctionByIndexInternal(index);
    }


    public static short lookupIndexByName(String name) {
        FunctionMetadata fd = getInstance().getFunctionByNameInternal(name);
        if (fd == null) {
            return -1;
        }
        return (short) fd.getIndex();
    }

    public static FunctionMetadata getFunctionByName(String name) {
        return getInstance().getFunctionByNameInternal(name);
    }

     Set<String> getAllFunctionNames() {
        return _functionDataByName.keySet();
    }

    private FunctionMetadata getFunctionByIndexInternal(int index) {
        return _functionDataByIndex[index];
    }

    private FunctionMetadata getFunctionByNameInternal(String name) {
        return _functionDataByName.get(name);
    }
}
