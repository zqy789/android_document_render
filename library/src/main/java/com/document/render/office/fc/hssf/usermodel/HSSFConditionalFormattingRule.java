

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.record.CFRuleRecord;
import com.document.render.office.fc.hssf.record.CFRuleRecord.ComparisonOperator;
import com.document.render.office.fc.hssf.record.cf.BorderFormatting;
import com.document.render.office.fc.hssf.record.cf.FontFormatting;
import com.document.render.office.fc.hssf.record.cf.PatternFormatting;
import com.document.render.office.fc.ss.usermodel.ConditionalFormattingRule;



public final class HSSFConditionalFormattingRule implements ConditionalFormattingRule {
    private static final byte CELL_COMPARISON = CFRuleRecord.CONDITION_TYPE_CELL_VALUE_IS;

    private final CFRuleRecord cfRuleRecord;
    private final HSSFWorkbook workbook;

    HSSFConditionalFormattingRule(HSSFWorkbook pWorkbook, CFRuleRecord pRuleRecord) {
        if (pWorkbook == null) {
            throw new IllegalArgumentException("pWorkbook must not be null");
        }
        if (pRuleRecord == null) {
            throw new IllegalArgumentException("pRuleRecord must not be null");
        }
        workbook = pWorkbook;
        cfRuleRecord = pRuleRecord;
    }

    CFRuleRecord getCfRuleRecord() {
        return cfRuleRecord;
    }

    private HSSFFontFormatting getFontFormatting(boolean create) {
        FontFormatting fontFormatting = cfRuleRecord.getFontFormatting();
        if (fontFormatting != null) {
            cfRuleRecord.setFontFormatting(fontFormatting);
            return new HSSFFontFormatting(cfRuleRecord);
        } else if (create) {
            fontFormatting = new FontFormatting();
            cfRuleRecord.setFontFormatting(fontFormatting);
            return new HSSFFontFormatting(cfRuleRecord);
        } else {
            return null;
        }
    }


    public HSSFFontFormatting getFontFormatting() {
        return getFontFormatting(false);
    }


    public HSSFFontFormatting createFontFormatting() {
        return getFontFormatting(true);
    }

    private HSSFBorderFormatting getBorderFormatting(boolean create) {
        BorderFormatting borderFormatting = cfRuleRecord.getBorderFormatting();
        if (borderFormatting != null) {
            cfRuleRecord.setBorderFormatting(borderFormatting);
            return new HSSFBorderFormatting(cfRuleRecord);
        } else if (create) {
            borderFormatting = new BorderFormatting();
            cfRuleRecord.setBorderFormatting(borderFormatting);
            return new HSSFBorderFormatting(cfRuleRecord);
        } else {
            return null;
        }
    }


    public HSSFBorderFormatting getBorderFormatting() {
        return getBorderFormatting(false);
    }


    public HSSFBorderFormatting createBorderFormatting() {
        return getBorderFormatting(true);
    }

    private HSSFPatternFormatting getPatternFormatting(boolean create) {
        PatternFormatting patternFormatting = cfRuleRecord.getPatternFormatting();
        if (patternFormatting != null) {
            cfRuleRecord.setPatternFormatting(patternFormatting);
            return new HSSFPatternFormatting(cfRuleRecord);
        } else if (create) {
            patternFormatting = new PatternFormatting();
            cfRuleRecord.setPatternFormatting(patternFormatting);
            return new HSSFPatternFormatting(cfRuleRecord);
        } else {
            return null;
        }
    }


    public HSSFPatternFormatting getPatternFormatting() {
        return getPatternFormatting(false);
    }


    public HSSFPatternFormatting createPatternFormatting() {
        return getPatternFormatting(true);
    }


    public byte getConditionType() {
        return cfRuleRecord.getConditionType();
    }


    public byte getComparisonOperation() {
        return cfRuleRecord.getComparisonOperation();
    }

    public String getFormula1() {
        return toFormulaString(cfRuleRecord.getParsedExpression1());
    }

    public String getFormula2() {
        byte conditionType = cfRuleRecord.getConditionType();
        if (conditionType == CELL_COMPARISON) {
            byte comparisonOperation = cfRuleRecord.getComparisonOperation();
            switch (comparisonOperation) {
                case ComparisonOperator.BETWEEN:
                case ComparisonOperator.NOT_BETWEEN:
                    return toFormulaString(cfRuleRecord.getParsedExpression2());
            }
        }
        return null;
    }

    private String toFormulaString(Ptg[] parsedExpression) {




        return null;
    }
}
