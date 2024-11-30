

package com.document.render.office.fc.hssf.formula.function;


public final class FunctionMetadata {

    private static final short FUNCTION_MAX_PARAMS = 30;

    private final int _index;
    private final String _name;
    private final int _minParams;
    private final int _maxParams;
    private final byte _returnClassCode;
    private final byte[] _parameterClassCodes;

     FunctionMetadata(int index, String name, int minParams, int maxParams,
                                   byte returnClassCode, byte[] parameterClassCodes) {
        _index = index;
        _name = name;
        _minParams = minParams;
        _maxParams = maxParams;
        _returnClassCode = returnClassCode;
        _parameterClassCodes = parameterClassCodes;
    }

    public int getIndex() {
        return _index;
    }

    public String getName() {
        return _name;
    }

    public int getMinParams() {
        return _minParams;
    }

    public int getMaxParams() {
        return _maxParams;
    }

    public boolean hasFixedArgsLength() {
        return _minParams == _maxParams;
    }

    public byte getReturnClassCode() {
        return _returnClassCode;
    }

    public byte[] getParameterClassCodes() {
        return _parameterClassCodes.clone();
    }


    public boolean hasUnlimitedVarags() {
        return FUNCTION_MAX_PARAMS == _maxParams;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");
        sb.append(_index).append(" ").append(_name);
        sb.append("]");
        return sb.toString();
    }
}
