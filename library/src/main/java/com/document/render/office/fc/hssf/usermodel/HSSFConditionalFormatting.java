
package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.CFRuleRecord;
import com.document.render.office.fc.hssf.record.aggregates.CFRecordsAggregate;
import com.document.render.office.fc.ss.usermodel.ConditionalFormatting;
import com.document.render.office.fc.ss.usermodel.ConditionalFormattingRule;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.ss.util.Region;



public final class HSSFConditionalFormatting implements ConditionalFormatting {
    private final HSSFWorkbook _workbook;
    private final CFRecordsAggregate cfAggregate;

    HSSFConditionalFormatting(HSSFWorkbook workbook, CFRecordsAggregate cfAggregate) {
        if (workbook == null) {
            throw new IllegalArgumentException("workbook must not be null");
        }
        if (cfAggregate == null) {
            throw new IllegalArgumentException("cfAggregate must not be null");
        }
        _workbook = workbook;
        this.cfAggregate = cfAggregate;
    }

    CFRecordsAggregate getCFRecordsAggregate() {
        return cfAggregate;
    }


    public Region[] getFormattingRegions() {
        HSSFCellRangeAddress[] cellRanges = getFormattingRanges();
        return Region.convertCellRangesToRegions(cellRanges);
    }


    public HSSFCellRangeAddress[] getFormattingRanges() {
        return cfAggregate.getHeader().getCellRanges();
    }


    public void setRule(int idx, HSSFConditionalFormattingRule cfRule) {
        cfAggregate.setRule(idx, cfRule.getCfRuleRecord());
    }

    public void setRule(int idx, ConditionalFormattingRule cfRule) {
        setRule(idx, (HSSFConditionalFormattingRule) cfRule);
    }


    public void addRule(HSSFConditionalFormattingRule cfRule) {
        cfAggregate.addRule(cfRule.getCfRuleRecord());
    }

    public void addRule(ConditionalFormattingRule cfRule) {
        addRule((HSSFConditionalFormattingRule) cfRule);
    }


    public HSSFConditionalFormattingRule getRule(int idx) {
        CFRuleRecord ruleRecord = cfAggregate.getRule(idx);
        return new HSSFConditionalFormattingRule(_workbook, ruleRecord);
    }


    public int getNumberOfRules() {
        return cfAggregate.getNumberOfRules();
    }

    public String toString() {
        return cfAggregate.toString();
    }
}
