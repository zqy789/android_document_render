

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.CFRuleRecord;
import com.document.render.office.fc.hssf.record.aggregates.CFRecordsAggregate;
import com.document.render.office.fc.hssf.record.aggregates.ConditionalFormattingTable;
import com.document.render.office.fc.ss.SpreadsheetVersion;
import com.document.render.office.fc.ss.usermodel.ConditionalFormatting;
import com.document.render.office.fc.ss.usermodel.ConditionalFormattingRule;
import com.document.render.office.fc.ss.usermodel.SheetConditionalFormatting;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.ss.util.Region;



public final class HSSFSheetConditionalFormatting implements SheetConditionalFormatting {

    private final HSSFSheet _sheet;
    private final ConditionalFormattingTable _conditionalFormattingTable;

     HSSFSheetConditionalFormatting(HSSFSheet sheet) {
        _sheet = sheet;
        _conditionalFormattingTable = sheet.getSheet().getConditionalFormattingTable();
    }


    public HSSFConditionalFormattingRule createConditionalFormattingRule(
            byte comparisonOperation,
            String formula1,
            String formula2) {




        return null;
    }

    public HSSFConditionalFormattingRule createConditionalFormattingRule(
            byte comparisonOperation,
            String formula1) {




        return null;
    }


    public HSSFConditionalFormattingRule createConditionalFormattingRule(String formula) {
        HSSFWorkbook wb = _sheet.getWorkbook();


        return null;
    }


    public int addConditionalFormatting(HSSFConditionalFormatting cf) {
        CFRecordsAggregate cfraClone = cf.getCFRecordsAggregate().cloneCFAggregate();

        return _conditionalFormattingTable.add(cfraClone);
    }

    public int addConditionalFormatting(ConditionalFormatting cf) {
        return addConditionalFormatting((HSSFConditionalFormatting) cf);
    }


    public int addConditionalFormatting(Region[] regions, HSSFConditionalFormattingRule[] cfRules) {
        return addConditionalFormatting(Region.convertRegionsToCellRanges(regions), cfRules);
    }


    public int addConditionalFormatting(HSSFCellRangeAddress[] regions, HSSFConditionalFormattingRule[] cfRules) {
        if (regions == null) {
            throw new IllegalArgumentException("regions must not be null");
        }
        for (HSSFCellRangeAddress range : regions) range.validate(SpreadsheetVersion.EXCEL97);

        if (cfRules == null) {
            throw new IllegalArgumentException("cfRules must not be null");
        }
        if (cfRules.length == 0) {
            throw new IllegalArgumentException("cfRules must not be empty");
        }
        if (cfRules.length > 3) {
            throw new IllegalArgumentException("Number of rules must not exceed 3");
        }

        CFRuleRecord[] rules = new CFRuleRecord[cfRules.length];
        for (int i = 0; i != cfRules.length; i++) {
            rules[i] = cfRules[i].getCfRuleRecord();
        }
        CFRecordsAggregate cfra = new CFRecordsAggregate(regions, rules);
        return _conditionalFormattingTable.add(cfra);
    }

    public int addConditionalFormatting(HSSFCellRangeAddress[] regions, ConditionalFormattingRule[] cfRules) {
        HSSFConditionalFormattingRule[] hfRules;
        if (cfRules instanceof HSSFConditionalFormattingRule[])
            hfRules = (HSSFConditionalFormattingRule[]) cfRules;
        else {
            hfRules = new HSSFConditionalFormattingRule[cfRules.length];
            System.arraycopy(cfRules, 0, hfRules, 0, hfRules.length);
        }
        return addConditionalFormatting(regions, hfRules);
    }

    public int addConditionalFormatting(HSSFCellRangeAddress[] regions,
                                        HSSFConditionalFormattingRule rule1) {
        return addConditionalFormatting(regions,
                rule1 == null ? null : new HSSFConditionalFormattingRule[]
                        {
                                rule1
                        });
    }

    public int addConditionalFormatting(HSSFCellRangeAddress[] regions,
                                        ConditionalFormattingRule rule1) {
        return addConditionalFormatting(regions, (HSSFConditionalFormattingRule) rule1);
    }

    public int addConditionalFormatting(HSSFCellRangeAddress[] regions,
                                        HSSFConditionalFormattingRule rule1,
                                        HSSFConditionalFormattingRule rule2) {
        return addConditionalFormatting(regions,
                new HSSFConditionalFormattingRule[]
                        {
                                rule1, rule2
                        });
    }

    public int addConditionalFormatting(HSSFCellRangeAddress[] regions,
                                        ConditionalFormattingRule rule1,
                                        ConditionalFormattingRule rule2) {
        return addConditionalFormatting(regions,
                (HSSFConditionalFormattingRule) rule1,
                (HSSFConditionalFormattingRule) rule2
        );
    }


    public HSSFConditionalFormatting getConditionalFormattingAt(int index) {
        CFRecordsAggregate cf = _conditionalFormattingTable.get(index);
        if (cf == null) {
            return null;
        }
        return new HSSFConditionalFormatting(_sheet.getWorkbook(), cf);
    }


    public int getNumConditionalFormattings() {
        return _conditionalFormattingTable.size();
    }


    public void removeConditionalFormatting(int index) {
        _conditionalFormattingTable.remove(index);
    }
}
