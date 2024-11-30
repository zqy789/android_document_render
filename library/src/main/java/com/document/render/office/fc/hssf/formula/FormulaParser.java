

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.constant.fc.ErrorConstant;
import com.document.render.office.fc.hssf.formula.function.FunctionMetadata;
import com.document.render.office.fc.hssf.formula.function.FunctionMetadataRegistry;
import com.document.render.office.fc.hssf.formula.ptg.AbstractFunctionPtg;
import com.document.render.office.fc.hssf.formula.ptg.AddPtg;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.ArrayPtg;
import com.document.render.office.fc.hssf.formula.ptg.AttrPtg;
import com.document.render.office.fc.hssf.formula.ptg.BoolPtg;
import com.document.render.office.fc.hssf.formula.ptg.ConcatPtg;
import com.document.render.office.fc.hssf.formula.ptg.DividePtg;
import com.document.render.office.fc.hssf.formula.ptg.EqualPtg;
import com.document.render.office.fc.hssf.formula.ptg.ErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.FuncPtg;
import com.document.render.office.fc.hssf.formula.ptg.FuncVarPtg;
import com.document.render.office.fc.hssf.formula.ptg.GreaterEqualPtg;
import com.document.render.office.fc.hssf.formula.ptg.GreaterThanPtg;
import com.document.render.office.fc.hssf.formula.ptg.IntPtg;
import com.document.render.office.fc.hssf.formula.ptg.LessEqualPtg;
import com.document.render.office.fc.hssf.formula.ptg.LessThanPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemAreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemFuncPtg;
import com.document.render.office.fc.hssf.formula.ptg.MissingArgPtg;
import com.document.render.office.fc.hssf.formula.ptg.MultiplyPtg;
import com.document.render.office.fc.hssf.formula.ptg.NamePtg;
import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.hssf.formula.ptg.NotEqualPtg;
import com.document.render.office.fc.hssf.formula.ptg.NumberPtg;
import com.document.render.office.fc.hssf.formula.ptg.OperandPtg;
import com.document.render.office.fc.hssf.formula.ptg.OperationPtg;
import com.document.render.office.fc.hssf.formula.ptg.ParenthesisPtg;
import com.document.render.office.fc.hssf.formula.ptg.PercentPtg;
import com.document.render.office.fc.hssf.formula.ptg.PowerPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.RangePtg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.RefPtg;
import com.document.render.office.fc.hssf.formula.ptg.StringPtg;
import com.document.render.office.fc.hssf.formula.ptg.SubtractPtg;
import com.document.render.office.fc.hssf.formula.ptg.UnaryMinusPtg;
import com.document.render.office.fc.hssf.formula.ptg.UnaryPlusPtg;
import com.document.render.office.fc.hssf.formula.ptg.UnionPtg;
import com.document.render.office.fc.hssf.formula.ptg.ValueOperatorPtg;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.ss.usermodel.ErrorConstants;
import com.document.render.office.fc.ss.util.AreaReference;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.ss.util.CellReference.NameType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



public final class FormulaParser {

