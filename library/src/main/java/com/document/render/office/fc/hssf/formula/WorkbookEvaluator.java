

package com.document.render.office.fc.hssf.formula;

import com.document.render.office.fc.hssf.formula.CollaboratingWorkbooksEnvironment.WorkbookNotFoundException;
import com.document.render.office.fc.hssf.formula.eval.AreaEval;
import com.document.render.office.fc.hssf.formula.eval.BlankEval;
import com.document.render.office.fc.hssf.formula.eval.BoolEval;
import com.document.render.office.fc.hssf.formula.eval.ErrorEval;
import com.document.render.office.fc.hssf.formula.eval.EvaluationException;
import com.document.render.office.fc.hssf.formula.eval.MissingArgEval;
import com.document.render.office.fc.hssf.formula.eval.NameEval;
import com.document.render.office.fc.hssf.formula.eval.NotImplementedException;
import com.document.render.office.fc.hssf.formula.eval.NumberEval;
import com.document.render.office.fc.hssf.formula.eval.OperandResolver;
import com.document.render.office.fc.hssf.formula.eval.RefEval;
import com.document.render.office.fc.hssf.formula.eval.StringEval;
import com.document.render.office.fc.hssf.formula.eval.ValueEval;
import com.document.render.office.fc.hssf.formula.function.Choose;
import com.document.render.office.fc.hssf.formula.function.FreeRefFunction;
import com.document.render.office.fc.hssf.formula.function.IfFunc;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.AttrPtg;
import com.document.render.office.fc.hssf.formula.ptg.BoolPtg;
import com.document.render.office.fc.hssf.formula.ptg.ControlPtg;
import com.document.render.office.fc.hssf.formula.ptg.DeletedArea3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.DeletedRef3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.ErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.ExpPtg;
import com.document.render.office.fc.hssf.formula.ptg.FuncVarPtg;
import com.document.render.office.fc.hssf.formula.ptg.IntPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemAreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.MemFuncPtg;
import com.document.render.office.fc.hssf.formula.ptg.MissingArgPtg;
import com.document.render.office.fc.hssf.formula.ptg.NamePtg;
import com.document.render.office.fc.hssf.formula.ptg.NameXPtg;
import com.document.render.office.fc.hssf.formula.ptg.NumberPtg;
import com.document.render.office.fc.hssf.formula.ptg.OperationPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.RefErrorPtg;
import com.document.render.office.fc.hssf.formula.ptg.RefPtg;
import com.document.render.office.fc.hssf.formula.ptg.StringPtg;
import com.document.render.office.fc.hssf.formula.ptg.UnionPtg;
import com.document.render.office.fc.hssf.formula.ptg.UnknownPtg;
import com.document.render.office.fc.hssf.formula.udf.AggregatingUDFFinder;
import com.document.render.office.fc.hssf.formula.udf.UDFFinder;
import com.document.render.office.fc.hssf.usermodel.HSSFEvaluationWorkbook;
import com.document.render.office.fc.ss.usermodel.ICell;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.ss.model.XLSModel.ACell;
import com.document.render.office.ss.model.baseModel.Cell;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public final class WorkbookEvaluator {

    private final EvaluationWorkbook _workbook;
    private final IEvaluationListener _evaluationListener;
    private final Map<EvaluationSheet, Integer> _sheetIndexesBySheet;
    private final Map<String, Integer> _sheetIndexesByName;
    private final IStabilityClassifier _stabilityClassifier;
    private final AggregatingUDFFinder _udfFinder;
    private EvaluationCache _cache;

    private int _workbookIx;
    private CollaboratingWorkbooksEnvironment _collaboratingWorkbookEnvironment;
    private EvaluationTracker tracker;


    public WorkbookEvaluator(EvaluationWorkbook workbook, IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        this(workbook, null, stabilityClassifier, udfFinder);
    }

     WorkbookEvaluator(EvaluationWorkbook workbook, IEvaluationListener evaluationListener,
                                    IStabilityClassifier stabilityClassifier, UDFFinder udfFinder) {
        _workbook = workbook;
        _evaluationListener = evaluationListener;
        _cache = new EvaluationCache(evaluationListener);
        _sheetIndexesBySheet = new IdentityHashMap<EvaluationSheet, Integer>();
        _sheetIndexesByName = new IdentityHashMap<String, Integer>();
        _collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
        _workbookIx = 0;
        _stabilityClassifier = stabilityClassifier;

        AggregatingUDFFinder defaultToolkit =
                workbook == null ? null : (AggregatingUDFFinder) workbook.getUDFFinder();
        if (defaultToolkit != null && udfFinder != null) {
            defaultToolkit.add(udfFinder);
        }
        _udfFinder = defaultToolkit;
    }

    private static boolean isDebugLogEnabled() {
        return false;
    }

    private static void logDebug(String s) {
        if (isDebugLogEnabled()) {
            System.out.println(s);
        }
    }



    static ValueEval getValueFromNonFormulaCell(EvaluationCell cell) {
        if (cell == null) {
            return BlankEval.instance;
        }
        int cellType = cell.getCellType();
        switch (cellType) {
            case ICell.CELL_TYPE_NUMERIC:
                return new NumberEval(cell.getNumericCellValue());
            case ICell.CELL_TYPE_STRING:
                return new StringEval(cell.getStringCellValue());
            case ICell.CELL_TYPE_BOOLEAN:
                return BoolEval.valueOf(cell.getBooleanCellValue());
            case ICell.CELL_TYPE_BLANK:
                return BlankEval.instance;
            case ICell.CELL_TYPE_ERROR:
                return ErrorEval.valueOf(cell.getErrorCellValue());
        }
        throw new RuntimeException("Unexpected cell type (" + cellType + ")");
    }


    private static int countTokensToBeSkipped(Ptg[] ptgs, int startIndex, int distInBytes) {
        int remBytes = distInBytes;
        int index = startIndex;
        while (remBytes != 0) {
            index++;
            remBytes -= ptgs[index].getSize();
            if (remBytes < 0) {
                throw new RuntimeException("Bad skip distance (wrong token size calculation).");
            }
            if (index >= ptgs.length) {
                throw new RuntimeException("Skip distance too far (ran out of formula tokens).");
            }
        }
        return index - startIndex;
    }


    public static ValueEval dereferenceResult(ValueEval evaluationResult, int srcRowNum, int srcColNum) {
        ValueEval value;
        try {
            value = OperandResolver.getSingleValue(evaluationResult, srcRowNum, srcColNum);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (value == BlankEval.instance) {

            return NumberEval.ZERO;


        }
        return value;
    }


     String getSheetName(int sheetIndex) {
        return _workbook.getSheetName(sheetIndex);
    }

     EvaluationSheet getSheet(int sheetIndex) {
        Iterator<EvaluationSheet> iter = _sheetIndexesBySheet.keySet().iterator();
        while (iter.hasNext()) {
            EvaluationSheet sheet = (EvaluationSheet) iter.next();
            if (_sheetIndexesBySheet.get(sheet) == sheetIndex) {
                return sheet;
            }
        }
        EvaluationSheet sheet = _workbook.getSheet(sheetIndex);
        _sheetIndexesBySheet.put(sheet, sheetIndex);
        return sheet;
    }

     EvaluationWorkbook getWorkbook() {
        return _workbook;
    }

     EvaluationName getName(String name, int sheetIndex) {
        NamePtg namePtg = _workbook.getName(name, sheetIndex).createPtg();

        if (namePtg == null) {
            return null;
        } else {
            return _workbook.getName(namePtg);
        }
    }

     void attachToEnvironment(CollaboratingWorkbooksEnvironment collaboratingWorkbooksEnvironment, EvaluationCache cache, int workbookIx) {
        _collaboratingWorkbookEnvironment = collaboratingWorkbooksEnvironment;
        _cache = cache;
        _workbookIx = workbookIx;
    }

     CollaboratingWorkbooksEnvironment getEnvironment() {
        return _collaboratingWorkbookEnvironment;
    }


     void detachFromEnvironment() {
        _collaboratingWorkbookEnvironment = CollaboratingWorkbooksEnvironment.EMPTY;
        _cache = new EvaluationCache(_evaluationListener);
        _workbookIx = 0;
    }


     WorkbookEvaluator getOtherWorkbookEvaluator(String workbookName) throws WorkbookNotFoundException {
        return _collaboratingWorkbookEnvironment.getWorkbookEvaluator(workbookName);
    }

     IEvaluationListener getEvaluationListener() {
        return _evaluationListener;
    }


    public void clearAllCachedResultValues() {
        _cache.clear();
        _sheetIndexesBySheet.clear();
    }


    public void notifyUpdateCell(EvaluationCell cell) {
        int sheetIndex = getSheetIndex(cell.getSheet());
        _cache.notifyUpdateCell(_workbookIx, sheetIndex, cell);
    }


    public void notifyDeleteCell(EvaluationCell cell) {
        int sheetIndex = getSheetIndex(cell.getSheet());
        _cache.notifyDeleteCell(_workbookIx, sheetIndex, cell);
    }

    private int getSheetIndex(EvaluationSheet sheet) {
        Integer result = _sheetIndexesBySheet.get(sheet);
        if (result == null) {
            int sheetIndex = _workbook.getSheetIndex(sheet);
            if (sheetIndex < 0) {
                throw new RuntimeException("Specified sheet from a different book");
            }
            result = Integer.valueOf(sheetIndex);
            _sheetIndexesBySheet.put(sheet, result);
        }
        return result.intValue();
    }

    public ValueEval evaluate(EvaluationCell srcCell) {
        int sheetIndex = getSheetIndex(srcCell.getSheet());
        if (tracker == null) {
            tracker = new EvaluationTracker(_cache);
        }
        return evaluateAny(srcCell, sheetIndex, srcCell.getRowIndex(), srcCell.getColumnIndex(), tracker);
    }


     int getSheetIndex(String sheetName) {
        Integer result = _sheetIndexesByName.get(sheetName);
        if (result == null) {
            int sheetIndex = _workbook.getSheetIndex(sheetName);
            if (sheetIndex < 0) {
                return -1;
            }
            result = Integer.valueOf(sheetIndex);
            _sheetIndexesByName.put(sheetName, result);
        }
        return result.intValue();
    }

     int getSheetIndexByExternIndex(int externSheetIndex) {
        return _workbook.convertFromExternSheetIndex(externSheetIndex);
    }


    private ValueEval evaluateAny(EvaluationCell srcCell, int sheetIndex,
                                  int rowIndex, int columnIndex, EvaluationTracker tracker) {


        boolean shouldCellDependencyBeRecorded = _stabilityClassifier == null ? true
                : !_stabilityClassifier.isCellFinal(sheetIndex, rowIndex, columnIndex);
        if (srcCell == null || srcCell.getCellType() != ICell.CELL_TYPE_FORMULA) {
            ValueEval result = getValueFromNonFormulaCell(srcCell);
            if (shouldCellDependencyBeRecorded) {
                tracker.acceptPlainValueDependency(_workbookIx, sheetIndex, rowIndex, columnIndex, result);
            }
            return result;
        }

        FormulaCellCacheEntry cce = _cache.getOrCreateFormulaCellEntry(srcCell);
        if (shouldCellDependencyBeRecorded || cce.isInputSensitive()) {
            tracker.acceptFormulaDependency(cce);
        }
        IEvaluationListener evalListener = _evaluationListener;
        ValueEval result;
        if (cce.getValue() == null) {
            if (!tracker.startEvaluate(cce)) {
                return ErrorEval.CIRCULAR_REF_ERROR;
            }
            OperationEvaluationContext ec = new OperationEvaluationContext(this, _workbook, sheetIndex, rowIndex, columnIndex, tracker);

            try {

                Ptg[] ptgs = _workbook.getFormulaTokens(srcCell);
                if (evalListener == null) {
                    result = evaluateFormula(ec, ptgs);
                    if (result == null) {
                        return null;
                    }

                } else {
                    evalListener.onStartEvaluate(srcCell, cce);
                    result = evaluateFormula(ec, ptgs);
                    evalListener.onEndEvaluate(cce, result);
                }

                tracker.updateCacheResult(result);
            } catch (NotImplementedException e) {
                throw addExceptionInfo(e, sheetIndex, rowIndex, columnIndex);
            } catch (Exception e) {
                ACell cell = (ACell) srcCell.getIdentityKey();
                cell.setCellType(Cell.CELL_TYPE_ERROR, false);
                cell.setCellErrorValue((byte) ErrorEval.NA.getErrorCode());
                return null;
            } finally {
                tracker.endEvaluate(cce);
            }
        } else {
            if (evalListener != null) {
                evalListener.onCacheHit(sheetIndex, rowIndex, columnIndex, cce.getValue());
            }
            return cce.getValue();
        }
        if (isDebugLogEnabled()) {
            String sheetName = getSheetName(sheetIndex);
            CellReference cr = new CellReference(rowIndex, columnIndex);
            logDebug("Evaluated " + sheetName + "!" + cr.formatAsString() + " to " + result.toString());
        }





        ACell cell = (ACell) srcCell.getIdentityKey();
        if (result instanceof NumberEval) {
            NumberEval ne = (NumberEval) result;
            cell.setCellType(Cell.CELL_TYPE_NUMERIC, false);
            cell.setCellValue(ne.getNumberValue());
        } else if (result instanceof BoolEval) {
            BoolEval be = (BoolEval) result;
            cell.setCellType(Cell.CELL_TYPE_BOOLEAN, false);
            cell.setCellValue(be.getBooleanValue());
        } else if (result instanceof StringEval) {
            StringEval ne = (StringEval) result;
            cell.setCellType(Cell.CELL_TYPE_STRING, false);
            cell.setCellValue(ne.getStringValue());
        } else if (result instanceof ErrorEval) {
            cell.setCellType(Cell.CELL_TYPE_ERROR, false);
            cell.setCellErrorValue((byte) ((ErrorEval) result).getErrorCode());

        }
        return result;
    }


    private NotImplementedException addExceptionInfo(NotImplementedException inner, int sheetIndex, int rowIndex, int columnIndex) {

        try {
            String sheetName = _workbook.getSheetName(sheetIndex);
            CellReference cr = new CellReference(sheetName, rowIndex, columnIndex, false, false);
            String msg = "Error evaluating cell " + cr.formatAsString();
            return new NotImplementedException(msg, inner);
        } catch (Exception e) {

            e.printStackTrace();
            return inner;
        }
    }


     ValueEval evaluateFormula(OperationEvaluationContext ec, Ptg[] ptgs) {

        List<ValueEval> stack = new ArrayList<ValueEval>();

        for (int i = 0, iSize = ptgs.length; i < iSize; i++) {


            Ptg ptg = ptgs[i];
            if (ptg instanceof AttrPtg) {
                AttrPtg attrPtg = (AttrPtg) ptg;
                if (attrPtg.isSum()) {


                    ptg = FuncVarPtg.SUM;
                }
                if (attrPtg.isOptimizedChoose()) {
                    ValueEval arg0 = stack.remove(stack.size() - 1);
                    int[] jumpTable = attrPtg.getJumpTable();
                    int dist;
                    int nChoices = jumpTable.length;
                    try {
                        int switchIndex = Choose.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
                        if (switchIndex < 1 || switchIndex > nChoices) {
                            stack.add(ErrorEval.VALUE_INVALID);
                            dist = attrPtg.getChooseFuncOffset() + 4;
                        } else {
                            dist = jumpTable[switchIndex - 1];
                        }
                    } catch (EvaluationException e) {
                        stack.add(e.getErrorEval());
                        dist = attrPtg.getChooseFuncOffset() + 4;
                    }


                    dist -= nChoices * 2 + 2;
                    i += countTokensToBeSkipped(ptgs, i, dist);
                    continue;
                }
                if (attrPtg.isOptimizedIf()) {
                    ValueEval arg0 = stack.remove(stack.size() - 1);
                    boolean evaluatedPredicate;
                    try {
                        evaluatedPredicate = IfFunc.evaluateFirstArg(arg0, ec.getRowIndex(), ec.getColumnIndex());
                    } catch (EvaluationException e) {
                        stack.add(e.getErrorEval());
                        int dist = attrPtg.getData();
                        i += countTokensToBeSkipped(ptgs, i, dist);
                        attrPtg = (AttrPtg) ptgs[i];
                        dist = attrPtg.getData() + 1;
                        i += countTokensToBeSkipped(ptgs, i, dist);
                        continue;
                    }
                    if (evaluatedPredicate) {

                    } else {
                        int dist = attrPtg.getData();
                        i += countTokensToBeSkipped(ptgs, i, dist);
                        Ptg nextPtg = ptgs[i + 1];
                        if (ptgs[i] instanceof AttrPtg && nextPtg instanceof FuncVarPtg) {

                            i++;
                            stack.add(BoolEval.FALSE);
                        }
                    }
                    continue;
                }
                if (attrPtg.isSkip()) {
                    int dist = attrPtg.getData() + 1;
                    i += countTokensToBeSkipped(ptgs, i, dist);
                    if (stack.get(stack.size() - 1) == MissingArgEval.instance) {
                        stack.remove(stack.size() - 1);
                        stack.add(BlankEval.instance);
                    }
                    continue;
                }
            }
            if (ptg instanceof ControlPtg) {

                continue;
            }
            if (ptg instanceof MemFuncPtg || ptg instanceof MemAreaPtg) {

                continue;
            }
            if (ptg instanceof MemErrPtg) {
                continue;
            }

            ValueEval opResult;
            if (ptg instanceof OperationPtg) {
                OperationPtg optg = (OperationPtg) ptg;

                if (optg instanceof UnionPtg) {
                    continue;
                }


                int numops = optg.getNumberOfOperands();
                ValueEval[] ops = new ValueEval[numops];


                for (int j = numops - 1; j >= 0; j--) {
                    ValueEval p = stack.remove(stack.size() - 1);
                    ;
                    ops[j] = p;
                }

                opResult = OperationEvaluatorFactory.evaluate(optg, ops, ec);
            } else {
                opResult = getEvalForPtg(ptg, ec);
            }
            if (opResult == null) {
                return null;

            }

            stack.add(opResult);
        }

        ValueEval value = stack.remove(stack.size() - 1);
        ;
        if (!stack.isEmpty()) {
            throw new IllegalStateException("evaluation stack not empty");
        }

        if (value instanceof AreaEval || value instanceof RefEval) {
            return value;
        }

        return dereferenceResult(value, ec.getRowIndex(), ec.getColumnIndex());
    }


    private ValueEval getEvalForPtg(Ptg ptg, OperationEvaluationContext ec) {


        if (ptg instanceof NamePtg) {

            NamePtg namePtg = (NamePtg) ptg;
            EvaluationName nameRecord = _workbook.getName(namePtg);
            if (nameRecord.isFunctionName()) {
                return new NameEval(nameRecord.getNameText());
            }
            if (nameRecord.hasFormula()) {
                return evaluateNameFormula(nameRecord.getNameDefinition(), ec);
            }

            throw new RuntimeException("Don't now how to evalate name '" + nameRecord.getNameText() + "'");
        }
        if (ptg instanceof NameXPtg) {
            NameXPtg nameXPtg = (NameXPtg) ptg;
            EvaluationName nameRecord = ((HSSFEvaluationWorkbook) _workbook).getName(nameXPtg);
            if (nameRecord.isFunctionName()) {
                return new NameEval(nameRecord.getNameText());
            }
            if (nameRecord.hasFormula()) {
                return evaluateNameFormula(nameRecord.getNameDefinition(), ec);
            }

            throw new RuntimeException("Don't now how to evalate name '" + nameRecord.getNameText() + "'");


        }

        if (ptg instanceof IntPtg) {
            return new NumberEval(((IntPtg) ptg).getValue());
        }
        if (ptg instanceof NumberPtg) {
            return new NumberEval(((NumberPtg) ptg).getValue());
        }
        if (ptg instanceof StringPtg) {
            return new StringEval(((StringPtg) ptg).getValue());
        }
        if (ptg instanceof BoolPtg) {
            return BoolEval.valueOf(((BoolPtg) ptg).getValue());
        }
        if (ptg instanceof ErrPtg) {
            return ErrorEval.valueOf(((ErrPtg) ptg).getErrorCode());
        }
        if (ptg instanceof MissingArgPtg) {
            return MissingArgEval.instance;
        }
        if (ptg instanceof AreaErrPtg || ptg instanceof RefErrorPtg
                || ptg instanceof DeletedArea3DPtg || ptg instanceof DeletedRef3DPtg) {
            return ErrorEval.REF_INVALID;
        }
        if (ptg instanceof Ref3DPtg) {
            Ref3DPtg rptg = (Ref3DPtg) ptg;
            return ec.getRef3DEval(rptg.getRow(), rptg.getColumn(), rptg.getExternSheetIndex());
        }
        if (ptg instanceof Area3DPtg) {
            Area3DPtg aptg = (Area3DPtg) ptg;
            return ec.getArea3DEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn(), aptg.getExternSheetIndex());
        }
        if (ptg instanceof RefPtg) {
            RefPtg rptg = (RefPtg) ptg;
            return ec.getRefEval(rptg.getRow(), rptg.getColumn());
        }
        if (ptg instanceof AreaPtg) {
            AreaPtg aptg = (AreaPtg) ptg;
            return ec.getAreaEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn());
        }

        if (ptg instanceof UnknownPtg) {



            throw new RuntimeException("UnknownPtg not allowed");
        }
        if (ptg instanceof ExpPtg) {


            throw new RuntimeException("ExpPtg currently not supported");
        }

        throw new RuntimeException("Unexpected ptg class (" + ptg.getClass().getName() + ")");
    }


     ValueEval evaluateNameFormula(Ptg[] ptgs, OperationEvaluationContext ec) {




        if (ptgs.length == 1) {
            return getEvalForPtg(ptgs[0], ec);
        } else {
            return evaluateFormula(ec, ptgs);
        }

    }


     ValueEval evaluateReference(EvaluationSheet sheet, int sheetIndex, int rowIndex,
                                              int columnIndex, EvaluationTracker tracker) {

        EvaluationCell cell = sheet.getCell(rowIndex, columnIndex);
        return evaluateAny(cell, sheetIndex, rowIndex, columnIndex, tracker);
    }

    public FreeRefFunction findUserDefinedFunction(String functionName) {
        return _udfFinder.findFunction(functionName);
    }
}
