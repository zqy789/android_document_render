

package com.document.render.office.fc.hssf.formula;


import com.document.render.office.fc.hssf.formula.function.FunctionMetadataRegistry;
import com.document.render.office.fc.hssf.formula.ptg.ArrayPtg;
import com.document.render.office.fc.hssf.formula.ptg.AttrPtg;
import com.document.render.office.fc.hssf.formula.ptg.FuncVarPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemAreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemFuncPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;


final class ParseNode {

    public static final ParseNode[] EMPTY_ARRAY = {};
    private final Ptg _token;
    private final ParseNode[] _children;
    private final int _tokenCount;
    private boolean _isIf;

    public ParseNode(Ptg token, ParseNode[] children) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null");
        }
        _token = token;
        _children = children;
        _isIf = isIf(token);
        int tokenCount = 1;
        for (int i = 0; i < children.length; i++) {
            tokenCount += children[i].getTokenCount();
        }
        if (_isIf) {

            tokenCount += children.length;
        }
        _tokenCount = tokenCount;
    }

    public ParseNode(Ptg token) {
        this(token, EMPTY_ARRAY);
    }

    public ParseNode(Ptg token, ParseNode child0) {
        this(token, new ParseNode[]{child0,});
    }

    public ParseNode(Ptg token, ParseNode child0, ParseNode child1) {
        this(token, new ParseNode[]{child0, child1,});
    }


    public static Ptg[] toTokenArray(ParseNode rootNode) {
        TokenCollector temp = new TokenCollector(rootNode.getTokenCount());
        rootNode.collectPtgs(temp);
        return temp.getResult();
    }

    private static boolean isIf(Ptg token) {
        if (token instanceof FuncVarPtg) {
            FuncVarPtg func = (FuncVarPtg) token;
            if (FunctionMetadataRegistry.FUNCTION_NAME_IF.equals(func.getName())) {
                return true;
            }
        }
        return false;
    }

    private int getTokenCount() {
        return _tokenCount;
    }

    public int getEncodedSize() {
        int result = _token instanceof ArrayPtg ? ArrayPtg.PLAIN_TOKEN_SIZE : _token.getSize();
        for (int i = 0; i < _children.length; i++) {
            result += _children[i].getEncodedSize();
        }
        return result;
    }

    private void collectPtgs(TokenCollector temp) {
        if (isIf(_token)) {
            collectIfPtgs(temp);
            return;
        }
        boolean isPreFixOperator = _token instanceof MemFuncPtg || _token instanceof MemAreaPtg;
        if (isPreFixOperator) {
            temp.add(_token);
        }
        for (int i = 0; i < getChildren().length; i++) {
            getChildren()[i].collectPtgs(temp);
        }
        if (!isPreFixOperator) {
            temp.add(_token);
        }
    }


    private void collectIfPtgs(TokenCollector temp) {


        getChildren()[0].collectPtgs(temp);


        int ifAttrIndex = temp.createPlaceholder();


        getChildren()[1].collectPtgs(temp);


        int skipAfterTrueParamIndex = temp.createPlaceholder();
        int trueParamSize = temp.sumTokenSizes(ifAttrIndex + 1, skipAfterTrueParamIndex);

        AttrPtg attrIf = AttrPtg.createIf(trueParamSize + 4);

        if (getChildren().length > 2) {



            getChildren()[2].collectPtgs(temp);

            int skipAfterFalseParamIndex = temp.createPlaceholder();

            int falseParamSize = temp.sumTokenSizes(skipAfterTrueParamIndex + 1, skipAfterFalseParamIndex);

            AttrPtg attrSkipAfterTrue = AttrPtg.createSkip(falseParamSize + 4 + 4 - 1);
            AttrPtg attrSkipAfterFalse = AttrPtg.createSkip(4 - 1);

            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue);
            temp.setPlaceholder(skipAfterFalseParamIndex, attrSkipAfterFalse);
        } else {

            AttrPtg attrSkipAfterTrue = AttrPtg.createSkip(4 - 1);

            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue);
        }
        temp.add(_token);
    }

    public Ptg getToken() {
        return _token;
    }

    public ParseNode[] getChildren() {
        return _children;
    }

    private static final class TokenCollector {

        private final Ptg[] _ptgs;
        private int _offset;

        public TokenCollector(int tokenCount) {
            _ptgs = new Ptg[tokenCount];
            _offset = 0;
        }

        public int sumTokenSizes(int fromIx, int toIx) {
            int result = 0;
            for (int i = fromIx; i < toIx; i++) {
                result += _ptgs[i].getSize();
            }
            return result;
        }

        public int createPlaceholder() {
            return _offset++;
        }

        public void add(Ptg token) {
            if (token == null) {
                throw new IllegalArgumentException("token must not be null");
            }
            _ptgs[_offset] = token;
            _offset++;
        }

        public void setPlaceholder(int index, Ptg token) {
            if (_ptgs[index] != null) {
                throw new IllegalStateException("Invalid placeholder index (" + index + ")");
            }
            _ptgs[index] = token;
        }

        public Ptg[] getResult() {
            return _ptgs;
        }
    }
}
