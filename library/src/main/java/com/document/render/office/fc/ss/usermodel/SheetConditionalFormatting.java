

package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;


public interface SheetConditionalFormatting {


    int addConditionalFormatting(HSSFCellRangeAddress[] regions,
                                 ConditionalFormattingRule rule);


    int addConditionalFormatting(HSSFCellRangeAddress[] regions,
                                 ConditionalFormattingRule rule1,
                                 ConditionalFormattingRule rule2);


    int addConditionalFormatting(HSSFCellRangeAddress[] regions, ConditionalFormattingRule[] cfRules);


    int addConditionalFormatting(ConditionalFormatting cf);


    ConditionalFormattingRule createConditionalFormattingRule(
            byte comparisonOperation,
            String formula1,
            String formula2);


    ConditionalFormattingRule createConditionalFormattingRule(
            byte comparisonOperation,
            String formula);


    ConditionalFormattingRule createConditionalFormattingRule(String formula);


    ConditionalFormatting getConditionalFormattingAt(int index);


    int getNumConditionalFormattings();


    void removeConditionalFormatting(int index);
}