    private static final Pattern CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Za-z]+)?(\\$?[0-9]+)?");
    private static char TAB = '\t';
    private final String _formulaString;
    private final int _formulaLength;

    private int _pointer;

    private ParseNode _rootNode;

    private char look;
    private FormulaParsingWorkbook _book;
    private SpreadsheetVersion _ssVersion;
    private int _sheetIndex;


    private FormulaParser(String formula, FormulaParsingWorkbook book, int sheetIndex) {
        _formulaString = formula;
        _pointer = 0;
        _book = book;
        _ssVersion = book == null ? SpreadsheetVersion.EXCEL97 : book.getSpreadsheetVersion();
        _formulaLength = _formulaString.length();
        _sheetIndex = sheetIndex;
    }


    public static Ptg[] parse(String formula, FormulaParsingWorkbook workbook, int formulaType, int sheetIndex) {
        FormulaParser fp = new FormulaParser(formula, workbook, sheetIndex);
        fp.parse();
        return fp.getRPNPtg(formulaType);
    }


    private static boolean IsAlpha(char c) {
        return Character.isLetter(c) || c == '$' || c == '_';
    }


    private static boolean IsDigit(char c) {
        return Character.isDigit(c);
    }


    private static boolean IsWhite(char c) {
        return c == ' ' || c == TAB;
    }

    private static ParseNode augmentWithMemPtg(ParseNode root) {
        Ptg memPtg;
        if (needsMemFunc(root)) {
            memPtg = new MemFuncPtg(root.getEncodedSize());
        } else {
            memPtg = new MemAreaPtg(root.getEncodedSize());
        }
        return new ParseNode(memPtg, root);
    }


    private static boolean needsMemFunc(ParseNode root) {
        Ptg token = root.getToken();
        if (token instanceof AbstractFunctionPtg) {
            return true;
        }
        if (token instanceof ExternSheetReferenceToken) {
            return true;
        }
        if (token instanceof NamePtg || token instanceof NameXPtg) {
            return true;
        }

        if (token instanceof OperationPtg || token instanceof ParenthesisPtg) {

            for (ParseNode child : root.getChildren()) {
                if (needsMemFunc(child)) {
                    return true;
                }
            }
            return false;
        }
        if (token instanceof OperandPtg) {
            return false;
        }
        if (token instanceof OperationPtg) {
            return true;
        }

        return false;
    }


    private static void checkValidRangeOperand(String sideName, int currentParsePosition, ParseNode pn) {
        if (!isValidRangeOperand(pn)) {
            throw new FormulaParseException("The " + sideName
                    + " of the range operator ':' at position "
                    + currentParsePosition + " is not a proper reference.");
        }
    }


    private static boolean isValidRangeOperand(ParseNode a) {
        Ptg tkn = a.getToken();

        if (tkn instanceof OperandPtg) {

            return true;
        }


        if (tkn instanceof AbstractFunctionPtg) {
            AbstractFunctionPtg afp = (AbstractFunctionPtg) tkn;
            byte returnClass = afp.getDefaultOperandClass();
            return Ptg.CLASS_REF == returnClass;
        }
        if (tkn instanceof ValueOperatorPtg) {
            return false;
        }
        if (tkn instanceof OperationPtg) {
            return true;
        }


        if (tkn instanceof ParenthesisPtg) {

            return isValidRangeOperand(a.getChildren()[0]);
        }


        if (tkn == ErrPtg.REF_INVALID) {
            return true;
        }


        return false;
    }


    private static boolean isValidDefinedNameChar(char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return true;
        }
        switch (ch) {
            case '.':
            case '_':
            case '?':
            case '\\':
                return true;
        }
        return false;
    }

    private static AreaReference createAreaRef(SimpleRangePart part1, SimpleRangePart part2) {
        if (!part1.isCompatibleForArea(part2)) {
            throw new FormulaParseException("has incompatible parts: '"
                    + part1.getRep() + "' and '" + part2.getRep() + "'.");
        }
        if (part1.isRow()) {
            return AreaReference.getWholeRow(part1.getRep(), part2.getRep());
        }
        if (part1.isColumn()) {
            return AreaReference.getWholeColumn(part1.getRep(), part2.getRep());
        }
        return new AreaReference(part1.getCellReference(), part2.getCellReference());
    }


    private static boolean isUnquotedSheetNameChar(char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return true;
        }
        switch (ch) {
            case '.':
            case '_':
                return true;
        }
        return false;
    }

    private static boolean isArgumentDelimiter(char ch) {
        return ch == ',' || ch == ')';
    }

    private static Double convertArrayNumber(Ptg ptg, boolean isPositive) {
        double value;
        if (ptg instanceof IntPtg) {
            value = ((IntPtg) ptg).getValue();
        } else if (ptg instanceof NumberPtg) {
            value = ((NumberPtg) ptg).getValue();
        } else {
            throw new RuntimeException("Unexpected ptg (" + ptg.getClass().getName() + ")");
        }
        if (!isPositive) {
            value = -value;
        }
        return new Double(value);
    }


    private static Ptg getNumberPtgFromString(String number1, String number2, String exponent) {
        StringBuffer number = new StringBuffer();

        if (number2 == null) {
            number.append(number1);

            if (exponent != null) {
                number.append('E');
                number.append(exponent);
            }

            String numberStr = number.toString();
            int intVal;
            try {
                intVal = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                return new NumberPtg(numberStr);
            }
            if (IntPtg.isInRange(intVal)) {
                return new IntPtg(intVal);
            }
            return new NumberPtg(numberStr);
        }

        if (number1 != null) {
            number.append(number1);
        }

        number.append('.');
        number.append(number2);

        if (exponent != null) {
            number.append('E');
            number.append(exponent);
        }

        return new NumberPtg(number.toString());
    }


    private void GetChar() {

        if (_pointer > _formulaLength) {
            throw new RuntimeException("too far");
        }
        if (_pointer < _formulaLength) {
            look = _formulaString.charAt(_pointer);
        } else {


            look = (char) 0;
        }
        _pointer++;

    }

    private void resetPointer(int ptr) {
        _pointer = ptr;
        if (_pointer <= _formulaLength) {
            look = _formulaString.charAt(_pointer - 1);
        } else {


            look = (char) 0;
        }
    }


    private RuntimeException expected(String s) {
        String msg;

        if (look == '=' && _formulaString.substring(0, _pointer - 1).trim().length() < 1) {
            msg = "The specified formula '" + _formulaString
                    + "' starts with an equals sign which is not allowed.";
        } else {
            msg = "Parse error near char " + (_pointer - 1) + " '" + look + "'"
                    + " in specified formula '" + _formulaString + "'. Expected "
                    + s;
        }
        return new FormulaParseException(msg);
    }


    private void SkipWhite() {
        while (IsWhite(look)) {
            GetChar();
        }
    }


    private void Match(char x) {
        if (look != x) {
            throw expected("'" + x + "'");
        }
        GetChar();
    }


    private String GetNum() {
        StringBuffer value = new StringBuffer();

        while (IsDigit(this.look)) {
            value.append(this.look);
            GetChar();
        }
        return value.length() == 0 ? null : value.toString();
    }

    private ParseNode parseRangeExpression() {
        ParseNode result = parseRangeable();
        boolean hasRange = false;
        while (look == ':') {
            int pos = _pointer;
            GetChar();
            ParseNode nextPart = parseRangeable();





            checkValidRangeOperand("LHS", pos, result);
            checkValidRangeOperand("RHS", pos, nextPart);

            ParseNode[] children = {result, nextPart,};
            result = new ParseNode(RangePtg.instance, children);
            hasRange = true;
        }
        if (hasRange) {
            return augmentWithMemPtg(result);
        }
        return result;
    }


    private ParseNode parseRangeable() {
        SkipWhite();
        int savePointer = _pointer;
        SheetIdentifier sheetIden = parseSheetName();
        if (sheetIden == null) {
            resetPointer(savePointer);
        } else {
            SkipWhite();
            savePointer = _pointer;
        }

        SimpleRangePart part1 = parseSimpleRangePart();
        if (part1 == null) {
            if (sheetIden != null) {
                if (look == '#') {
                    return new ParseNode(ErrPtg.valueOf(parseErrorLiteral()));
                } else {
                    throw new FormulaParseException("Cell reference expected after sheet name at index "
                            + _pointer + ".");
                }
            }
            return parseNonRange(savePointer);
        }
        boolean whiteAfterPart1 = IsWhite(look);
        if (whiteAfterPart1) {
            SkipWhite();
        }

        if (look == ':') {
            int colonPos = _pointer;
            GetChar();
            SkipWhite();
            SimpleRangePart part2 = parseSimpleRangePart();
            if (part2 != null && !part1.isCompatibleForArea(part2)) {



                part2 = null;
            }
            if (part2 == null) {


                resetPointer(colonPos);
                if (!part1.isCell()) {
                    String prefix;
                    if (sheetIden == null) {
                        prefix = "";
                    } else {
                        prefix = "'" + sheetIden.getSheetIdentifier().getName() + '!';
                    }
                    throw new FormulaParseException(prefix + part1.getRep() + "' is not a proper reference.");
                }
                return createAreaRefParseNode(sheetIden, part1, part2);
            }
            return createAreaRefParseNode(sheetIden, part1, part2);
        }

        if (look == '.') {
            GetChar();
            int dotCount = 1;
            while (look == '.') {
                dotCount++;
                GetChar();
            }
            boolean whiteBeforePart2 = IsWhite(look);

            SkipWhite();
            SimpleRangePart part2 = parseSimpleRangePart();
            String part1And2 = _formulaString.substring(savePointer - 1, _pointer - 1);
            if (part2 == null) {
                if (sheetIden != null) {
                    throw new FormulaParseException("Complete area reference expected after sheet name at index "
                            + _pointer + ".");
                }
                return parseNonRange(savePointer);
            }


            if (whiteAfterPart1 || whiteBeforePart2) {
                if (part1.isRowOrColumn() || part2.isRowOrColumn()) {


                    throw new FormulaParseException("Dotted range (full row or column) expression '"
                            + part1And2 + "' must not contain whitespace.");
                }
                return createAreaRefParseNode(sheetIden, part1, part2);
            }

            if (dotCount == 1 && part1.isRow() && part2.isRow()) {

                return parseNonRange(savePointer);
            }

            if (part1.isRowOrColumn() || part2.isRowOrColumn()) {
                if (dotCount != 2) {
                    throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2
                            + "' must have exactly 2 dots.");
                }
            }
            return createAreaRefParseNode(sheetIden, part1, part2);
        }
        if (part1.isCell() && isValidCellReference(part1.getRep())) {
            return createAreaRefParseNode(sheetIden, part1, null);
        }
        if (sheetIden != null) {
            throw new FormulaParseException("Second part of cell reference expected after sheet name at index "
                    + _pointer + ".");
        }

        return parseNonRange(savePointer);
    }


    private ParseNode parseNonRange(int savePointer) {
        resetPointer(savePointer);

        if (Character.isDigit(look)) {
            return new ParseNode(parseNumber());
        }
        if (look == '"') {
            return new ParseNode(new StringPtg(parseStringLiteral()));
        }


        StringBuilder sb = new StringBuilder();


        if (!Character.isLetter(look) && look != '_') {
            throw expected("number, string, or defined name");
        }
        while (isValidDefinedNameChar(look)) {
            sb.append(look);
            GetChar();
        }
        SkipWhite();
        String name = sb.toString();
        if (look == '(') {
            return function(name);
        }
        if (name.equalsIgnoreCase("TRUE") || name.equalsIgnoreCase("FALSE")) {
            return new ParseNode(BoolPtg.valueOf(name.equalsIgnoreCase("TRUE")));
        }
        if (_book == null) {

            throw new IllegalStateException("Need book to evaluate name '" + name + "'");
        }
        EvaluationName evalName = _book.getName(name, _sheetIndex);
        if (evalName == null) {
            throw new FormulaParseException("Specified named range '"
                    + name + "' does not exist in the current workbook.");
        }
        if (evalName.isRange()) {
            return new ParseNode(evalName.createPtg());
        }

        throw new FormulaParseException("Specified name '"
                + name + "' is not a range as expected.");
    }


    private ParseNode createAreaRefParseNode(SheetIdentifier sheetIden, SimpleRangePart part1,
                                             SimpleRangePart part2) throws FormulaParseException {

        int extIx;
        if (sheetIden == null) {
            extIx = Integer.MIN_VALUE;
        } else {
            String sName = sheetIden.getSheetIdentifier().getName();
            if (sheetIden.getBookName() == null) {
                extIx = _book.getExternalSheetIndex(sName);
            } else {
                extIx = _book.getExternalSheetIndex(sheetIden.getBookName(), sName);
            }
        }
        Ptg ptg;
        if (part2 == null) {
            CellReference cr = part1.getCellReference();
            if (sheetIden == null) {
                ptg = new RefPtg(cr);
            } else {
                ptg = new Ref3DPtg(cr, extIx);
            }
        } else {
            AreaReference areaRef = createAreaRef(part1, part2);

            if (sheetIden == null) {
                ptg = new AreaPtg(areaRef);
            } else {
                ptg = new Area3DPtg(areaRef, extIx);
            }
        }
        return new ParseNode(ptg);
    }


    private SimpleRangePart parseSimpleRangePart() {
        int ptr = _pointer - 1;
        boolean hasDigits = false;
        boolean hasLetters = false;
        while (ptr < _formulaLength) {
            char ch = _formulaString.charAt(ptr);
            if (Character.isDigit(ch)) {
                hasDigits = true;
            } else if (Character.isLetter(ch)) {
                hasLetters = true;
            } else if (ch == '$' || ch == '_') {

            } else {
                break;
            }
            ptr++;
        }
        if (ptr <= _pointer - 1) {
            return null;
        }
        String rep = _formulaString.substring(_pointer - 1, ptr);
        if (!CELL_REF_PATTERN.matcher(rep).matches()) {
            return null;
        }

        if (hasLetters && hasDigits) {
            if (!isValidCellReference(rep)) {
                return null;
            }
        } else if (hasLetters) {
            if (!CellReference.isColumnWithnRange(rep.replace("$", ""), _ssVersion)) {
                return null;
            }
        } else if (hasDigits) {
            int i;
            try {
                i = Integer.parseInt(rep.replace("$", ""));
            } catch (NumberFormatException e) {
                return null;
            }
            if (i < 1 || i > 65536) {
                return null;
            }
        } else {

            return null;
        }


        resetPointer(ptr + 1);
        return new SimpleRangePart(rep, hasLetters, hasDigits);
    }


    private SheetIdentifier parseSheetName() {

        String bookName;
        if (look == '[') {
            StringBuilder sb = new StringBuilder();
            GetChar();
            while (look != ']') {
                sb.append(look);
                GetChar();
            }
            GetChar();
            bookName = sb.toString();
        } else {
            bookName = null;
        }

        if (look == '\'') {
            StringBuffer sb = new StringBuffer();

            Match('\'');
            boolean done = look == '\'';
            while (!done) {
                sb.append(look);
                GetChar();
                if (look == '\'') {
                    Match('\'');
                    done = look != '\'';
                }
            }

            Identifier iden = new Identifier(sb.toString(), true);

            SkipWhite();
            if (look == '!') {
                GetChar();
                return new SheetIdentifier(bookName, iden);
            }
            return null;
        }


        if (look == '_' || Character.isLetter(look)) {
            StringBuilder sb = new StringBuilder();

            while (isUnquotedSheetNameChar(look)) {
                sb.append(look);
                GetChar();
            }
            SkipWhite();
            if (look == '!') {
                GetChar();
                return new SheetIdentifier(bookName, new Identifier(sb.toString(), false));
            }
            return null;
        }
        return null;
    }


    private boolean isValidCellReference(String str) {

        boolean result = CellReference.classifyCellReference(str, _ssVersion) == NameType.CELL;

        if (result) {

            boolean isFunc = FunctionMetadataRegistry.getFunctionByName(str.toUpperCase()) != null;
            if (isFunc) {
                int savePointer = _pointer;
                resetPointer(_pointer + str.length());
                SkipWhite();


                result = look != '(';
                resetPointer(savePointer);
            }
        }
        return result;
    }



    private ParseNode function(String name) {
        Ptg nameToken = null;
        if (!AbstractFunctionPtg.isBuiltInFunctionName(name)) {



            if (_book == null) {

                throw new IllegalStateException("Need book to evaluate name '" + name + "'");
            }
            EvaluationName hName = _book.getName(name, _sheetIndex);
            if (hName == null) {

                nameToken = _book.getNameXPtg(name);
                if (nameToken == null) {
                    throw new FormulaParseException("Name '" + name
                            + "' is completely unknown in the current workbook");
                }
            } else {
                if (!hName.isFunctionName()) {
                    throw new FormulaParseException("Attempt to use name '" + name
                            + "' as a function, but defined name in workbook does not refer to a function");
                }



                nameToken = hName.createPtg();
            }
        }

        Match('(');
        ParseNode[] args = Arguments();
        Match(')');

        return getFunction(name, nameToken, args);
    }


    private ParseNode getFunction(String name, Ptg namePtg, ParseNode[] args) {

        FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByName(name.toUpperCase());
        int numArgs = args.length;
        if (fm == null) {
            if (namePtg == null) {
                throw new IllegalStateException("NamePtg must be supplied for external functions");
            }

            ParseNode[] allArgs = new ParseNode[numArgs + 1];
            allArgs[0] = new ParseNode(namePtg);
            System.arraycopy(args, 0, allArgs, 1, numArgs);
            return new ParseNode(FuncVarPtg.create(name, numArgs + 1), allArgs);
        }

        if (namePtg != null) {
            throw new IllegalStateException("NamePtg no applicable to internal functions");
        }
        boolean isVarArgs = !fm.hasFixedArgsLength();
        int funcIx = fm.getIndex();
        if (funcIx == FunctionMetadataRegistry.FUNCTION_INDEX_SUM && args.length == 1) {


            return new ParseNode(AttrPtg.getSumSingle(), args);

        }
        validateNumArgs(args.length, fm);

        AbstractFunctionPtg retval;
        if (isVarArgs) {
            retval = FuncVarPtg.create(name, numArgs);
        } else {
            retval = FuncPtg.create(funcIx);
        }
        return new ParseNode(retval, args);
    }

    private void validateNumArgs(int numArgs, FunctionMetadata fm) {
        if (numArgs < fm.getMinParams()) {
            String msg = "Too few arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg += "Expected " + fm.getMinParams();
            } else {
                msg += "At least " + fm.getMinParams() + " were expected";
            }
            msg += " but got " + numArgs + ".";
            throw new FormulaParseException(msg);
        }

        int maxArgs;
        if (fm.hasUnlimitedVarags()) {
            if (_book != null) {
                maxArgs = _book.getSpreadsheetVersion().getMaxFunctionArgs();
            } else {

                maxArgs = fm.getMaxParams();
            }
        } else {
            maxArgs = fm.getMaxParams();
        }

        if (numArgs > maxArgs) {
            String msg = "Too many arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg += "Expected " + maxArgs;
            } else {
                msg += "At most " + maxArgs + " were expected";
            }
            msg += " but got " + numArgs + ".";
            throw new FormulaParseException(msg);
        }
    }


    private ParseNode[] Arguments() {

        List<ParseNode> temp = new ArrayList<ParseNode>(2);
        SkipWhite();
        if (look == ')') {
            return ParseNode.EMPTY_ARRAY;
        }

        boolean missedPrevArg = true;
        int numArgs = 0;
        while (true) {
            SkipWhite();
            if (isArgumentDelimiter(look)) {
                if (missedPrevArg) {
                    temp.add(new ParseNode(MissingArgPtg.instance));
                    numArgs++;
                }
                if (look == ')') {
                    break;
                }
                Match(',');
                missedPrevArg = true;
                continue;
            }
            temp.add(comparisonExpression());
            numArgs++;
            missedPrevArg = false;
            SkipWhite();
            if (!isArgumentDelimiter(look)) {
                throw expected("',' or ')'");
            }
        }
        ParseNode[] result = new ParseNode[temp.size()];
        temp.toArray(result);
        return result;
    }


    private ParseNode powerFactor() {
        ParseNode result = percentFactor();
        while (true) {
            SkipWhite();
            if (look != '^') {
                return result;
            }
            Match('^');
            ParseNode other = percentFactor();
            result = new ParseNode(PowerPtg.instance, result, other);
        }
    }

    private ParseNode percentFactor() {
        ParseNode result = parseSimpleFactor();
        while (true) {
            SkipWhite();
            if (look != '%') {
                return result;
            }
            Match('%');
            result = new ParseNode(PercentPtg.instance, result);
        }
    }


    private ParseNode parseSimpleFactor() {
        SkipWhite();
        switch (look) {
            case '#':
                return new ParseNode(ErrPtg.valueOf(parseErrorLiteral()));
            case '-':
                Match('-');
                return parseUnary(false);
            case '+':
                Match('+');
                return parseUnary(true);
            case '(':
                Match('(');
                ParseNode inside = comparisonExpression();
                Match(')');
                return new ParseNode(ParenthesisPtg.instance, inside);
            case '"':
                return new ParseNode(new StringPtg(parseStringLiteral()));
            case '{':
                Match('{');
                ParseNode arrayNode = parseArray();
                Match('}');
                return arrayNode;
        }
        if (IsAlpha(look) || Character.isDigit(look) || look == '\'' || look == '[') {
            return parseRangeExpression();
        }
        if (look == '.') {
            return new ParseNode(parseNumber());
        }
        throw expected("cell ref or constant literal");
    }

    private ParseNode parseUnary(boolean isPlus) {

        boolean numberFollows = IsDigit(look) || look == '.';
        ParseNode factor = powerFactor();

        if (numberFollows) {


            Ptg token = factor.getToken();
            if (token instanceof NumberPtg) {
                if (isPlus) {
                    return factor;
                }
                token = new NumberPtg(-((NumberPtg) token).getValue());
                return new ParseNode(token);
            }
            if (token instanceof IntPtg) {
                if (isPlus) {
                    return factor;
                }
                int intVal = ((IntPtg) token).getValue();

                token = new NumberPtg(-intVal);
                return new ParseNode(token);
            }
        }
        return new ParseNode(isPlus ? UnaryPlusPtg.instance : UnaryMinusPtg.instance, factor);
    }

    private ParseNode parseArray() {
        List<Object[]> rowsData = new ArrayList<Object[]>();
        while (true) {
            Object[] singleRowData = parseArrayRow();
            rowsData.add(singleRowData);
            if (look == '}') {
                break;
            }
            if (look != ';') {
                throw expected("'}' or ';'");
            }
            Match(';');
        }
        int nRows = rowsData.size();
        Object[][] values2d = new Object[nRows][];
        rowsData.toArray(values2d);
        int nColumns = values2d[0].length;
        checkRowLengths(values2d, nColumns);

        return new ParseNode(new ArrayPtg(values2d));
    }

    private void checkRowLengths(Object[][] values2d, int nColumns) {
        for (int i = 0; i < values2d.length; i++) {
            int rowLen = values2d[i].length;
            if (rowLen != nColumns) {
                throw new FormulaParseException("Array row " + i + " has length " + rowLen
                        + " but row 0 has length " + nColumns);
            }
        }
    }

    private Object[] parseArrayRow() {
        List<Object> temp = new ArrayList<Object>();
        while (true) {
            temp.add(parseArrayItem());
            SkipWhite();
            switch (look) {
                case '}':
                case ';':
                    break;
                case ',':
                    Match(',');
                    continue;
                default:
                    throw expected("'}' or ','");

            }
            break;
        }

        Object[] result = new Object[temp.size()];
        temp.toArray(result);
        return result;
    }

    private Object parseArrayItem() {
        SkipWhite();
        switch (look) {
            case '"':
                return parseStringLiteral();
            case '#':
                return ErrorConstant.valueOf(parseErrorLiteral());
            case 'F':
            case 'f':
            case 'T':
            case 't':
                return parseBooleanLiteral();
            case '-':
                Match('-');
                SkipWhite();
                return convertArrayNumber(parseNumber(), false);
        }

        return convertArrayNumber(parseNumber(), true);
    }

    private Boolean parseBooleanLiteral() {
        String iden = parseUnquotedIdentifier();
        if ("TRUE".equalsIgnoreCase(iden)) {
            return Boolean.TRUE;
        }
        if ("FALSE".equalsIgnoreCase(iden)) {
            return Boolean.FALSE;
        }
        throw expected("'TRUE' or 'FALSE'");
    }

    private Ptg parseNumber() {
        String number2 = null;
        String exponent = null;
        String number1 = GetNum();

        if (look == '.') {
            GetChar();
            number2 = GetNum();
        }

        if (look == 'E') {
            GetChar();

            String sign = "";
            if (look == '+') {
                GetChar();
            } else if (look == '-') {
                GetChar();
                sign = "-";
            }

            String number = GetNum();
            if (number == null) {
                throw expected("Integer");
            }
            exponent = sign + number;
        }

        if (number1 == null && number2 == null) {
            throw expected("Integer");
        }

        return getNumberPtgFromString(number1, number2, exponent);
    }

    private int parseErrorLiteral() {
        Match('#');
        String part1 = parseUnquotedIdentifier().toUpperCase();
        if (part1 == null) {
            throw expected("remainder of error constant literal");
        }

        switch (part1.charAt(0)) {
            case 'V':
                if (part1.equals("VALUE")) {
                    Match('!');
                    return ErrorConstants.ERROR_VALUE;
                }
                throw expected("#VALUE!");
            case 'R':
                if (part1.equals("REF")) {
                    Match('!');
                    return ErrorConstants.ERROR_REF;
                }
                throw expected("#REF!");
            case 'D':
                if (part1.equals("DIV")) {
                    Match('/');
                    Match('0');
                    Match('!');
                    return ErrorConstants.ERROR_DIV_0;
                }
                throw expected("#DIV/0!");
            case 'N':
                if (part1.equals("NAME")) {
                    Match('?');
                    return ErrorConstants.ERROR_NAME;
                }
                if (part1.equals("NUM")) {
                    Match('!');
                    return ErrorConstants.ERROR_NUM;
                }
                if (part1.equals("NULL")) {
                    Match('!');
                    return ErrorConstants.ERROR_NULL;
                }
                if (part1.equals("N")) {
                    Match('/');
                    if (look != 'A' && look != 'a') {
                        throw expected("#N/A");
                    }
                    Match(look);

                    return ErrorConstants.ERROR_NA;
                }
                throw expected("#NAME?, #NUM!, #NULL! or #N/A");

        }
        throw expected("#VALUE!, #REF!, #DIV/0!, #NAME?, #NUM!, #NULL! or #N/A");
    }

    private String parseUnquotedIdentifier() {
        if (look == '\'') {
            throw expected("unquoted identifier");
        }
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(look) || look == '.') {
            sb.append(look);
            GetChar();
        }
        if (sb.length() < 1) {
            return null;
        }

        return sb.toString();
    }

    private String parseStringLiteral() {
        Match('"');

        StringBuffer token = new StringBuffer();
        while (true) {
            if (look == '"') {
                GetChar();
                if (look != '"') {
                    break;
                }
            }
            token.append(look);
            GetChar();
        }
        return token.toString();
    }


    private ParseNode Term() {
        ParseNode result = powerFactor();
        while (true) {
            SkipWhite();
            Ptg operator;
            switch (look) {
                case '*':
                    Match('*');
                    operator = MultiplyPtg.instance;
                    break;
                case '/':
                    Match('/');
                    operator = DividePtg.instance;
                    break;
                default:
                    return result;
            }
            ParseNode other = powerFactor();
            result = new ParseNode(operator, result, other);
        }
    }

    private ParseNode unionExpression() {
        ParseNode result = comparisonExpression();
        boolean hasUnions = false;
        while (true) {
            SkipWhite();
            switch (look) {
                case ',':
                    GetChar();
                    hasUnions = true;
                    ParseNode other = comparisonExpression();
                    result = new ParseNode(UnionPtg.instance, result, other);
                    continue;
            }
            if (hasUnions) {
                return augmentWithMemPtg(result);
            }
            return result;
        }
    }

    private ParseNode comparisonExpression() {
        ParseNode result = concatExpression();
        while (true) {
            SkipWhite();
            switch (look) {
                case '=':
                case '>':
                case '<':
                    Ptg comparisonToken = getComparisonToken();
                    ParseNode other = concatExpression();
                    result = new ParseNode(comparisonToken, result, other);
                    continue;
            }
            return result;
        }
    }

    private Ptg getComparisonToken() {
        if (look == '=') {
            Match(look);
            return EqualPtg.instance;
        }
        boolean isGreater = look == '>';
        Match(look);
        if (isGreater) {
            if (look == '=') {
                Match('=');
                return GreaterEqualPtg.instance;
            }
            return GreaterThanPtg.instance;
        }
        switch (look) {
            case '=':
                Match('=');
                return LessEqualPtg.instance;
            case '>':
                Match('>');
                return NotEqualPtg.instance;
        }
        return LessThanPtg.instance;
    }

    private ParseNode concatExpression() {
        ParseNode result = additiveExpression();
        while (true) {
            SkipWhite();
            if (look != '&') {
                break;
            }
            Match('&');
            ParseNode other = additiveExpression();
            result = new ParseNode(ConcatPtg.instance, result, other);
        }
        return result;
    }


    private ParseNode additiveExpression() {
        ParseNode result = Term();
        while (true) {
            SkipWhite();
            Ptg operator;
            switch (look) {
                case '+':
                    Match('+');
                    operator = AddPtg.instance;
                    break;
                case '-':
                    Match('-');
                    operator = SubtractPtg.instance;
                    break;
                default:
                    return result;
            }
            ParseNode other = Term();
            result = new ParseNode(operator, result, other);
        }
    }


    private void parse() {
        _pointer = 0;
        GetChar();
        _rootNode = unionExpression();

        if (_pointer <= _formulaLength) {
            String msg = "Unused input [" + _formulaString.substring(_pointer - 1)
                    + "] after attempting to parse the formula [" + _formulaString + "]";
            throw new FormulaParseException(msg);
        }
    }

    private Ptg[] getRPNPtg(int formulaType) {
        OperandClassTransformer oct = new OperandClassTransformer(formulaType);

        oct.transformFormula(_rootNode);
        return ParseNode.toTokenArray(_rootNode);
    }

    private static final class Identifier {
        private final String _name;
        private final boolean _isQuoted;

        public Identifier(String name, boolean isQuoted) {
            _name = name;
            _isQuoted = isQuoted;
        }

        public String getName() {
            return _name;
        }

        public boolean isQuoted() {
            return _isQuoted;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            if (_isQuoted) {
                sb.append("'").append(_name).append("'");
            } else {
                sb.append(_name);
            }
            sb.append("]");
            return sb.toString();
        }
    }





    private static final class SheetIdentifier {


        private final String _bookName;
        private final Identifier _sheetIdentifier;

        public SheetIdentifier(String bookName, Identifier sheetIdentifier) {
            _bookName = bookName;
            _sheetIdentifier = sheetIdentifier;
        }

        public String getBookName() {
            return _bookName;
        }

        public Identifier getSheetIdentifier() {
            return _sheetIdentifier;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer(64);
            sb.append(getClass().getName());
            sb.append(" [");
            if (_bookName != null) {
                sb.append(" [").append(_sheetIdentifier.getName()).append("]");
            }
            if (_sheetIdentifier.isQuoted()) {
                sb.append("'").append(_sheetIdentifier.getName()).append("'");
            } else {
                sb.append(_sheetIdentifier.getName());
            }
            sb.append("]");
            return sb.toString();
        }
    }


    private static final class SimpleRangePart {
        private final Type _type;
        private final String _rep;
        public SimpleRangePart(String rep, boolean hasLetters, boolean hasNumbers) {
            _rep = rep;
            _type = Type.get(hasLetters, hasNumbers);
        }

        public boolean isCell() {
            return _type == Type.CELL;
        }

        public boolean isRowOrColumn() {
            return _type != Type.CELL;
        }

        public CellReference getCellReference() {
            if (_type != Type.CELL) {
                throw new IllegalStateException("Not applicable to this type");
            }
            return new CellReference(_rep);
        }

        public boolean isColumn() {
            return _type == Type.COLUMN;
        }

        public boolean isRow() {
            return _type == Type.ROW;
        }

        public String getRep() {
            return _rep;
        }


        public boolean isCompatibleForArea(SimpleRangePart part2) {
            return _type == part2._type;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(_rep);
            sb.append("]");
            return sb.toString();
        }

        private enum Type {
            CELL, ROW, COLUMN;

            public static Type get(boolean hasLetters, boolean hasDigits) {
                if (hasLetters) {
                    return hasDigits ? CELL : COLUMN;
                }
                if (!hasDigits) {
                    throw new IllegalArgumentException("must have either letters or numbers");
                }
                return ROW;
            }
        }
    }
}
